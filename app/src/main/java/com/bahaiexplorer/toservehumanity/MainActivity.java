package com.bahaiexplorer.toservehumanity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    private TypedArray imgs;
    private ToServeHumanityApplication mApp;
    private Context mContext;

    private GridView mIconGridView;

    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    // File url to download
    // javascript:selectDownload('twofold_moral_purpose_en_high.mp4');
    //private static String file_url = "http://downloadcdn1.bahai.org/toserve/building_a_new_civilization_en_standard.mp4";
    // 'http://downloadcdn1.bahai.org/toserve/' + file_name;
    //building_a_new_civilization_en_standard.mp4
    // http://downloadcdn1.bahai.org/toserve/building_a_new_civilization_en_standard.mp4



    //private static Integer[] icons_array =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApp = (ToServeHumanityApplication)getApplication();
        mContext = this;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        imgs = getResources().obtainTypedArray(R.array.icon_array);
        mIconGridView = (GridView)findViewById(R.id.gridViewIcons);
        Log.d("main", "imgs is:" + imgs);
        mIconGridView.setAdapter(new CustomGridAdapter(this, imgs));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_download_and_share) {
            openDownloadAndShare();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openVideo(int index){
        String downloadFileName = getResources().getStringArray(R.array.video_file_name_array)[index];
        Log.d(TAG,"downloadFileName: " + downloadFileName);
        String mFileName = downloadFileName + "_" + Constants.LANGUAGE + "_" + Constants.VIDEO_SIZE  + Constants.VIDEO_SUFFIX;
        // now check if the file has been saved already
        if(mApp.isVideoFileSaved(mContext, mFileName)){
            //its saved so get it and play it
            //File videoFile = mApp.getSavedVideoFile(this,mFileName);
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
            //String vidID = getResources().getStringArray(R.array.video_id_array)[index];
            Intent mVideoIntent = new Intent(this,WebVideoActivity.class);
            mVideoIntent.putExtra(WebVideoActivity.VIDEO_INDEX, index);
            this.startActivity(mVideoIntent);
        }


    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void openDownloadAndShare(){
        Intent intent = new Intent(this, DownloadAndShareActivity.class);

        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }



    // Here is your custom Adapter

    public class CustomGridAdapter extends BaseAdapter {
        private Activity mContext;
        private TypedArray mItems;


        // Constructor
        public CustomGridAdapter(MainActivity mainActivity, TypedArray items) {
            this.mContext = mainActivity;
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
                    openVideo(((ImageButton)v).getId());
                }
            });

            return imageButton;
        }

    }



}
