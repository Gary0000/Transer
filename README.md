# Transer
是一个传输框架,目前支持：
- 支持 HTTP/HTTPS 断点续传下载
- 支持 HTTP/HTTPS 大文件分片上传
- 支持 类EventBus的task状态变更通知，支持三种线程的订阅模式
- 支持 任务分组，分用户
- 支持 传输速度限制(当前版本只支持下载限速)

## 设计
<img src="/imgs/design.png" alt="架构" width="80%" height="500"/>

## 简单的下载或上传(不使用续传功能或者自己保存任务信息):

下载 (示例代码 SmpleDownloadActivity)
```` java 
   //创建一个任务
        ITask task = new TaskBuilder()
                .setName("test.zip") //设置任务名称
                .setDataSource(URL)  //设置数据源
                .setDestSource(FILE_PATH) //设置目标路径
                .build();

        ITaskHandler mHandler = new DefaultHttpDownloadHandler.Builder()
                .setTask(task)
                .addParam("path","test.zip")
                .setSpeedLimited(BaseTaskHandler.SPEED_LISMT.SPEED_1MB)
                .setCallback(new DownloadListener())
                .defaultThreadPool(3)
                .setEnableCoverFile(true)
                .build();
       
        //开始任务
        mHandler.start();
        
        //停止/暂停 任务
        mHandler.stop();
````
上传 (示例代码 SimpleUploadActivity)
```` java 
   task = new TaskBuilder()
                .setName("test.zip")
                .setTaskId("1233444")
                .setSessionId("123123123131")
                .setDataSource(FILE_PATH)
                .setDestSource(URL)
                .build();

   ITaskHandler mHandler = new DefaultHttpUploadHandler.Builder()
                .setTask(task)
                .addParam("path","test.zip")
                .setCallback(new UploadListenner())
                .defaultThreadPool(3)
                .build();
````

使用TaskEventBus代替ITaskHandlerCallback (示例代码 SimpleTaskEventBusActivity)
- 初始化TaskEventBus
````java
TaskEventBus.init(context); //在application 得 onCreate中
````
- 创建TaskHandler
````java 
task = new TaskBuilder()
                .setName("test.zip")
                .setTaskId("1233444")
                .setSessionId("123123123131")
                .setDataSource(FILE_PATH)
                .setDestSource(URL)
                .build();

 ITaskHandler mHandler = new DefaultHttpUploadHandler.Builder()
                .setTask(task)
                .addParam("path","test.zip")
                //.setCallback(new UploadListenner())
                .setEventDispatcher(TaskEventBus.getDefault().getDispatcher()) //设置EventDispatcher,
                .defaultThreadPool(3)
                .build();
        setTitle(getString(R.string.simple_upload));
 
 ````
 - 见下面 TaskEventBus 的使用

## 自定义ITaskHandler (示例代码 MyUploadHandler)
- 默认的handler将不会验证服务端的返回值，继承DefaultDownloadHandler 或 DefaultUploadHandler 适配服务端返回值的验证
````java
public class MyUploadHandler extends DefaultHttpUploadHandler {

    @Override
    public boolean isPiceSuccessful() {
        try{
            String response = getNowResponse();
            JSONObject jObj = new JSONObject(response);
            int code = jObj.optInt("code");
            if(code == 1 || isSuccessful()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean isSuccessful() {
        try {
            String response = getNowResponse();
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.optInt("code");
            if(code == 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}

mHandler = new MyUploadHandler.Builder()
                .setTask(task)
                .addParam("path","test.zip")
                .setCallback(new UploadListenner())
                .defaultThreadPool(3)
                .build();
````
### 自定义Handler 的Builder,用于增加自定义的参数或配置（示例代码 DefaultDownloadHandler.Builder)
````java
public static class Builder extends BaseTaskHandler.Builder<Builder,MyUploadHandler> {

        private boolean isEnableCoverfile;
        private long mSpeedLimited;

        public Builder setSpeedLimited(long limited) {
            mSpeedLimited = limited;
            return this;
        }

        public Builder setEnableCoverFile(boolean enable){
            isEnableCoverfile = enable;
            return this;
        }

        @Override
        protected MyUploadHandler buildTarget() {
            MyUploadHandler handler = new MyUploadHandler();
            handler.isCoverOldFile = isEnableCoverfile;
            handler.mLimitSpeed = mSpeedLimited;
            return handler;
        }
    }
````

## 使用任务管理:
1.配置传输服务
- 在Application 的 onCreate 中(示例代码 BaseApplication)
````java
 TranserConfig config = new TranserConfig.Builder()
                .setDownloadConcurrentThreadSize(3)
                .setUploadConcurrentThreadSize(3)
                .build();
        TranserService.init(this,config);
````

2.添加单个任务 (示例代码 CreateTaskActivity)

```` java
ITask task = new TaskBuilder()
                .setTaskType(task_type)  //任务类型
                .setDataSource(source)   //任务数据源 (下在任务为要下载的服务文件链接，上传任务为要上传的本地文件路径)
                .setDestSource(dest)     //任务目标源 (下载任务为保存的本地路径，上传任务为服务器地址)
                .setName(NAME)           //任务名称
                .build();

        ITaskCmd cmd = new TaskCmdBuilder()
                .setTaskType(task_type) //任务类型
                .setProcessType(ProcessType.TYPE_ADD_TASK) //操作类型(添加任务)
                .setTask(task) //任务信息
                .build();

        TaskEventBus.getDefault().execute(cmd); //执行命令
````
3.开始任务 (示例代码 TaskListRecyclerAdapter)

```` java
        ITaskCmd cmd = new TaskCmdBuilder()
                .setTaskType(task_type) //任务类型
                .setProcessType(ProcessType.TYPE_START_TASK) //操作类型(修改任务状态)
                .setTask(task) //任务信息
                .build();

        TaskEventBus.getDefault().execute(cmd); //执行命令
````
4.结束/暂停 任务 (示例代码 TaskListRecyclerAdapter)
```` java
        ITaskCmd cmd = new TaskCmdBuilder()
                .setTaskType(task_type) //任务类型
                .setProcessType(ProcessType.TYPE_STOP_TASK) //操作类型(修改任务状态)
                .setTask(task) //任务信息
                .build();

        TaskEventBus.getDefault().execute(cmd); //执行命令
````
- 其他命令,详见ProcessType 中支持的 type 类型


5.接收任务变更通知 TaskEventBus 使用 ((示例代码 TaskFragment))
- 在Activity,Fragement,Service,Dialog 等 onResume 或 onStart 中:

````java
TaskEventBus.getDefault().regesit(this);
````
- 在 onPause , onStop 中使用 

````java
TaskEventBus.getDefault().unregesit(this);
````
- 添加一个方法，参数为List<ITask> tasks,并且添加注解TaskSubscriber

````java 
@TaskSubscriber
public void onTasksChanged(List<ITask> tasks) {
       //TODO update ui on any processtype
}
````

- TaskScriber
> 默认情况下TaskScriber 会接受所有任务变更的消息，也可以指定只接受某个操作的消息例如：

````java
@TaskSubscriber(taskType = TYPE_DOWNLOAD, processType = TYPE_CHANGE_TASK)
public void onTasksChanged(List<ITask> tasks) {
       //TODO update ui in posting thread
}
````
>也可以指定消息接收的线程，默认为发送消息的线程，例如:

````java
@TaskSubscriber(taskType = TYPE_DOWNLOAD, processType = TYPE_CHANGE_TASK,threadMode = ThreadMode.MODE_MAIN)
public void onTasksChanged(List<ITask> tasks) {
       //TODO update ui in main thread
}
````
开源库使用:</br>

<a href="http://jakewharton.github.io/butterknife/">ButterKnife</a></br>
<a href="https://github.com/yanzhenjie/AndPermission">AndPermission</a></br>
<a href="https://github.com/square/retrofit">Retrofit</a></br>
<a href="https://github.com/ReactiveX/RxAndroid">RxAndroid</a></br>
<a href="https://github.com/ReactiveX/RxJava">RxJava</a></br>
<a href="https://github.com/CymChad/BaseRecyclerViewAdapterHelper">BaseRecyclerViewAdapterHelper</a></br>
<a href="https://github.com/greenrobot/greenDAO">GreenDao</a></br>
<a href="https://github.com/square/okhttp">OkHttp</a></br>

功能更新日志:
- 2017/1/2 添加下载限速，设置分片大小
- 2017/1/21 简化传输器配置，修复部分bug

接下来将会增加的功能：
- 其他方式的文件传输支持
- 优化性能

服务端测试Demo详见：
- https://github.com/shilec/WebDemo
