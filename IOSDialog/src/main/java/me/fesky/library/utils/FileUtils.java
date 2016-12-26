package me.fesky.library.utils;

import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;

/**
 * @author liuqiang
 *
 *
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * @return true if exsists and can write, false on others
     * @description check the ExternalStorage status
     */
    public final static boolean isExternalStorageWrittenable() {
        boolean mExternalStorageAvailable, mExternalStorageWriteable;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable;
    }

    public final static boolean isExternalStorageReadable() {
        boolean mExternalStorageReadable;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageReadable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageReadable = true;
        } else {
            mExternalStorageReadable = false;
        }
        return mExternalStorageReadable;
    }


    /**
     * 文件删除操作
     * @param path
     * @return
     */
    public final static boolean delete(@NonNull final File path) {

        boolean result = true;
        if (path.exists()) {
            if (path.isDirectory()) {
                for (File child : path.listFiles()) {
                    result &= delete(child);
                }
                result &= path.delete(); // Delete empty directory.
            }
            if (path.isFile()) {
                result &= path.delete();
            }
            if (!result) {
                Log.e(null, "Delete failed;");
            }
            return result;
        } else {
            Log.e(null, "File does not exist.");
            return false;
        }
    }

    /**
     * 某个目录的大小
     * @param directory
     * @return
     */
    public final static long getFolderSize(@NonNull final File directory) {
        long folderSize = 0;

        File[] currentFolder = directory.listFiles();

        for (int q = 0; q < currentFolder.length; q++) {
            if (currentFolder[q].isDirectory()) {
                //if folder run self on q'th folder - in which case the files.length will be counted for the files inside
                folderSize += getFolderSize(currentFolder[q]);
            } else {
                //else get file size
                folderSize += currentFolder[q].length();
            }
        }
        return folderSize;
    }

    /**
     * 检测sd卡得剩余空间
     *
     * @return
     */
    public final static long getAvailableStorage() {
        try {
            File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
            StatFs stat = new StatFs(path.getAbsolutePath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } catch (RuntimeException ex) {
            return 0;
        }
    }


    /**
     * sd卡的总共空间
     *
     * @return
     */
    public static long getTotalStorage() {

        try {
            //取得SD卡文件路径
            File path = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(path.getAbsolutePath());
            //获取单个数据块的大小(Byte)
            long blockSize = sf.getBlockSize();
            //获取所有数据块数
            long allBlocks = sf.getBlockCount();
            //返回SD卡大小
            //return allBlocks * blockSize; //单位Byte
            //return (allBlocks * blockSize)/1024; //单位KB
            return allBlocks * blockSize; //单位B
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    /**
     * 得到格式化的磁盘空间
     *
     * @param size
     * @return
     */
    public static String getFormatSize(long size) {

        double d_size = size * 1.0;

        DecimalFormat formater = new DecimalFormat("####.##");
        if (d_size <= 0) {
            return "0B";
        } else if (d_size < 1024 * 1.0) {
            return d_size + "B";
        } else if (d_size < 1024.0 * 1024) {
            double kbsize = d_size / 1024.0;
            return formater.format(kbsize) + "KB";
        } else if (d_size < 1024 * 1024 * 1024.0) {
            double mbsize = d_size / 1024 / 1024.0;
            return formater.format(mbsize) + "MB";
        } else {
            double gbsize = d_size / 1024 / 1024 / 1024.0;
            return formater.format(gbsize) + "GB";
        }
    }

}
