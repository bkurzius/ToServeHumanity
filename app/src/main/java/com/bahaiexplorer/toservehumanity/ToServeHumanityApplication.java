package com.bahaiexplorer.toservehumanity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bahaiexplorer.toservehumanity.model.ConfigObjects;
import com.bahaiexplorer.toservehumanity.model.Constants;
import com.bahaiexplorer.toservehumanity.model.VideoItem;
import com.bahaiexplorer.toservehumanity.model.VideoObject;
import com.bahaiexplorer.toservehumanity.util.JsonUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;



/**
 * Created by briankurzius on 2/8/14.
 */
public class ToServeHumanityApplication extends Application implements JsonUtils.ConfigListener{
    final static String TAG = "ToServeHumanityApplication";
    final static String REMIND_CELLULAR_CONNECTION = "dont_remind_cellular_connection";
    public ArrayList<VideoObject> mVideoObjList;
    public TypedArray videoIconDrawables;
    private SharedPreferences prefs;
    private JSONObject jsonObj;


    @Override
    public void onCreate() {
        super.onCreate();
        buildVideoArray();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    // build the videoArray so we don;t have to do it more than once
    private void buildVideoArray(){
        mVideoObjList = new ArrayList<VideoObject>();
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
            VideoObject vo = new VideoObject();
            vo.title = videoTitle;
            vo.id = videoID;
            vo.fileName = videoFileName;
            vo.language = VideoItem.VIDEO_LANGUAGE_ENGLISH;
            vo.iconDrawable = (Drawable) videoIconDrawables.getDrawable(i);
            // TODO - set the proper length
            vo.length = "60min";
            // TODO - set te file path properly
            String filePath = vo.fileName + "_en_standard.mp4";
            boolean isFileSaved = isVideoFileSaved(getApplicationContext(),filePath);
            vo.isSaved = isFileSaved;
            mVideoObjList.add(vo);
        }
        videoIconDrawables.recycle();

        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.getJSONfromURL(Constants.CONFIG_URL, this);
    }



    public ArrayList<VideoObject> getVideoList(){
        return mVideoObjList;
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

    // the callback when the config is loaded
    public void configLoaded(ConfigObjects config){
        ArrayList<VideoObject> videos = null;
        // TODO load the first one by default - but later get them by language
        Log.d(TAG,"config:  " + config );
        if(config!=null){
            ArrayList<ConfigObjects.ConfigObject> configObjects = config.configObjects;
            Log.d(TAG,"configObjects:" + configObjects.size());
            if(configObjects!=null){
                ConfigObjects.ConfigObject co = configObjects.get(0);
                Log.d(TAG,"configObject:" + co);
                if(co!=null){
                     videos = co.videos;
                    Log.d(TAG,"videos:  " + videos );
                }
            }
            if(videos!=null){
                mVideoObjList = videos;
            }

            videoIconDrawables = getResources().obtainTypedArray(R.array.video_icon_array);
            for(int i=0; i<mVideoObjList.size(); i++){
                try{
                    VideoObject vo = mVideoObjList.get(i);
                    vo.iconDrawable = (Drawable) videoIconDrawables.getDrawable(i);
                }catch(Error e){
                   Log.e(TAG,"mission control issue");
                }
            }
        }

    }

}
