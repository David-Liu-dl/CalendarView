package me.fesky.library.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

/**
 * @author liuqiang
 *         <p/>
 *         需要添加以下权限
 *         <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 *         <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 *         <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
 */
public class NetworkUtil {

    /**
     * 检测是否有网络
     * @param context
     * @return
     */
    public static final boolean isNetWorkAvailable(@NonNull Context context) {
        boolean isCheckNet = false;
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo mobNetInfoActivity = connectivityManager
                    .getActiveNetworkInfo();
            if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
                isCheckNet = false;
                return isCheckNet;
            } else {
                isCheckNet = true;
                return isCheckNet;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isCheckNet;
    }

    /**
     * 判断当前网络是否是手机网络
     * @param context
     * @return
     */
    public static final boolean isMobileConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * WIFI 连接
     *
     * @param mContext
     * @return
     */
    public static boolean isWifiConnected(@NonNull Context mContext) {
        ConnectivityManager connManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


}
