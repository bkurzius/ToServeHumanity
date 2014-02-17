package com.bahaiexplorer.toservehumanity.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by briankurzius on 2/12/14.
 */
public class ConfigObjects {
    @Expose
    public ArrayList<ConfigObject> configObjects;

    public class ConfigObject {
        @Expose
        public String projectName;
        @Expose
        public String language;
        @Expose
        public String website;
        @Expose
        public String facebookPage;
        @Expose
        public String appLink;
        @Expose
        public Strings strings;
        @Expose
        public ArrayList<VideoObject> videos;

        public class Strings{
            @Expose
            public String titleLanguage;
            @Expose
            public String titleGoToWebsite;
            @Expose
            public String titleAbout;
            @Expose
            public String titleSave;
            @Expose
            public String titleSaved;
            @Expose
            public String titleShare;
            @Expose
            public String titleShareSubject;
            @Expose
            public String textShareBody;
            @Expose
            public String titleTerms;
            @Expose
            public String titleSaving;
            @Expose
            public String titleFacebookLink;
            @Expose
            public String titleAlreadySaved;
            @Expose
            public String titleSaveSucceeded;
            @Expose
            public String titleFileDeleted;
            @Expose
            public String textSureYouWantToDelete;
            @Expose
            public String textTerms;
            @Expose
            public String textIntro;
            @Expose
            public String textNeedToSave;
            @Expose
            public String alertCellularData;
        }
    }
}


