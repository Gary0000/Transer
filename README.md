## Transer
是一个大文件传输的任务管理框架，该框架的设计是无关传输协议的。

## 架构
<img src="https://github.com/shilec/Transer/blob/master/imgs/transer_designpng.png"></img>

## 功能

状态 | 功能
-------- | ---
**支持**|**HTTP/HTTPS 断点续传下载**
**支持**|**HTTP/HTTPS 大文件分片上传**
**支持**|**类EventBus的task状态变更通知，支持三种线程的订阅模式**
**支持**|**任务分组，分用户**
**支持**|**传输速度限制(当前版本只支持下载限速)**
**支持**|**自定义的Http传输，数据库保存**
**支持**|**自动任务错误重试**
**支持**|**任务重命名(下载任务)**
**支持**|**小文件优先下载(dev 分支)**
**支持**|**multi/form-part上传 (dev 分支)**
待支持|**其他协议的传输**

[![](https://jitpack.io/v/shilec/Transer.svg)](https://jitpack.io/#shilec/Transer)

## 集成方式
1. 在project build.gradle 中加入

        allprojects {
            repositories {
                maven { url 'https://jitpack.io' }
            }
        }

2. 在app 的 build.gradle 中加入

         compile 'com.github.shilec:Transer:1.2-d'

3. 在app 的AndroidManifest.xml中 申明 TranserService

        <service android:name="com.scott.transer.TranserService"/>

4. 在 app 的 application 的 onCreate 中初始化TranserService

        TranserConfig config = new TranserConfig.Builder()
                .setDownloadConcurrentThreadSize(3)
                .setUploadConcurrentThreadSize(3)
                .build();

        TranserService.init(this,config);
        
## dev 分支为最新代码，新功能只是做了简单测试，后续多次测试验证后会同步到master,提供最新release版本。
### 如果需要最新功能，请直接依赖dev代码作为moudle，方便修改。

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

##### 服务端测试Demo详见：

- <a href="https://github.com/shilec/TranserServer">TranserServer</a>
