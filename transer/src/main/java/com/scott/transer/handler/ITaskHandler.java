package com.scott.transer.handler;


import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-13 11:52</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:
 *      传输处理器
 * </p>
 */

public interface ITaskHandler extends ITaskHolder {

    //设置一个线程池，用来执行当前的Handler,如果不设置，start 后将会在当前线程中传输
    void setThreadPool(ThreadPoolExecutor threadPool);

    //启动 支持同步，和异步
    void start();

    //停止
    void stop();

    //当前handler 包含的请求头
    Map<String,String> getHeaders();

    //当前handler 包含的请求参数
    Map<String,String> getParams();

    //设置请求头
    void setHeaders(Map<String,String> headers);

    //设置请求参数
    void setParams(Map<String,String> params);

    //设置状态回掉
    void setHandlerListenner(ITaskHandlerCallback l);
}
