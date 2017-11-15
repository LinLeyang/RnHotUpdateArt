package com.penta.rnhotupdatelibrary.app;

import android.content.Context;

import com.penta.rnhotupdatelibrary.core.diff_match_patch;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    /**
     * 解压 ZIP 包
     */
    public static void decompression(String folderPath, String filePath) {
        try {
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(filePath));
            ZipEntry zipEntry;
            String szName;
            try {

                while ((zipEntry = inZip.getNextEntry()) != null) {

                    szName = zipEntry.getName();
                    if (zipEntry.isDirectory()) {
                        szName = szName.substring(0, szName.length() - 1);
                        File folder = new File(folderPath + File.separator + szName);
                        folder.mkdirs();
                    } else {
                        File file1 = new File(folderPath + File.separator + szName);
                        file1.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file1);
                        int len;
                        byte[] buffer = new byte[1024];
                        while ((len = inZip.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            fos.flush();
                        }
                        fos.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            inZip.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Assets目录下的bundle文件
     *
     * @return
     */
    public static String getJsBundleFromAssets(Context context, String fileName) {

        String result = "";
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 将.pat文件转换为String
     *
     * @param patPath 下载的.pat文件所在目录
     * @return
     */
    public static String getStringFromPat(String patPath) {

        FileReader reader;
        String result = "";
        try {
            reader = new FileReader(patPath);
            int ch = reader.read();
            StringBuilder sb = new StringBuilder();
            while (ch != -1) {
                sb.append((char) ch);
                ch = reader.read();
            }
            reader.close();
            result = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 将图片复制到bundle所在文件夹下的drawable-mdpi
     *
     * @param srcFilePath
     * @param destFilePath
     */
    public static void copyPatchImgs(String srcFilePath, String destFilePath) {

        File root = new File(srcFilePath);
        File[] files;
        if (root.exists() && root.listFiles() != null) {
            files = root.listFiles();
            for (File file : files) {
                File oldFile = new File(srcFilePath + file.getName());
                File newFile = new File(destFilePath + file.getName());
                DataInputStream dis = null;
                DataOutputStream dos = null;
                try {
                    dos = new DataOutputStream(new FileOutputStream(newFile));
                    dis = new DataInputStream(new FileInputStream(oldFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                int temp;
                try {
                    while ((temp = dis.read()) != -1) {
                        dos.write(temp);
                    }
                    dis.close();
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 遍历删除文件夹下所有文件
     *
     * @param filePath
     */
    public static void traversalFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    traversalFile(f.getAbsolutePath());
                } else {
                    f.delete();
                }
            }
            file.delete();
        }
    }

    /**
     * 删除指定File
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        File patFile = new File(filePath);
        if (patFile.exists()) {
            patFile.delete();
        }
    }

    /**
     * 删除文件夹下面的所有文件
     *
     * @param pPath      删除的路径
     * @param deleteSelf 是否删除路径本身
     */
    public static void deleteDir(final String pPath, boolean deleteSelf) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
        if (deleteSelf) {
            dir.delete();// 删除目录本身
        }
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            file.delete(); // 删除所有文件
//            if (file.isFile())
//
//            else if (file.isDirectory())
//                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
    }

    /**
     * 两个Asset资源目录下的bundle文件合并
     *
     * @param context
     * @param firstAssetName  第一个文件名
     * @param SecondAssetName 第二个文件名
     * @param finalTotalPath  最终生成文件路径
     */
    public static void mergePatAndAsset(Context context, String firstAssetName, String SecondAssetName, String finalTotalPath) {
        // 1.解析Asset目录下的bundle文件
        String assetsBundle = FileUtils.getJsBundleFromAssets(context, firstAssetName);
        // 2.解析bundle当前目录下.pat文件字符串
        String patcheStr = FileUtils.getJsBundleFromAssets(context, SecondAssetName);
        // 3.合并
        merge(patcheStr, assetsBundle, finalTotalPath);
    }

    /**
     * 与本地存储的bundle进行合并
     */
    public static void mergePatAndBundle(Context context, String firstAssetName, String patch, String finalTotalPath) {

        // 1.解析sd卡目录下的bunlde
        String assetsBundle = FileUtils.getJsBundleFromAssets(context, firstAssetName);
        // 2.解析最新下发的.pat文件字符串
        String patcheStr = FileUtils.getStringFromPat(patch);

        // 3.合并
        merge(patcheStr, assetsBundle, finalTotalPath);
//        // 5.删除本次下发的更新文件
//        FileUtils.traversalFile(PATCH_BUNDLE_DIR);
    }

    /**
     * 合并,生成新的bundle文件
     */
    private static void merge(String patchStr, String bundle, String finalTotalPath) {


        // 3.初始化 dmp
        diff_match_patch dmp = new diff_match_patch();
        // 4.转换pat
        LinkedList<diff_match_patch.Patch> patches = (LinkedList<diff_match_patch.Patch>) dmp.patch_fromText(patchStr);
        // 5.pat与bundle合并，生成新的bundle
        Object[] bundleArray = dmp.patch_apply(patches, bundle);
        // 6.保存新的bundle文件
        try {
            String[] a = finalTotalPath.split("/");
            String folder = finalTotalPath.replace(a[a.length - 1], "");
            File total_folder = new File(folder);
            total_folder.mkdirs();

            File total_bundle = new File(finalTotalPath);
            total_bundle.createNewFile();

            Writer writer = new FileWriter(finalTotalPath);
            String newBundle = (String) bundleArray[0];
            writer.write(newBundle);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
