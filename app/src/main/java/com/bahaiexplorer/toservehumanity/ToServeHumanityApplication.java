package com.bahaiexplorer.toservehumanity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bahaiexplorer.toservehumanity.model.ConfigObjects;
import com.bahaiexplorer.toservehumanity.model.Constants;
import com.bahaiexplorer.toservehumanity.model.Language;
import com.bahaiexplorer.toservehumanity.model.VideoObject;
import com.bahaiexplorer.toservehumanity.util.ConnectionUtils;
import com.bahaiexplorer.toservehumanity.util.JsonUtils;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


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
    private BroadcastReceiver networkStateReceiver;

    private JsonUtils jsonUtils;

    private static GoogleAnalytics mGa;
    private static Tracker mTracker;


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
        jsonUtils = new JsonUtils();
        jsonUtils.loadConfigFromApp(this);

        //add connection chck and if not available set a listener so we are sure to load the
        // config as soon as we get connected again
        if(ConnectionUtils.isConnected(getApplicationContext())){
            getJSONfromURL();
        }else{
            startCheckingConnection();
        }

        initializeGa();

    }

    private void initializeGa() {
        mGa = GoogleAnalytics.getInstance(this);
        mTracker = mGa.getTracker(Constants.GA_ANALYTICS_ID);

        // Set dispatch period.
        GAServiceManager.getInstance().setLocalDispatchPeriod(Constants.GA_DISPATCH_PERIOD);

        // Set dryRun flag.
        mGa.setDryRun(Constants.GA_IS_DRY_RUN);

        // Set Logger verbosity.
        mGa.getLogger().setLogLevel(Constants.GA_LOG_VERBOSITY);

    }
    private void getJSONfromURL(){
        jsonUtils.getJSONfromURL(Constants.CONFIG_URL, this);
    }

    private void startCheckingConnection(){
       networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Network Type Changed");
                if(ConnectionUtils.isConnected(getApplicationContext())){
                    getJSONfromURL();
                    stopCheckingConnection();
                }else{
                    Log.d(TAG, "Keep listening");
                }
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);
    }

    private void stopCheckingConnection(){
        // stop listening
        unregisterReceiver(networkStateReceiver);
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


    /**
     *  ANALYTICS
     *
     *
     */


    // Returns the Google Analytics tracker.


    public static Tracker getGaTracker() {
        return mTracker;
    }

    // * Returns the Google Analytics instance.


    public static GoogleAnalytics getGaInstance() {
        return mGa;
    }

    /**
     * not sure if this is working
     * @param screenName
     */
    public void  trackScreen(String screenName){
        /*
        * Send a screen view to Google Analytics by setting a map of parameter
        * values on the tracker and calling send.
        */
        HashMap<String, String> hitParameters = new HashMap<String, String>();
        hitParameters.put(Fields.SCREEN_NAME, screenName);
        getGaTracker().send(hitParameters);


    }

    /**
     * Tracks a user event
     * @param eventType
     * @param eventName
     */
    public void trackEvent(String eventType, String eventName){
        /*
        * Send a screen view to Google Analytics by setting a map of parameter
        * values on the tracker and calling send.
        */

        HashMap<String, String> hitParameters = new HashMap<String, String>();
        hitParameters.put(Fields.EVENT_ACTION, eventType);
        hitParameters.put(Fields.EVENT_VALUE, eventName);
        getGaTracker().send(hitParameters);


        EasyTracker easyTracker = EasyTracker.getInstance(this);

        // MapBuilder.createEvent().build() returns a Map of event fields and values
        // that are set and sent with the hit.
        easyTracker.send(MapBuilder
                .createEvent(eventType,     // Event category (required)
                        eventName,  // Event action (required)
                        null,   // Event label
                        null)            // Event value
                .build()
        );

    }




}
