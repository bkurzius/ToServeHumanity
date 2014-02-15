package com.bahaiexplorer.toservehumanity.model;

import java.util.ArrayList;

/**
 * Created by briankurzius on 2/12/14.
 */
public class ConfigObjects {
    public ArrayList<ConfigObject> configObjects;

    public class ConfigObject {

        public String projectName;
        public String language;
        public String website;
        public String facebookPage;
        public Strings strings;
        public ArrayList<VideoObject> videos;

        public class Strings{
            public String titleSave;
            public String titleSaved;
            public String titleShare;
            public String titleTerms;
            public String titleSaving;
            public String titleAlreadySaved;
            public String titleSaveSucceeded;
            public String textTerms;
            public String textIntro;
            public String alertCellularData;
        }
    }
}


