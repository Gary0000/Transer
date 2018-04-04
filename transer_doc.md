## 简单的下载或上传(不使用续传功能或者自己保存任务信息):

- ##### 下载 (示例代码 SmpleDownloadActivity)

````java

        //创建一个任务
        ITask task = new TaskBuilder()
                .setName("test.zip") //设置任务名称
                .setSourceUrl(URL)  //设置数据源
                .setDestUrl(FILE_PATH) //设置目标路径
                .build();

        ITaskHandler mHandler = new DefaultHttpDownloadHandler.Builder()
                .setTask(task)
                .addParam("path","test.zip")
                .setSpeedLimited(BaseTaskHandler.SPEED_LISMT.SPEED_1MB)
                .setCallback(new DownloadListener())
                .defaultThreadPool(3)
                .setEnableCoverFile(true)
                .build();

     mHandler.start() 或者
	 mHandler.stop()
````

- ##### 上传 (示例代码 SimpleUploadActivity)

````java
    task = new TaskBuilder()
                .setName("test.zip")
                .setSessionId("123123123131")
                .setSourceUrl(FILE_PATH)
                .setDestUrl(URL)
                .build();
    ITaskHandler mHandler = new DefaultHttpUploadHandler.Builder()
                .setTask(task)
                .addParam("path","test.zip")
                .setCallback(new UploadListenner())
                .defaultThreadPool(3)
                .build();
````


- ##### 使用注解TaskSubscriber监听回调

1. 初始化TaskEventBus

````java
        TaskEventBus.init(context); //在application 得 onCreate中
````

2. 创建TaskHandler

````java
        task = new TaskBuilder()
                .setName("test.zip")
                .setTaskId("1233444")
                .setSessionId("123123123131")
                .setSourceurl(FILE_PATH)
                .setDestUrl(URL)
                .build();

        ITaskHandler mHandler = new DefaultHttpUploadHandler.Builder()
                .setTask(task)
                .addParam("path","test.zip")
                .setEventDispatcher(TaskEventBus.getDefault().getDispatcher())
                .defaultThreadPool(3)
                .build();
````
> 注意:如果使用TaskSubscriber注解的方法来监听回调，该方法在使用任务管理时有任务变更也会受到通知，TaskScriber使用方法见下面 TaskEventBus 的使用


### 自定义ITaskHandler (示例代码 MyUploadHandler)

> 默认的DefaultHttpUploadHandler 将不会验证服务端的返回值，如果需要校验服务器的返回值用来判断当前片是否上传成功，需要自定义HttpUploadHandler,继承自 DefaultHttpUploadHandler

1. 自定义HttpUploadHandler

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
````


2. 自定义Handler 的Builder,用于增加自定义的参数或配置（示例代码 DefaultDownloadHandler.Builder)

> 在自定义的MyUploadHandler 中 建一个静态的子类 Builder 继承自 DefaultHttpUploadHandler.Builder,可以增加自定义的参数

````java
		public static class Builder extends DefaultHttpUploadHandler.Builder {
			
			private String arg;
        
        	public Builder setArg(String arg) {
            	this.arg = arg;
            	return this;
        	}

        	@Override
        	protected DefaultHttpUploadHandler buildTarget() {
           		return new MyUploadHandler();
        	}
    	}
````

### 使用任务管理器:

> 使用任务管理器去管理任务，会让任务列表运行在一个后台服务的线程池中，不会和V层直接交互，V 层也不能直接修改传输任务的信息，只能通过TaskCmd将控制命令发送给后台服务，后台服务会讲命令加入到命令队列。之后会由任务管理器派发任务命令到执行器去执行命令。通过EventDispatcher 中间层 将任务变更的通知分发到V 层 TaskSubScriber 标注的方法，

>下面文档将以上传文件为示例，下载文件操作相同。

-  配置传输服务

````java
         //在Application 的 onCreate 中(示例代码 BaseApplication)
		 TranserConfig config = new TranserConfig.Builder()
		                .setDownloadConcurrentThreadSize(3)
		                .setUploadConcurrentThreadSize(3)
		                .build();
		 TranserService.init(this,config);
		 TaskEventBus.init(this);
````

- 基本任务操作

1. 添加单个任务 (示例代码 CreateTaskActivity)

````java
			ITask task = new TaskBuilder()
		                .setTaskType(TaskType.HTTP_UPLOAD)  
		                .setSourceUrl(source)   
		                .setDestUrl(dest)   
		                .setName(NAME)           
		                .build();
		
		        ITaskCmd cmd = new TaskCmdBuilder()
		                .setProcessType(ProcessType.TYPE_ADD_TASK)
		                .setTask(task)
		                .build();
			TaskEventBus.getDefault().execute(cmd); 
````

2. 开始任务 (示例代码 TaskListRecyclerAdapter)

````java
		ITaskCmd cmd = new TaskCmdBuilder()
		               .setProcessType(ProcessType.TYPE_START_TASK) 
		               .setTask(task) 
		               .build();
		TaskEventBus.getDefault().execute(cmd); //执行命令
````


4. 结束/暂停 任务 (示例代码 TaskListRecyclerAdapter)

````java
		ITaskCmd cmd = new TaskCmdBuilder()
		               .setTaskType(task_type) 
		               .setProcessType(ProcessType.TYPE_STOP_TASK)
		               .build();
		TaskEventBus.getDefault().execute(cmd);
````
> 其他任务操作类型请查看ProcessType中的类型

- 接收任务变更通知 TaskEventBus 使用 ((示例代码 TaskFragment))

> 在Activity,Fragement,Service,Dialog 等 onResume 或 onStart 中:

````java
		TaskEventBus.getDefault().regesit(this);
````

> 在 Activity,Fragement,Service,Dialog 等onPause , onStop 中使用 

````java
		TaskEventBus.getDefault().unregesit(this);
````

1. 添加一个方法，参数为List<ITask> tasks,并且添加注解TaskSubscriber

> 这样会接受所有任务类型，所有操作类型的通知

````java
		@TaskSubscriber
		public void onTasksChanged(List<ITask> tasks) {
		       //TODO update ui on any processtype
		}
````

2. 只接受指定任务类型，指定操作类型，并且指定在哪个线程中去接收通知
	
````java
		@TaskSubscriber(taskType=TaskType.HTTP_UPLOAD,processType=ProcessType.ADD_TASK,threadMode=ThreadMode.MODE_MAIN)
		public void onTasksChanged(List<ITask> tasks) {
		       //TODO update ui on any processtype
		}
````
