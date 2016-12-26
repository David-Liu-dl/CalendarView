package me.fesky.library.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

/**
 * 桌面快捷图标的管理
 *
 * @author liuqiang
 *         <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
 *         <uses-permission android:name="com.android.launcher.permission.READ_SETTINGS" />
 *         <uses-permission android:name="com.android.launcher.permission.UNINSTALL_SHORTCUT" />
 */
public class ShortcutUtil {

    /**
     * 为当前应用添加桌面快捷方式
     *
     * @param context
     * @param shortcutName         R.string.app_name
     * @param shortcutIcon         R.drawable.ic_launcher
     * @param launcherActivityName 例如MainActivity.getClass().getName()   要启动的activity的名字
     */
    public static void addShortcut(Context context, int shortcutName, int shortcutIcon, String launcherActivityName) {
        if (hasShortcut(context, shortcutName)) {
            return;
        }

        // 快捷方式要启动的Activity
        Intent target = new Intent(Intent.ACTION_MAIN);
        target.setClassName(context, launcherActivityName);
        target.addCategory(Intent.CATEGORY_LAUNCHER);
        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        target.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        // 动态生成快捷方式
        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        // 快捷方式的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                context.getString(shortcutName));
        // 快捷方式的图标
        Parcelable iconResource = Intent.ShortcutIconResource.fromContext(
                context.getApplicationContext(), shortcutIcon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
        // 不允许重复创建（不一定有效）
        shortcutIntent.putExtra("duplicate", false);

        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);

        //  利用receiver通知系统创建快捷方式
        context.sendBroadcast(shortcutIntent);
    }

    /**
     * 删除当前应用的桌面快捷方式
     *
     * @param context
     * @param shortcutName R.string.app_name
     */
    public static void delShortcut(Context context, int shortcutName, String launcherActivityName) {

        if (!hasShortcut(context, shortcutName)) {
            return;
        }

        // 快捷方式要启动的Activity
        Intent target = new Intent(Intent.ACTION_MAIN);
        target.setClassName(context, launcherActivityName);
        target.addCategory(Intent.CATEGORY_LAUNCHER);
        target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        target.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);


        final Intent shortcutIntent = new Intent();
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                context.getResources().getString(shortcutName));
        shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(shortcutIntent);
    }

    /**
     * 判断桌面是否已添加快捷方式
     * 并不是所有的国产机都能精确的判断
     *
     * @param context
     * @param shortcutName R.string.app_name
     * @return
     */
    public static boolean hasShortcut(Context context, int shortcutName) {
        // 获取应用名称
        String title = context.getResources().getString(shortcutName);

        String uri = null;
        if (android.os.Build.VERSION.SDK_INT < 8) {
            uri = "content://com.android.launcher.settings/favorites?notify=true";
        } else {
            uri = "content://com.android.launcher2.settings/favorites?notify=true";
        }
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = resolver.query(
                    Uri.parse(uri),
                    null,
                    "title=?",
                    new String[]{title},
                    null
            );
            if (cursor != null) {
                return cursor.moveToFirst();
            }
        } catch (Exception ex) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    /**
     * 通过Context的getContentResolve获取系统的共享数据，在共享数据中，查找指定Uri的数据，也即launcher中的favorite表，也即快捷方式表。
     * 然后query的参数是筛选表的title列，并且title=“app_name”的行数据，如果查询有结果，表示快捷方式名为R.string.app_name的存在，也即返回快捷方式已经添加到桌面。
     * 但是如果你的测试机是MIUI，或者HTC等rom，你会发现c为null，怎么回事，解释下
     * android系统桌面的基本信息由一个launcher.db的Sqlite数据库管理，里面有三张表，其中一张表就是favorites。
     * 这个db文件一般放在data/data/com.android.launcher(launcher2)文件的databases下。
     * 但是对于不同的rom会放在不同的地方，例如MIUI放在data/data/com.miui.home/databases下面，htc放在data/data/com.htc.launcher/databases下面，
     * 那么如何用程序来找到这个认证标示呢
     *
     * @param context
     * @param permission
     * @return
     */
    private String getAuthorityFromPermission(Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            return null;
        }
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs == null) {
            return null;
        }
        for (PackageInfo pack : packs) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (permission.equals(provider.readPermission) || permission.equals(provider.writePermission)) {
                        return provider.authority;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param permission   com.android.launcher.permission.READ_SETTINGS
     * @param context
     * @return
     */
    private boolean hasShortcut(Context context, String permission) {
        final String AUTHORITY = getAuthorityFromPermission(context.getApplicationContext(),permission);
        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor c = context.getContentResolver().query(
                CONTENT_URI,
                new String[]{"title"},
                "title=?",
                new String[]{"test"},
                null);
        if (c != null && c.moveToNext()) {
            return true;
        }
        return false;
    }

}