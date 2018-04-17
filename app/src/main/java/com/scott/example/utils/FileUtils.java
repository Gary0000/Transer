package com.scott.example.utils;

import android.os.Environment;

import com.scott.transer.event.EventDispatcher;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2018/2/3</P>
 * <P>Email: shilec@126.com</p>
 */

public class FileUtils {

    private static String sDownloadRoot;
    private static final String TRANSER_DEFAULT_ROOT = "transer";
    private static final String TRANSER_DEFAULT_DOWNLOAD_PATH = "download";

    public static String getDownloadSavePath() {
        synchronized (FileUtils.class) {
            if(sDownloadRoot == null) {
                sDownloadRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }
        String path = sDownloadRoot + File.separator + TRANSER_DEFAULT_ROOT +
                File.separator + TRANSER_DEFAULT_DOWNLOAD_PATH;
        try {
            File file = new File(path);
            if(!file.exists()) {
                boolean ret = file.mkdirs();
                if(!ret) return null;
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDownloadPathByName(String name) {
        String path = getDownloadSavePath();
        path += File.separator + name;
        return path;
    }

    public static String getFileMD5Value(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[512 * 1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
}
