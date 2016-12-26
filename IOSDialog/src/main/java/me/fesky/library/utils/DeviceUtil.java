package me.fesky.library.utils;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

/**
 * 手机某些功能是否可用
 * @author liuqiang
 *
 */
public class DeviceUtil {

	/**
	 * 判断手机是否支持闪关灯
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isCameraFlashAvailable(Context context) {

		PackageManager pm = context.getPackageManager();
		FeatureInfo[] features = pm.getSystemAvailableFeatures();
		for (FeatureInfo f : features) {
			if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
				return true;
			}
		}
		return false;
	}

	public static String getInfo(@NonNull final Context context) {
		String model = android.os.Build.MODEL;
		String device = android.os.Build.DEVICE;
		String brand = android.os.Build.BRAND;
		String product = android.os.Build.PRODUCT;
		String display = android.os.Build.DISPLAY;
		String manufacture = android.os.Build.MANUFACTURER;

		Resources resources = context.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		float density = dm.density;

		StringBuilder sb = new StringBuilder();
		String finalInfo = sb.append("MODEL " + model)
				.append("\nDEVICE " + device).append("\nBRAND " + brand)
				.append("\nPRODUCT " + product).append("\nDISPLAY " + display)
				.append("\nMANUFACTURE " + manufacture)
				.append("\nSCREEN_WIDTH " + screenWidth)
				.append("\nSCREEN_HEIGHT " + screenHeight)
				.append("\nDENSITY " + density).toString();
		return finalInfo;
	}
}
