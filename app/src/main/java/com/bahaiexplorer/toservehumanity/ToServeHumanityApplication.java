package com.bahaiexplorer.toservehumanity;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by briankurzius on 2/8/14.
 */
public class ToServeHumanityApplication extends Application {
    final static String TAG = "ToServeHumanityApplication";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    static public boolean isVideoFileSaved(final Context context, String fileName){
        File[] files = getSavedFiles();
        for(File file:files){
            Log.d(TAG, "file.getName(): " + file.getName());
            // now check if the fiel is the one we are looking for
            if(file.getName().indexOf(fileName)>-1){
                return true;
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

}
