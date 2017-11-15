package com.penta.rnhotupdatelibrary.local;


import com.penta.rnhotupdatelibrary.core.diff_match_patch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by linyueyang on 9/5/17.
 * <p>
 * RN Android 热更新本地生成差量包的工具 通过google-diff-match-patch
 */

public class DiffMain {

    //各个路径 其中common.bundle business.bundle需要跟着app一起打包 因此生成在assets里面
    //_total.bundle _patch.bundle 打在别的目录下即可

    private final static String BUSIBESS_NAME = "bus_a";
    private final static boolean ISPATCH = true;

    private final static String PROJECT_PATH = System.getProperty("user.dir");

    private final static String COMMON_BUNDLE_PATH = PROJECT_PATH + "/app/src/main/assets/common.bundle";
    private final static String TOTAL_BUNDLE_PATH = PROJECT_PATH + "/rn/hot_update/" + BUSIBESS_NAME + "_total.bundle";

    private final static String BUSINESS_BUNDLE_PATH = PROJECT_PATH + "/app/src/main/assets/" + BUSIBESS_NAME + "_diff.bundle";
    private final static String PATCH_BUNDLE_PATH = PROJECT_PATH + "/rn/hot_update/" + BUSIBESS_NAME + "_patch.bundle";
    private final static String PATCH_ZIP_PATH = PROJECT_PATH + "/rn/hot_update/" + BUSIBESS_NAME + "_patch.zip";

    /**
     * 在执行程序之前请确保 生成了common.bundle 和 total.bundle
     * <p>
     * 生成方法 首先确保你在工程里面的rn_folder目录下执行命令
     * <p>
     * 生成common.bundle命令
     * react-native bundle --platform android --dev false --entry-file ./blank.js --bundle-output ../app/src/main/assets/common.bundle --assets-dest ../app/src/main/res/
     * <p>
     * 生成total.bundle命令
     * react-native bundle --platform android --dev false --entry-file ./bus_a.js --bundle-output ./hot_update/bus_a_total.bundle
     */

    public static void main(String[] a) {


        //开发阶段 首先需要在本地执行打包命令 打出公共包 common.bundle，common.bundle生成一次即可
        //以及针对某个业务模块的全量包_total.bundle
        //打出diff包 这里命名为_business.bundle

        //更新阶段和上面一样只不过新的diff包 命名为_patch.bundle

        String common_bundle = LocalRNFileUtils.getStringFromPat(COMMON_BUNDLE_PATH);
        String total_bundle = LocalRNFileUtils.getStringFromPat(TOTAL_BUNDLE_PATH);

        // 对比
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(common_bundle, total_bundle);

        // 生成差异补丁包
        LinkedList<diff_match_patch.Patch> patches = dmp.patch_make(diffs);

        // 解析补丁包
        String patchesStr = dmp.patch_toText(patches);

        // 将补丁文件写入到某个位置
        FileWriter writer;
        String diffPatch;
        if (ISPATCH) {
            diffPatch = PATCH_BUNDLE_PATH;
        } else {
            diffPatch = BUSINESS_BUNDLE_PATH;
        }
        try {
            writer = new FileWriter(diffPatch);
            writer.write(patchesStr);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ISPATCH) {
            ZipCompress zipCom = new ZipCompress(PATCH_ZIP_PATH, PATCH_BUNDLE_PATH);
            try {
                zipCom.zip();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
