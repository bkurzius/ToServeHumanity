package com.bahaiexplorer.toservehumanity.util;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by briankurzius on 2/8/14.
 */
public class UIUtils {

    public static boolean isTablet(Context context) {
        if (context != null && context.getResources() != null) {
            if(context.getResources().getConfiguration()!=null){
                return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
            }
            return false;
        }
        return false;
    }


}
