package com.bahaiexplorer.toservehumanity.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by briankurzius on 2/11/14.
 */
public class ConnectionUtils {

    /**
     * returns if the device has mobile data -- like a phone
     * @param Context ctx
     * @return 	true - has mobile data capability
     * 		false - is not mobile capable
     */
    public static boolean isDeviceMobileDataCapable(final Context ctx){
        ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(mMobile!=null)return true;
        else return false;
    }

    /**
     * returns if the device has available wifi
     * @param Context
     * @return boolean - true if is available
     */

    public static boolean isUsingWifiConnection(final Context ctx) {
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    /**
     * returns if the device has available cellular data
     * @param Context
     * @return boolean - true if is available
     */

    public static boolean isUsingCellularConnection(final Context ctx) {
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo cellInfo = connec.getActiveNetworkInfo();
        if (cellInfo.isConnected()
                && cellInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            return true;
        else
            return false;
    }
}
