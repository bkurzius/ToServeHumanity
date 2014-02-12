package com.bahaiexplorer.toservehumanity.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bahaiexplorer.toservehumanity.R;
import com.bahaiexplorer.toservehumanity.ToServeHumanityApplication;
import com.bahaiexplorer.toservehumanity.model.Constants;
import com.bahaiexplorer.toservehumanity.model.VideoItem;
import com.bahaiexplorer.toservehumanity.util.ConnectionUtils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class VideoDetailActivity extends ActionBarActivity {
    public static final String TAG = "VideoDetailActivity";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_LENGTH = "extra_length";
    public static final String EXTRA_DRAWABLE = "extra_drawable";
    public static final int MENU_ITEM_ID_SAVED = 1;
    private ProgressDialog pDialog;

    private int index;
    private VideoItem vi;
    private String downloadFileName;
    private Context mContext;
    private String mFileStoragePath;

    ToServeHumanityApplication mApp;
    static final int progress_bar_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (ToServeHumanityApplication) getApplication();
        mContext = this;
        setContentView(R.layout.activity_video_detail);
        vi = ((ToServeHumanityApplication) getApplication()).getVideoList().get(getIntent().getIntExtra(VideoItem.VIDEO_INDEX,0));
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(vi))
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
        String saveTxt = "Save";
        if(isVideoSaved()){
            saveDrawable = R.drawable.ic_action_saved;
            saveTxt = "Saved";
        }
        menu.add(0,MENU_ITEM_ID_SAVED,0,saveTxt)
                .setIcon(saveDrawable)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == MENU_ITEM_ID_SAVED) {
            requestDownload();
            return true;
        }else if (id == R.id.action_share){
            createShare();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isVideoSaved(){
        return vi.isSaved;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        VideoItem vi;

        public PlaceholderFragment() {
        }

        public PlaceholderFragment(VideoItem _vi) {
            vi = _vi;

        }
        @Override
        public void onCreate(Bundle save){
            super.onCreate(save);
            setRetainInstance(true);
        }


        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_video_detail, container, false);

            ImageView iv = (ImageView) rootView.findViewById(R.id.iv_video_icon);
            TextView tvTitle = (TextView)rootView.findViewById(R.id.tv_video_title);
            TextView tvLength = (TextView)rootView.findViewById(R.id.tv_video_length);

            iv.setImageDrawable(vi.videoIconDrawable);
            tvTitle.setText(vi.videoTitle);
            tvLength.setText(vi.videoLength);

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((VideoDetailActivity)getActivity()).openVideo();
                }
            });



            return rootView;
        }
    }

    private void requestDownload(){
        // getFile name
        downloadFileName = vi.videoFileName;
        Log.d(TAG,"downloadFileName: " + downloadFileName);
        String mFileName = downloadFileName + "_" + vi.videoLanguage + "_" + Constants.VIDEO_SIZE  + Constants.VIDEO_SUFFIX;

        // now check if the file has been saved already
        if(mApp.isVideoFileSaved(mContext, mFileName)){
            //its saved so you don;t need to again
            Toast.makeText(this, getResources().getString(R.string.title_already_downloaded), Toast.LENGTH_SHORT).show();
        }else{
            mFileStoragePath = mApp.getAlbumStorageDir(Constants.VIDEO_FOLDER).toString() + "/" + mFileName;
            String mFileURL = Constants.DOWNLOAD_PATH + mFileName;
            new DownloadFileFromURL().execute(mFileURL);
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
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }


    public void openVideo(){

        String mFileName = vi.videoFileName + "_" + Constants.LANGUAGE + "_" + Constants.VIDEO_SIZE  + Constants.VIDEO_SUFFIX;
        // now check if the file has been saved already
        if(mApp.isVideoFileSaved(mContext, mFileName)){
            Log.d(TAG,"file is saved - s play it");
            //its saved so get it and play it
            Intent intent = new Intent(this,VideoActivity.class);
            intent.putExtra(VideoActivity.FILE_NAME,mFileName);
            startActivity(intent);
            /*FileInputStream fis;
            try{
                fis = new FileInputStream(videoFile);
                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(fis.getFD());
                fis.close();
                mp.prepare();
                mp.start();
            }catch (IOException e){
                Toast.makeText(this,"unknown video error", Toast.LENGTH_SHORT).show();
            }*/

        }else{
            Log.d(TAG,"file is NOT saved - so stream it");
            if(ConnectionUtils.isUsingCellularConnection(mContext) && mApp.remindOnCellularConnection()){
                // show dialog to be sure that they want to stream
               // Toast.makeText(mContext,"Are you sure you want to use cell data for this? You can download it instead.", Toast.LENGTH_LONG).show();
                showConnectionWarningDialog();
            }else{
                startStream();
            }
        }


    }

    private void startStream(){
        //String vidID = getResources().getStringArray(R.array.video_id_array)[index];
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
            dismissDialog(progress_bar_type);
            vi.isSaved = true;
            invalidateOptionsMenu();
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
        builder.setMessage(getResources().getString(R.string.dialog_connection_warning_message))
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

    private void createShare(){
        String shareString = String.format(getResources().getString(R.string.share_body), vi.videoTitle, vi.getVideoStreamURL());
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                getResources().getString(R.string.share_subject));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        mContext.startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_title)));
    }
    /**
     * Shares content
     *
     * @param context
     * @param shareSubject
     * @param shareBody
     */
    public static void shareContent(final Context context, String shareSubject,
                                    String shareBody) {


    }

}
