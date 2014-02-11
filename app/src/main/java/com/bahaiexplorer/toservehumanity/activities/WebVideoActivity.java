package com.bahaiexplorer.toservehumanity.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.bahaiexplorer.toservehumanity.R;
import com.bahaiexplorer.toservehumanity.activities.HTML5WebView;

/**
 * Created by briankurzius on 2/8/14.
 */
public class WebVideoActivity extends Activity{
    public static final String VIDEO_ID = "video_id";
    public static final String VIDEO_INDEX = "video_index";

    HTML5WebView mWebView;
    int vidIndex = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*  if (!UIUtils.isTablet(this)) {
            // this really should be handled in manifests, if possible (perry)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }*/
        if(getIntent().getExtras()!=null){
            vidIndex = getIntent().getExtras().getInt(VIDEO_INDEX);
        }

        // now get the id:
        String videoId = getResources().getStringArray(R.array.video_id_array)[vidIndex];
        mWebView = new HTML5WebView(this);

        if (savedInstanceState != null) {

            mWebView.restoreState(savedInstanceState);
        } else {
            mWebView.loadUrl("http://player.vimeo.com/video/" + videoId);
        }

        setContentView(mWebView.getLayout());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        //mWebView.stopLoading();
        mWebView.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.inCustomView()) {
                mWebView.hideCustomView();
                //  mWebView.goBack();
                //mWebView.goBack();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}

