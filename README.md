## Transer
是一个传输框架,目前支持：

- 支持 HTTP/HTTPS 断点续传下载
- 支持 HTTP/HTTPS 大文件分片上传
- 支持 类EventBus的task状态变更通知，支持三种线程的订阅模式
- 支持 任务分组，分用户
- 支持 传输速度限制(当前版本只支持下载限速)
- 支持 自定义的Http传输，数据库保存

## 集成方式
1. 在project build.gradle 中加入

        allprojects {
            repositories {
                maven { url 'https://jitpack.io' }
            }
        }

2. 在app 的 build.gradle 中加入

         compile 'com.github.shilec:Transer:1.0.0-beta'

3. 在app 的AndroidManifest.xml中 申明 TranserService

        <service android:name="com.scott.transer.TranserService"/>

4. 在 app 的 application 的 onCreate 中初始化TranserService

        TranserConfig config = new TranserConfig.Builder()
                .setDownloadConcurrentThreadSize(3)
                .setUploadConcurrentThreadSize(3)
                .build();

        TranserService.init(this,config);

## 帮助文档
[文档](https://github.com/shilec/Transer/blob/master/transer_doc.md)


##### 开源库使用:</br>

<a href="http://jakewharton.github.io/butterknife/">ButterKnife</a></br>
<a href="https://github.com/yanzhenjie/AndPermission">AndPermission</a></br>
<a href="https://github.com/square/retrofit">Retrofit</a></br>
<a href="https://github.com/ReactiveX/RxAndroid">RxAndroid</a></br>
<a href="https://github.com/ReactiveX/RxJava">RxJava</a></br>
<a href="https://github.com/CymChad/BaseRecyclerViewAdapterHelper">BaseRecyclerViewAdapterHelper</a></br>
<a href="https://github.com/greenrobot/greenDAO">GreenDao</a></br>
<a href="https://github.com/square/okhttp">OkHttp</a></br>

##### 功能更新日志:

- 2018/1/2 添加下载限速，设置分片大小
- 2018/1/21 简化传输器配置，修复部分bug
- 2018/3/6 增加Processor动态代理操作任务
- 2018/2/6 增加JitPack 依赖的支持
- 2018/3/27 增加分组任务，分用户支持


##### 服务端测试Demo详见：

- <a href="https://github.com/shilec/TranserServer">TranserServer</a>
