package com.bahaiexplorer.toservehumanity.model;

import com.google.analytics.tracking.android.Logger;

/**
 * Created by briankurzius on 2/8/14.
 */
public class Constants {

    public static final String GA_ANALYTICS_ID = "UA-2194207-9";
    public static final int GA_DISPATCH_PERIOD = 30;
    public static final boolean GA_IS_DRY_RUN = false;
    public static final Logger.LogLevel GA_LOG_VERBOSITY = Logger.LogLevel.INFO;

    public static final String TRACK_SCREEN_HOME = "trackscreen_home";
    public static final String TRACK_SCREEN_DETAIL = "trackscreen_detail";
    public static final String TRACK_SCREEN_STREAM_VIDEO = "trackscreen_stream_video";
    public static final String TRACK_SCREEN_VIDEO = "trackscreen_watch_video";
    public static final String TRACK_SCREEN_VIDEO_GINGERBREAD =
            "trackscreen_watch_video_gingerbread";
    public static final String TRACK_SCREEN_TERMS = "trackscreen_terms";
    public static final String TRACK_SCREEN_ABOUT = "trackscreen_about";

    public static final String TRACK_EVENT_TYPE_DOWNLOAD_VIDEO = "Event_download_video";
    public static final String TRACK_EVENT_TYPE_VIEW_VIDEO = "Event_view_video";
    public static final String TRACK_EVENT_TYPE_PLAY_VIDEO = "Event_play_video";
    public static final String TRACK_EVENT_TYPE_STREAM_VIDEO = "Event_stream_video";
    public static final String TRACK_EVENT_TYPE_SHARE = "Event_share";
    public static final String TRACK_EVENT_TYPE_DELETE_VIDEO = "Event_delete_video";
    public static final String TRACK_EVENT_TYPE_GOTO_FACEBOOK = "Event_goto_facebook";
    public static final String TRACK_EVENT_TYPE_GOTO_WEBSITE = "Event_goto_website";
    public static final String TRACK_EVENT_TYPE_CHANGE_LANGUAGE = "Event_change_language";



    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_SPANISH = "es";
    public static final String LANGUAGE_FRENCH = "fr";
    public static final String LANGUAGE_RUSSIAN = "ru";
    public static final String LANGUAGE_FARSI = "fa";
    public static final String LANGUAGE_ARABIC = "ar";

    public static final String CACHED_CONFIG_FILENAME = "cachedConfig.json";

   public static final String PREFERENCE_KEY_LANGUAGE = "language";
    public static final String PREFERENCE_KEY_SEEN_TERMS = "seen_terms";
    public static final String PREFERENCE_KEY_CELLULAR_REMINDER = "dont_remind_cellular_connection";


    public static final String CONFIG_URL = "http://bahaiexplorer.com/toservehumanity/config.json";
    public static final String VIDEO_FOLDER = "ToServeHumanity";
    public static final String DOWNLOAD_PATH = "http://downloadcdn1.bahai.org/toserve/";
    public static final String LANGUAGE = "en";
    public static final String VIDEO_SIZE = "standard";
    public static final String VIDEO_SUFFIX = ".mp4";
}
