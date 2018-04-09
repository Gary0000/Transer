package com.scott.transer.utils;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-20 13:31</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class NumberUtils {

    public static String getRandomStr(int length,long hashCode) {
        if(length <= 0) {
            length = 10;
        }

        Random random = new Random(System.currentTimeMillis() + hashCode);
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < length; i++) {
            int i1 = random.nextInt(10000);
            sb.append(i1 + "");
        }
        return sb.toString();
    }
}
