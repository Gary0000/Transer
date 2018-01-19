package com.scott.transer;

import com.scott.annotionprocessor.ITask;
import com.scott.transer.handler.ITaskHandlerCallback;

/**
 * <p>Author:    shijiale</p>
 * <p>Date:      2017-12-18 16:54</p>
 * <p>Email:     shilec@126.com</p>
 * <p>Describe:</p>
 */

public class SimpleTaskHandlerListenner implements ITaskHandlerCallback {

    @Override
    public void onReady(ITask task) {

    }

    @Override
    public void onStart(ITask params) {

    }

    @Override
    public void onStop(ITask params) {

    }

    @Override
    public void onError(int code, ITask params) {

    }

    @Override
    public void onSpeedChanged(long speed, ITask params) {

    }

    @Override
    public void onPiceSuccessful(ITask params) {

    }

    @Override
    public void onFinished(ITask task) {

    }
}
