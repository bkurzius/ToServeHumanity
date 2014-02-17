package com.bahaiexplorer.toservehumanity.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bahaiexplorer.toservehumanity.R;
import com.bahaiexplorer.toservehumanity.ToServeHumanityApplication;
import com.bahaiexplorer.toservehumanity.model.Constants;
import com.bahaiexplorer.toservehumanity.model.VideoItem;
import com.bahaiexplorer.toservehumanity.model.VideoObject;
import com.bahaiexplorer.toservehumanity.util.ConnectionUtils;
import com.bahaiexplorer.toservehumanity.util.UIUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;



public class VideoDetailActivity extends BaseActivity {
    public static final String TAG = "VideoDetailActivity";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_LENGTH = "extra_length";
    public static final String EXTRA_DRAWABLE = "extra_drawable";
    public static final int MENU_ITEM_ID_SAVED = 1;
    private ProgressDialog pDialog;

    private int index;
    private VideoObject vo;
    private Context mContext;
    private String mFileStoragePath;

    private CountDownTimer mDeleteVideoTimer;
    private Button btnFacebook;
    private DownloadFileFromURL mDownloadTask;

    ToServeHumanityApplication mApp;
    static final int progress_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (ToServeHumanityApplication) getApplication();
        mContext = this;
        setContentView(R.layout.activity_video_detail);
        index = getIntent().getIntExtra(VideoItem.VIDEO_INDEX,0);
        vo = ((ToServeHumanityApplication) getApplication()).getVideoList().get(index);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(vo))
                    .commit();
        }
        getSupportActionBar().setTitle("Detail");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.video_detail, menu);
        // these are change on the fly so we can reset them based on the state
        int saveDrawable = R.drawable.ic_action_save;
        String saveTxt = mApp.currLanguageConfig.strings.titleSave;
        if(isVideoSaved()){
            saveDrawable = R.drawable.ic_action_saved;
            saveTxt = mApp.currLanguageConfig.strings.titleSaved;
        }
        MenuItem mi = menu.getItem(0);
        mi.setIcon(saveDrawable);
        mi.setTitle(saveTxt);
        /*MenuItemCompat savedItem = new MenuItemCompat();
        //savedItem.
        menu.add(0,MENU_ITEM_ID_SAVED,0,saveTxt)
                .setIcon(saveDrawable);
               // .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save) {
            requestDownload();
            return true;
        }else if (id == R.id.action_share){
            createShare();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isVideoSaved(){
        return vo.isSaved;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        VideoObject vo;

        public PlaceholderFragment() {
        }

        public PlaceholderFragment(VideoObject _vo) {
            vo = _vo;

        }
        @Override
        public void onCreate(Bundle save){
            super.onCreate(save);
            setRetainInstance(true);
        }


        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_video_detail, container, false);
            ToServeHumanityApplication mApp = (ToServeHumanityApplication)getActivity()
                    .getApplication();
            ImageView iv = (ImageView) rootView.findViewById(R.id.iv_video_icon);
            TextView tvTitle = (TextView)rootView.findViewById(R.id.tv_video_title);
            TextView tvLength = (TextView)rootView.findViewById(R.id.tv_video_length);
            TextView tvSize = (TextView)rootView.findViewById(R.id.tv_video_size);

            iv.setImageDrawable(vo.iconDrawable);
            tvTitle.setText(vo.title);
            tvLength.setText(vo.length);
            tvSize.setText(vo.downloadSize);

            Button btnFacebook = (Button) rootView.findViewById(R.id.btn_facebutton);
            String facebookLink = mApp.currLanguageConfig.strings.titleFacebookLink;
            if(facebookLink.isEmpty()){
                btnFacebook.setVisibility(View.GONE);
            }else{
                btnFacebook.setText(mApp.currLanguageConfig.strings.titleFacebookLink);
                btnFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String facebookURL = ((VideoDetailActivity)getActivity()).mApp
                                .currLanguageConfig.facebookPage;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(facebookURL));
                        startActivity(intent);
                    }
                });
            }

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((VideoDetailActivity)getActivity()).openVideo();
                }
            });




            return rootView;
        }
    }

  /*  public Intent getOpenFacebookIntent(String pId) {

        try {
            getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("facebook:/groups?album=0&photo=" +
                    pId+"&user="+ownerId));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"));
        }
    }
*/

    private void requestDownload(){
        Log.d(TAG," vo.downloadFileName: " + vo.downloadFileName);
        String mFileName = vo.downloadFileName;
        // check if the file has been saved already - if so warn them that they are going to
        // delete it
        if(mApp.isVideoFileSaved(mContext, mFileName)){
            showDeleteVideoWarningDialog();
        }else{
            // if this is the first time then show the dialog terms
            if(!mApp.getSeenTermsOfUse()){
                showTermsDialog();
            }else{
                startDownload();
            }

        }

    }

    private void startDownload(){
        String mFileName = vo.downloadFileName;
        mFileStoragePath = mApp.getAlbumStorageDir(Constants.VIDEO_FOLDER).toString() + "/" + mFileName;
        String mFileURL = Constants.DOWNLOAD_PATH + mFileName;
        mDownloadTask = new DownloadFileFromURL();
        mDownloadTask.execute(mFileURL);
    }

    private void deleteVideoFile(boolean showToast){
        Log.d(TAG," vo.downloadFileName: " + vo.downloadFileName);
        String mFileName = vo.downloadFileName;
        File file =  mApp.getSavedVideoFile(mContext, mFileName);
        boolean deleted = file.delete();
        if(deleted){
            vo.isSaved = false;
            if(showToast){
            Toast.makeText(this,mApp.currLanguageConfig.strings.titleFileDeleted,
                    Toast.LENGTH_SHORT).show();
            }
            supportInvalidateOptionsMenu();

        }
    }




    /**
     * Showing Dialog
     * */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage(mApp.getStrings().titleSaving);
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mDownloadTask.cancel(true);
                        deleteVideoFile(false);
                    }
                });
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }


    public void openVideo(){

        String mFileName = vo.downloadFileName;
        // now check if the file has been saved already
        if(mApp.isVideoFileSaved(mContext, mFileName)){
            Log.d(TAG,"file is saved - so play it");
                Intent intent = new Intent(this,VideoActivityGingerbread.class);
                intent.putExtra(VideoActivityGingerbread.FILE_NAME,mFileName);
                startActivity(intent);
        }else{
            if(UIUtils.isOSLessThanHoneycomb()){
                Toast.makeText(mContext,mApp.getStrings().textNeedToSave,Toast.LENGTH_LONG).show();
            }else{
                if(ConnectionUtils.isUsingCellularConnection(mContext) && mApp.remindOnCellularConnection()){
                    // show dialog to be sure that they want to stream
                   // Toast.makeText(mContext,"Are you sure you want to use cell data for this? You can download it instead.", Toast.LENGTH_LONG).show();
                    showConnectionWarningDialog();
                }else{
                    startStream();
                }
            }
        }
    }

    private void startStream(){
        Intent mVideoIntent = new Intent(this,WebVideoActivity.class);
        mVideoIntent.putExtra(WebVideoActivity.VIDEO_INDEX, index);
        this.startActivity(mVideoIntent);
    }


    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                OutputStream output = new FileOutputStream(mFileStoragePath);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            if(pDialog.isShowing()) dismissDialog(progress_bar_type);
            vo.isSaved = true;
            supportInvalidateOptionsMenu();
            Toast.makeText(mContext, getResources().getString(R.string.title_downloading_succeeded),Toast.LENGTH_SHORT).show();

        }
    }

    // -----------------------------------------------------
    // Dialogs
    // -----------------------------------------------------

    //TODO -- set up custom view to display this
    public void showConnectionWarningDialog(){
        Log.d(TAG, "showConnectionWarningDialog()");
        final String ok = getResources().getString(R.string.dialog_connection_warning_ok);
        final String dont_remind = getResources().getString(R.string.dialog_connection_warning_dont_remind_mew);
        final String cancel = getResources().getString(R.string.dialog_connection_warning_cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mApp.getStrings().alertCellularData)
                .setCancelable(false)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startStream();
                    }
                })
                .setNeutralButton(cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // don't do anything
                    }
                })
                .setNegativeButton(dont_remind, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Utils.logger(TAG, DIALOG_DONT_REMIND_ME);
                        mApp.setRemindOnCellularConnection();
                        startStream();
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();
    }

    //TODO -- set up custom view to display this
    public void showDeleteVideoWarningDialog(){
        Log.d(TAG, "showDeleteVideoWarningDialog()");
        final String ok = getResources().getString(R.string.dialog_delete_video_warning_ok);
        final String cancel = getResources().getString(R.string
                .dialog_delete_video_warning_cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mApp.getStrings().textSureYouWantToDelete)
                .setCancelable(false)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteVideoFile(true);
                    }
                })
                .setNeutralButton(cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // don't do anything
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();
    }


    public void showTermsDialog(){
        Log.d(TAG, "showTermsDialog()");
        final String ok = getResources().getString(R.string.dialog_connection_warning_ok);
        final String cancel = getResources().getString(R.string.dialog_connection_warning_cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mApp.currLanguageConfig.strings.textTerms)
                .setTitle(mApp.currLanguageConfig.strings.titleTerms)
                .setCancelable(false)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startDownload();
                        mApp.setSeenTermsOfUse(true);
                    }
                })
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // don't do anything
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void createShare(){
        String shareString = String.format(mApp.currLanguageConfig.strings.textShareBody,
                mApp.currLanguageConfig.projectName + ":" + vo.title, vo.shareURL,
                mApp.currLanguageConfig.appLink );
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                mApp.currLanguageConfig.strings.titleShareSubject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        mContext.startActivity(Intent.createChooser(sharingIntent,
                mApp.getStrings().titleShare));
    }


}
