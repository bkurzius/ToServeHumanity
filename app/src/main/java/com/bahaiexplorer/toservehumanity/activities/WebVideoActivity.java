package com.bahaiexplorer.toservehumanity.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.bahaiexplorer.toservehumanity.ToServeHumanityApplication;
import com.bahaiexplorer.toservehumanity.model.Constants;
import com.bahaiexplorer.toservehumanity.model.VideoObject;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * Created by briankurzius on 2/8/14.
 */
public class WebVideoActivity extends Activity{
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
        ToServeHumanityApplication mApp = (ToServeHumanityApplication)getApplication();
        VideoObject vo = mApp.getVideoList().get(vidIndex);
        mWebView = new HTML5WebView(this);

        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        } else {
            mWebView.loadUrl(vo.streamingURL);
        }

        setContentView(mWebView.getLayout());
        // set analytics
        ((ToServeHumanityApplication)getApplication()).trackScreen(Constants.TRACK_SCREEN_STREAM_VIDEO);
        ((ToServeHumanityApplication)getApplication()).trackEvent
                (Constants
                        .TRACK_EVENT_TYPE_PLAY_VIDEO,vo.streamingURL);
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
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
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

    @Override
    public void onStart() {
        super.onStart();

        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }


}

