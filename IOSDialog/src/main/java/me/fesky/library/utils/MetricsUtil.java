package me.fesky.library.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

/**
 * 获取一些尺寸信息
 * @author liuqiang
 *
 */
public class MetricsUtil {

	/**
	 * 获取屏幕尺寸，像素
	 * @param window
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static Point getScreenSize(Window window) {
		Point size = new Point();

		android.view.Display display = window.getWindowManager()
				.getDefaultDisplay();

		try {
			display.getRealSize(size);
		} catch (NoSuchMethodError e) {
			size.x = display.getWidth();
			size.y = display.getHeight();
		}
		return size;
	}

	/**
	 * 获得状态栏的高度
	 *
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {

		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}
	
	/**
	 * 获取屏幕尺寸，像素
	 * @param activity
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static Point getScreenSize(Activity activity) {
		Point size = new Point();

		android.view.Display display = activity.getWindowManager()
				.getDefaultDisplay();

		try {
			display.getRealSize(size);
		} catch (NoSuchMethodError e) {
			size.x = display.getWidth();
			size.y = display.getHeight();
		}
		return size;
	}
	
	/**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */ 
    public static int dip2px(Context context, float dpValue) { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int) (dpValue * scale + 0.5f); 
    } 
   
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */ 
    public static int px2dip(Context context, float pxValue) { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int) (pxValue / scale + 0.5f); 
    }

	public static float getScreenInches(@NonNull final Context context) {
		float screenInches = -1;
		try {
			Resources resources = context.getResources();
			DisplayMetrics dm = resources.getDisplayMetrics();
			double width = Math.pow(dm.widthPixels / dm.xdpi, 2);
			double height = Math.pow(dm.heightPixels / dm.ydpi, 2);
			screenInches = (float) (Math.sqrt(width + height));
		} catch (Exception e) {
		}
		return screenInches;
	}

	public static int getDensity(@NonNull final Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		int density = metrics.densityDpi;
		return density;
	}

}