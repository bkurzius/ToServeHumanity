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
import com.bahaiexplorer.toservehumanity.model.Language;
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

    public ArrayList<VideoObject> mVideoObjList;
    public TypedArray videoIconDrawables;
    private SharedPreferences prefs;
    private JSONObject jsonObj;
    public ConfigObjects config;
    public ConfigObjects.ConfigObject currLanguageConfig;
    public ArrayList<LanguageChangedListener> languageChangeListeners = new ArrayList
            <LanguageChangedListener>();
    public ArrayList<ConfigChangedListener> configChangeListeners = new ArrayList
            <ConfigChangedListener>();


    public interface LanguageChangedListener{
        public void languageChanged();
    }
    public interface ConfigChangedListener{
        public void configChanged();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        JsonUtils jsonUtils = new JsonUtils();
        jsonUtils.loadConfigFromApp(this);
        jsonUtils.getJSONfromURL(Constants.CONFIG_URL, this);
    }



    public void addLanguageListener(LanguageChangedListener listener){
        languageChangeListeners.add(listener);
    }

    public void addConfigListener(ConfigChangedListener listener){
        configChangeListeners.add(listener);
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


    public ConfigObjects.ConfigObject.Strings getStrings(){
        if(currLanguageConfig!=null){
            return currLanguageConfig.strings;
        }else{
            return null;
        }
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


    // the callback when the config is loaded
    public void configLoaded(ConfigObjects _config){
        config = _config;

        loadConfigValues();
        for(ConfigChangedListener list: configChangeListeners){
            list.configChanged();
        }
    }

    public void loadConfigValues(){
        ArrayList<VideoObject> videos = null;

        Log.d(TAG,"config:  " + config );
        if(config!=null){
            ArrayList<ConfigObjects.ConfigObject> configObjects = config.configObjects;
            if(configObjects!=null){
                // TODO load the first one by default - if the correct language one is not
                // available
                int languageIndex = 0;
                for(int i=0; i<configObjects.size();i++){
                    ConfigObjects.ConfigObject config = configObjects.get(i);
                    if(config.language.equalsIgnoreCase(getLanguagePreference())){
                        languageIndex = i;
                        break;
                    }
                }
                Log.d(TAG,"the language is: " + languageIndex);
                currLanguageConfig = configObjects.get(languageIndex);
                if(currLanguageConfig!=null){
                    videos = currLanguageConfig.videos;
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
                    boolean isFileSaved = isVideoFileSaved(getApplicationContext(),vo.downloadFileName);
                    vo.isSaved = isFileSaved;
                }catch(Error e){
                    Log.e(TAG,"mission control issue");
                }
            }
        }
    }

    // **** PREFERENCES ****

    public void setLanguagePreference(String language){
        //set a preference not to remind on this version
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(Constants.PREFERENCE_KEY_LANGUAGE, language);
        edit.commit();

        loadConfigValues();
        for(LanguageChangedListener list: languageChangeListeners){
            list.languageChanged();
        }

    }

    public String getLanguagePreference(){
        //set a preference not to remind on this version
        return prefs.getString(Constants.PREFERENCE_KEY_LANGUAGE, Constants.LANGUAGE_ENGLISH);
    }

    public int getLanguagePreferenceIndex(){
        int index = 0;
        String lang = getLanguagePreference();
        // now loop through the langarray and get the index
        ArrayList<Language> langArray = getLanguageArray();
        for(int i =0; i<langArray.size(); i++){
            Language language = langArray.get(i);
            if(language.id.equalsIgnoreCase(lang)){
                index = i;
                Log.d(TAG,"got it:" + i);
                break;
            }
        }
        //set a preference not to remind on this version
        return index;
    }

    public void setRemindOnCellularConnection(){
        //set a preference not to remind on this version
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(Constants.PREFERENCE_KEY_CELLULAR_REMINDER, false);
        edit.commit();
    }

    public boolean remindOnCellularConnection(){
        return prefs.getBoolean(Constants.PREFERENCE_KEY_CELLULAR_REMINDER, true);
    }

    public boolean getSeenTermsOfUse(){
        return prefs.getBoolean(Constants.PREFERENCE_KEY_SEEN_TERMS + currLanguageConfig
                .language, false);
    }

    public void setSeenTermsOfUse(boolean tf){
        // add the languages so we are sure they see it in all languages
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(Constants.PREFERENCE_KEY_SEEN_TERMS + currLanguageConfig
                .language, tf);
        edit.commit();
    }

    /**
     * returns a ArrayList of the languages available
     * @return
     */
    public ArrayList<Language> getLanguageArray(){
        ArrayList<Language> languageArray = new ArrayList<Language>();

        Language lang = new Language(getResources().getString(R.string.language_name_english),
                getResources().getString(R.string.language_id_english));
        languageArray.add(lang);

        lang = new Language(getResources().getString(R.string.language_name_spanish),
                getResources().getString(R.string.language_id_spanish));
        languageArray.add(lang);

        lang = new Language(getResources().getString(R.string.language_name_french),
                getResources().getString(R.string.language_id_french));
        languageArray.add(lang);

        lang = new Language(getResources().getString(R.string.language_name_russian),
                getResources().getString(R.string.language_id_russian));
        languageArray.add(lang);

        lang = new Language(getResources().getString(R.string.language_name_farsi),
                getResources().getString(R.string.language_id_farsi));
        languageArray.add(lang);

        lang = new Language(getResources().getString(R.string.language_name_arabic),
                getResources().getString(R.string.language_id_arabic));
        languageArray.add(lang);

        return languageArray;
    }


}
