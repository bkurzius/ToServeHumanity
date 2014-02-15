package com.bahaiexplorer.toservehumanity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bahaiexplorer.toservehumanity.model.Constants;
import com.bahaiexplorer.toservehumanity.model.VideoItem;
import com.bahaiexplorer.toservehumanity.util.JsonUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;



/**
 * Created by briankurzius on 2/8/14.
 */
public class ToServeHumanityApplication extends Application {
    final static String TAG = "ToServeHumanityApplication";
    final static String REMIND_CELLULAR_CONNECTION = "dont_remind_cellular_connection";
    public ArrayList<VideoItem> mVideoList = new ArrayList<VideoItem>();
    public TypedArray videoIconDrawables;
    private SharedPreferences prefs;
    private JSONObject jsonObj;

    private String configURL = "http://bahaiexplorer.com/toservehumanity/config.json";

    @Override
    public void onCreate() {
        super.onCreate();
        buildVideoArray();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    // build the videoArray so we don;t have to do it more than once
    private void buildVideoArray(){
        mVideoList = new ArrayList<VideoItem>();
        videoIconDrawables = getResources().obtainTypedArray(R.array.video_icon_array);

        String[] videoTitleArray = getResources().getStringArray(R.array.video_title_array);
        String[] videoIDArray = getResources().getStringArray(R.array.video_id_array);
        String[] videoFileNameArray = getResources().getStringArray(R.array.video_file_name_array);
        String[] videoIconDrawableArray = getResources().getStringArray(R.array.video_icon_array);

        for(int i =0; i<videoTitleArray.length;i++){
            String videoTitle = videoTitleArray[i];
            String videoID = videoIDArray[i];
            String videoFileName = videoFileNameArray[i];
            //String videoIconDrawable = videoIconDrawableArray[i];
            VideoItem vi = new VideoItem();
            vi.videoTitle = videoTitle;
            vi.videoID = videoID;
            vi.videoFileName = videoFileName;
            vi.videoLanguage = VideoItem.VIDEO_LANGUAGE_ENGLISH;
            vi.videoIconDrawable = (Drawable) videoIconDrawables.getDrawable(i);
            vi.videoLength = "60min";
            String filePath = vi.videoFileName + "_" +  vi.videoLanguage + "_" + Constants.VIDEO_SIZE  + Constants.VIDEO_SUFFIX;
            boolean isFileSaved = isVideoFileSaved(getApplicationContext(),filePath);
            vi.isSaved = isFileSaved;
            mVideoList.add(vi);
        }
        videoIconDrawables.recycle();

        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.getJSONfromURL(configURL);
    }

    public ArrayList<VideoItem> getVideoList(){
        return mVideoList;
    }

    static public boolean isVideoFileSaved(final Context context, String fileName){
        File[] files = getSavedFiles();
        if(files!=null){
            for(File file:files){
                Log.d(TAG, "file.getName(): " + file.getName());
                // now check if the fiel is the one we are looking for
                if(file.getName().indexOf(fileName)>-1){
                    return true;
                }
            }
        }
        return false;
    }


    static public File getSavedVideoFile(final Context context, String fileName){
        File[] files = getSavedFiles();
        for(File file:files){
            Log.d(TAG, "file.getName(): " + file.getName());
            // now check if the fiel is the one we are looking for
            if(file.getName().indexOf(fileName)>-1){
                return file;
            }
        }
        return null;
    }


    /* Checks if external storage is available for read and write */
    static public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    static public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    static public File[] getSavedFiles(){
        File[] files = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES + "/" + Constants.VIDEO_FOLDER).listFiles();
        return files;
    }



    static public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    public void setRemindOnCellularConnection(){
        //set a preference not to remind on this version
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(REMIND_CELLULAR_CONNECTION, false);
        edit.commit();
    }

    public boolean remindOnCellularConnection(){
        //set a preference not to remind on this version
       return prefs.getBoolean(REMIND_CELLULAR_CONNECTION, true);
    }

}
