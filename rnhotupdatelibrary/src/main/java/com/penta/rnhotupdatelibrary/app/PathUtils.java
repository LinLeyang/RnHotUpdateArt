package com.penta.rnhotupdatelibrary.app;

import android.content.pm.PackageManager;

import java.io.File;

/**
 * RN热修复相关所有路径的工具类
 */
public class PathUtils {


    private static final String FILE_DIR = RnUpdateLogic.ins().getContext().getExternalFilesDir(null).getPath();

    private static final String BUNDLE_FILE_DIR = FILE_DIR + "/bundle";

    private static final String PATCH_FILE_DIR = FILE_DIR + "/patch";

    public static String getPatchFileDir() {
        return PATCH_FILE_DIR;
    }

    public static String getBundleFileDir() {
        return BUNDLE_FILE_DIR;
    }

    public static String getTotalVersionBundlePath(String business_name) {

        //版本号 防止版本更新时 导致新的total bundle无法生成
        String verCode = "";
        try {
            verCode = AppInfoUtils.getVersionCode(RnUpdateLogic.ins().getContext());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return BUNDLE_FILE_DIR + File.separator + verCode + File.separator + business_name + "_total.bundle";
    }

    public static String getTotalVersionFolderPath() {

        //版本号 防止版本更新时 导致新的total bundle无法生成
        String verCode = "";
        try {
            verCode = AppInfoUtils.getVersionCode(RnUpdateLogic.ins().getContext());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return BUNDLE_FILE_DIR + File.separator + verCode;
    }

    public static String getBusinessBundleName(String business_name) {
        return business_name + "_diff.bundle";
    }

    public static String getPatchBundlePath(String business_name) {
        return PATCH_FILE_DIR + File.separator + business_name + "_patch.bundle";
    }

    public static String getPatchZipPath(String business_name) {
        return PATCH_FILE_DIR + File.separator + business_name + "_patch.zip";
    }

}
