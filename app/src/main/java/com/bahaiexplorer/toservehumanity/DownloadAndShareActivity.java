package com.bahaiexplorer.toservehumanity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by briankurzius on 2/8/14.
 */
public class DownloadAndShareActivity extends Activity {

    private static final String TAG = "DownloadAndShareActivity";

    private ToServeHumanityApplication mApp;
    private Context mContext;
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private String downloadFileName = "";
    private GridView mIconGridView;
    private TypedArray imgs;

    private static final String mFileSuffix = ".mp4";
    private static String mLanguage = "en";
    private static String mVideoSize = "standard";
    // File url to download
    private String mFileName = "";
    private String mFileStoragePath ="";

    private String mFileURL = "";



    //private static Integer[] icons_array =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (ToServeHumanityApplication)getApplication();
        setContentView(R.layout.activity_main);
        mContext = this;

        imgs = getResources().obtainTypedArray(R.array.icon_array);
        mIconGridView = (GridView)findViewById(R.id.gridViewIcons);
        Log.d("main", "imgs is:" + imgs);
        mIconGridView.setAdapter(new CustomGridAdapter(this, imgs));

    }

    private void requestDownload(int index){
        Log.d(TAG,"videoIndex: " + index);
        // getFile name
        downloadFileName = getResources().getStringArray(R.array.video_file_name_array)[index];
        Log.d(TAG,"downloadFileName: " + downloadFileName);
        mFileName = downloadFileName + "_" + mLanguage + "_" + mVideoSize  + mFileSuffix;

        // now check if the file has been saved already
        if(mApp.isVideoFileSaved(mContext, mFileName)){
            //its saved so you don;t need to again
            Toast.makeText(this,getResources().getString(R.string.title_already_downloaded),Toast.LENGTH_SHORT).show();
        }else{
            mFileStoragePath = mApp.getAlbumStorageDir(Constants.VIDEO_FOLDER).toString() + "/" + mFileName;
            mFileURL = Constants.DOWNLOAD_PATH + mFileName;
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
            Toast.makeText(mContext, getResources().getString(R.string.title_downloading_succeeded),Toast.LENGTH_SHORT).show();

        }
    }

    // Here is your custom Adapter

    public class CustomGridAdapter extends BaseAdapter {
        private Activity mContext;
        private TypedArray mItems;


        // Constructor
        public CustomGridAdapter(Activity activity, TypedArray items) {
            this.mContext = activity;
            mItems = items;

        }

        @Override
        public int getCount() {
            return mItems.length();
        }

        @Override
        public Object getItem(int position) {
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(mContext);
            ImageButton imageButton = new ImageButton(mContext);
            Drawable drawable = mItems.getDrawable(position);
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageButton.setImageDrawable(drawable);
            imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageButton.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageButton.setId(position);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("button clicked","num:");
                    //openVideo(((ImageButton)v).getId());
                    requestDownload(((ImageButton)v).getId());
                }
            });

            return imageButton;
        }

    }
}
