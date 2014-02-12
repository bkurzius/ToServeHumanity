package com.bahaiexplorer.toservehumanity.model;

import android.graphics.drawable.Drawable;

/**
 * Created by briankurzius on 2/8/14.
 */
public class VideoItem {
    public static final String VIDEO_INDEX = "video_index";
    public static final String VIDEO_LANGUAGE_ENGLISH = "en";
    public static final String VIDEO_LANGUAGE_SPANISH = "es";
    public static final String VIDEO_LANGUAGE_FRENCH = "fr";
    public static final String VIDEO_LANGUAGE_RUSSIAN = "ru";
    public static final String VIDEO_LANGUAGE_FARSI = "fa";
    public static final String VIDEO_LANGUAGE_ARABIC = "ar";
    public static final String VIDEO_FOLDER = "ToServeHumanity";
    public static final String DOWNLOAD_PATH = "http://downloadcdn1.bahai.org/toserve/";
    public static final String LANGUAGE = "en";
    public static final String VIDEO_SIZE = "standard";
    public static final String VIDEO_SUFFIX = ".mp4";
    public static final String VIDEO_STREAM_PATH = "http://player.vimeo.com/video/";

    public String videoTitle;
    public String videoID;
    public String videoFileName;
    public String videoStreamURL;
    public String videoDownloadURL;
    public String videoLanguage;
    public String videoLength;
    public Drawable videoIconDrawable;
    public boolean isSaved;


    public String getVideoFileName(){
        return "";
    }

    public String getVideoDownloadPath(){
        return DOWNLOAD_PATH + videoFileName + "_" + LANGUAGE + "_" + VIDEO_SIZE + VIDEO_SUFFIX;
    }

    public String getVideoStreamURL(){
        return VIDEO_STREAM_PATH + videoID;
    }
}
