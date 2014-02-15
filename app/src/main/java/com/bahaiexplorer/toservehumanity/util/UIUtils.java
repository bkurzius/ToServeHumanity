package com.bahaiexplorer.toservehumanity.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

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

    public static boolean isOSLessThanHoneycomb(){
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            return true;
        return false;
    }


}
