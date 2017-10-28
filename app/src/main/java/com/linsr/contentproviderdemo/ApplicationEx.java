package com.linsr.contentproviderdemo;

import android.app.Application;

import com.linsr.contentproviderdemo.logic.task.TaskManager;

/**
 * Description
 *
 * @author linsenrong on 2017/10/19 16:40
 */

public class ApplicationEx extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initManagers();

    }

    private void initManagers() {
        TaskManager taskManager = TaskManager.getInstance();
        taskManager.init(this);
    }
}
