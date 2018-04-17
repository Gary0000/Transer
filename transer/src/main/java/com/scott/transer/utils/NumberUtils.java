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

    /**
     * 获取一串长度为 length 的随机串，注意:Random 的随机种子
     * 如果是相同的，则会产生相同的随机串。System.currentTimeMillis()
     * 在循环中可能会是相同的值，所以循环调用getRandomStr方法可能产生相同的
     * 随机串，hashCode 是一个在某时刻唯一的值，可以保证不会产生相同的序列
     * @param length
     * @param hashCode
     * @return
     */
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
