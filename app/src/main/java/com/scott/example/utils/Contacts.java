package com.scott.example.utils;

import android.os.Environment;

import java.io.File;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2017/12/19</P>
 * <P>Email: shilec@126.com</p>
 */

public class Contacts {

    //测试的user 列表
    public final static String[] TEMP_USERS = new String[]{
            "shijiale",
            "shilec",
            "scott"
    };

    //用来当做user id
    public static String USER_ID = TEMP_USERS[0];

    public static class API {

        public static final String HOST_URL = "192.168.1.129";
        //public static final String HOST_URL = "192.168.1.121";

       //public static final String HOST_PORT = "8080";
        public static final String HOST_PORT = "443";

        //public static final String HOST_SCHEMA = "http";
        public static final String HOST_SCHEMA = "https";

        //public static final String WEB_APP = "TranserServer/";
        public static final String WEB_APP = "/api/v1/files/";

        public static final String DOWNLOAD_URL = "download";

        public static final String UPLOAD_URL = "upload";

        public static final String FILE_LIST_GET = "file_list_get";

        public static String getBaseUrl() {
            return HOST_SCHEMA + "://" + HOST_URL + ":" + HOST_PORT
                    + "/";
        }

        public static String getWebAppUrl() {
            return getBaseUrl() + WEB_APP + "/";
        }

        public static String getUrl(String url) {
            return getWebAppUrl() + url;
            //return getBaseUrl() + "api/v1/files/upload";
        }
    }

    public static class LOCAL_STORAGE {

        public static final String TRANSER_ROOT = "transer";

        public static String getBaseSavePath() {

            if(!Environment.isExternalStorageEmulated()) {
                return null;
            }
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            root += File.separator + TRANSER_ROOT;
            File file = new File(root);

            if(!file.exists()) {
                if(!file.mkdirs()) {
                    return null;
                }
            }
            return root;
        }

        public static String getSavePath(String path) {
            path = getBaseSavePath() + File.separator + path;
            File file = new File(path);

            if(!file.exists()) {
                if(!file.mkdirs()) {
                    return null;
                }
                return path;
            }
            return path;
        }
    }

}
