package com.bahaiexplorer.toservehumanity.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bahaiexplorer.toservehumanity.R;
import com.bahaiexplorer.toservehumanity.ToServeHumanityApplication;
import com.bahaiexplorer.toservehumanity.model.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    private ToServeHumanityApplication mApp;
    private Context mContext;

    private GridView mIconGridView;
    private ListView mIconListView;

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

        mIconListView = (ListView)findViewById(R.id.lv_video_items);

       ToServeHumanityApplication app = (ToServeHumanityApplication)getApplication();

        mIconListView.setAdapter(new CustomListAdapter(this, R.layout.list_item_view, app.getVideoList()));
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

    public class CustomListAdapter extends ArrayAdapter<VideoItem> {
        ArrayList<VideoItem> viList;
        int vView;

        // Constructor
        public CustomListAdapter(Context context, int layoutResourceId,
                                 List<VideoItem> objects) {
            super(context, layoutResourceId, objects);
            viList = (ArrayList<VideoItem>)objects;
            vView = layoutResourceId;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            View v  = li.inflate(vView, parent, false);
            VideoItem vi = viList.get(position);
            ImageView imageView = (ImageView) v.findViewById(R.id.iv_icon);
            TextView mTitle = (TextView)v.findViewById(R.id.tv_video_title);
            TextView mLength = (TextView)v.findViewById(R.id.tv_video_length);

            imageView.setImageDrawable(vi.videoIconDrawable);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            v.setId(position);

            mTitle.setText(vi.videoTitle);
            mLength.setText(vi.videoLength);

            // check to see if the file is saved and hide the icon if not
            ImageView savedIcon = (ImageView) v.findViewById(R.id.iv_saved);
            if(!vi.isSaved) savedIcon.setVisibility(View.GONE);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("button clicked","num:");
                    Intent i = new Intent(mContext, VideoDetailActivity.class);
                    i.putExtra(VideoItem.VIDEO_INDEX,v.getId());
                    startActivity(i);
                }
            });

            return v;
        }

    }




}
