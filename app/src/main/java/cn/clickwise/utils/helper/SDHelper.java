package cn.clickwise.utils.helper;

import android.content.Intent;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cn.clickwise.AppContext;
import cn.clickwise.config.Constants;
import cn.clickwise.utils.other.ToastUtil;

/**
 * Created by T420s on 2016/10/20.
 */
public class SDHelper {
    public static String filePath = Environment.getExternalStorageDirectory() + File.separator + "ClickWiseRouteLog" + File.separator;
    public static String ApkPath = Environment.getExternalStorageDirectory() + File.separator + "ClickWiseRouteLog";
    public static String fileName = "";
    public static String fileType = "txt";
    public static String fileType_apk = "apk";
    public static double diagnoseProgress = 0;
    private static Intent intent;

    public static File getFile(String filePath, String fileName, String fileType) {//log.txt log为fileName,.txt为fileType
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            makeFile(filePath, fileName, fileType);
            file = new File(filePath + fileName + "." + fileType);
            if (!file.exists()) {
                try {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    public /*synchronized*/ static void saveFileToSD(final File file, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(content);
            /*if (*//*AppContext.isTestProgressing && *//*diagnoseProgress <= RouteTestHelper.calcuDiagnoseCount(AppContext.getContext())) {
                // ToastUtil.make("进来了，sendBroadcast");
                ++diagnoseProgress;
                if (intent == null) {
                    intent = new Intent(Constants.PROGRESSRESEIVER);
                }
                intent.putExtra(Constants.intentKey, diagnoseProgress);
                AppContext.getContext().sendBroadcast(intent);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成文件
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public static File makeFile(String filePath, String fileName, String fileType) {
        File file = null;
        makeRootDirectory(filePath);//创建文件首先生成文件夹
        try {
            file = new File(filePath + fileName + "." + fileType);
            if (!file.exists()) {
                file.createNewFile();//创建文件
            }
        } catch (Exception e) {

        }
        return file;
    }

    /**
     * 生成文件夹
     *
     * @param filePath
     * @return
     */
    public static File makeRootDirectory(String filePath) {
        File rootDirectory = null;
        try {
            rootDirectory = new File(filePath);
            if (!rootDirectory.exists()) {
                rootDirectory.mkdir();
            }
        } catch (Exception e) {

        }
        return rootDirectory;
    }

    public static String getSaveLogPath() {
        return getFile(filePath, fileName, fileType).getAbsolutePath();
    }

    public static String getFileName() {
        return getFile(filePath, fileName, fileType).getName();
    }

    /**
     * 判断当前分享的文件是否存在
     *
     * @return
     */
    public static boolean isExist() {
        boolean isExist = false;
        File file = new File(filePath + fileName + "." + fileType);
        if (file.exists()) {
            isExist = true;
        }
        return isExist;
    }
}
