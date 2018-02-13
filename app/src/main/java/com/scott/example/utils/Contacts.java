package com.scott.example.utils;

import android.os.Environment;

import java.io.File;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2017/12/19</P>
 * <P>Email: shilec@126.com</p>
 */

public class Contacts {

    public static class API {

        public static final String HOST_URL = "192.168.1.103";

        public static final String HOST_PORT = "8080";

        public static final String HOST_SCHEMA = "http";

        public static final String WEB_APP = "WebDemo";

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
    }
}
