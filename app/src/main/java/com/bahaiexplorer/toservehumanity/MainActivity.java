package com.bahaiexplorer.toservehumanity;

import android.app.Activity;
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

public class MainActivity extends ActionBarActivity {

    private TypedArray imgs;

    private GridView mIconGridView;

    //private static Integer[] icons_array =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            return imageButton;
        }

    }

}
