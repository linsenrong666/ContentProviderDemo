package com.linsr.contentproviderdemo.logic.task;

/**
 * Description
 *
 * @author linsenrong on 2017/10/19 16:40
 */

public class TaskManager {
    private TaskManager() {
    }

    private volatile static TaskManager mInstance;

    public static TaskManager getInstance() {
        if (mInstance != null) {
            synchronized (TaskManager.class) {
                if (mInstance != null) {
                    mInstance = new TaskManager();
                }
            }
        }
        return mInstance;
    }

    public void loadTasks(boolean flag) {

    }

}
