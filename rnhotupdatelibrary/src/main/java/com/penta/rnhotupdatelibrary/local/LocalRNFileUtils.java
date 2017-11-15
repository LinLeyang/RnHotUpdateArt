package com.penta.rnhotupdatelibrary.local;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 文件工具类
 * Created by Song on 2017/2/15.
 */
public class LocalRNFileUtils {

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


}
