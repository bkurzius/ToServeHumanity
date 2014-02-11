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

    public String videoTitle;
    public String videoID;
    public String videoFileName;
    public String videoLanguage;
    public String videoLength;
    public Drawable videoIconDrawable;
    public boolean isSaved;


    public String getVideoFileName(){
        return "";
    }
}
