package com.bahaiexplorer.toservehumanity.model;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;

/**
 * Created by briankurzius on 2/12/14.
 */
public class VideoObject {
    @Expose
    public String title = "";
    @Expose
    public String id = "";
    @Expose
    public String fileName = "";
    @Expose
    public String streamingURL = "";
    @Expose
    public String downloadURL = "";
    @Expose
    public String downloadPath = "";
    @Expose
    public String downloadFileName = "";
    @Expose
    public String length = "";
    @Expose
    public String downloadSize = "";

    public String language;
    public boolean isSaved;
    public Drawable iconDrawable;

}
