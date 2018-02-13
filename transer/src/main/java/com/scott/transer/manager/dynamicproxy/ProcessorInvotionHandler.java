package com.scott.transer.manager.dynamicproxy;

import com.scott.annotionprocessor.ITask;
import com.scott.annotionprocessor.ProcessType;
import com.scott.annotionprocessor.TaskType;
import com.scott.transer.ITaskCmd;
import com.scott.transer.TaskCmdBuilder;
import com.scott.transer.manager.ITaskManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <P>Author: shijiale</P>
 * <P>Date: 2018/2/6</P>
 * <P>Email: shilec@126.com</p>
 */

public class ProcessorInvotionHandler implements InvocationHandler {

    //从内部获得 TaskManager ,直接调用 TaskManager 的 process 方法，分发命令
    private ITaskManager mManager;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        ITaskCmd cmd = null;
        switch (method.getName()) {
            case "setTaskManager":
                if(mManager == null) {
                    mManager = (ITaskManager) args[0];
                }
                break;
            case "addTask":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_ADD_TASK)
                        .setTask((ITask) args[0])
                        .build();
                break;
            case "addTasks":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_ADD_TASKS)
                        .setTasks((List<ITask>) args[0])
                        .build();
                break;
            case "deleteTask":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_DELETE_TASK)
                        .setTaskId(args[0].toString())
                        .build();
                break;
            case "deleteGroup":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_DELETE_TASKS_GROUP)
                        .setGroupId(args[0].toString())
                        .build();
                break;
            case "deleteTasks":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_DELETE_TASKS_SOME)
                        .setTaskIds((String[]) args[0])
                        .build();
                break;
            case "deleteCompleted":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_DELETE_TASKS_COMPLETED)
                        .setTaskType((TaskType) args[0])
                        .build();
                break;
            case "delete":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_DELETE_TASKS_STATE)
                        .setState((Integer) args[0])
                        .setTaskType((TaskType) args[1])
                        .build();
                break;
            case "deleteAll":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_DELETE_TASKS_ALL)
                        .build();
                break;
            case "getTask":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_QUERY_TASK)
                        .setTaskId((String) args[0])
                        .build();
                break;
            case "getGroup":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_QUERY_TASKS_GROUP)
                        .setGroupId((String) args[0])
                        .build();
                break;
            case "getAllTasks":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_DELETE_TASKS_ALL)
                        .setTaskType((TaskType) args[0])
                        .build();
                break;
            case "getTasks":
                if(args.length == 2) {
                    cmd = new TaskCmdBuilder()
                            .setProcessType(ProcessType.TYPE_QUERY_TASKS_STATE)
                            .setState((Integer) args[0])
                            .setTaskType((TaskType) args[1])
                            .build();
                } else if(args.length == 1) {
                    cmd = new TaskCmdBuilder()
                            .setProcessType(ProcessType.TYPE_QUERY_TASKS_SOME)
                            .setTaskIds((String[]) args[0])
                            .build();
                }
                break;
            case "start":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_START_TASK)
                        .setTaskId((String) args[0])
                        .build();
                break;
            case "startGroup":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_START_GROUP)
                        .setGroupId((String) args[0])
                        .build();
                break;
            case "startAll":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_START_ALL)
                        .setTaskType((TaskType) args[0])
                        .build();
                break;
            case "stop":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_STOP_TASK)
                        .setTaskId((String) args[0])
                        .build();
                break;
            case "stopGroup":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_STOP_GROUP)
                        .setGroupId((String) args[0])
                        .build();
                break;
            case "stopAll":
                cmd = new TaskCmdBuilder()
                        .setProcessType(ProcessType.TYPE_STOP_ALL)
                        .setTaskType((TaskType) args[0])
                        .build();
                break;
            default:
                cmd = null;
        }

        if(mManager == null || cmd == null) {
            return null;
        }

        mManager.process(cmd);
        return null;
    }

}
