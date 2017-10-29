package com.linsr.contentproviderdemo.logic.task;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.linsr.contentproviderdemo.data.Tasks;
import com.linsr.contentproviderdemo.data.contacts.TaskContact;
import com.linsr.contentproviderdemo.model.Task;
import com.linsr.contentproviderdemo.utils.DemoUtils;

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
        if (mInstance == null) {
            synchronized (TaskManager.class) {
                if (mInstance == null) {
                    mInstance = new TaskManager();
                }
            }
        }
        return mInstance;
    }

    private ContentResolver mContentResolver;

    public void init(Context context) {
        mContentResolver = context.getContentResolver();
    }

    public void loadTasks(boolean flag) {

    }

    public void saveTask(String taskId, String title, String desc) {
        ContentValues values = new ContentValues();
        values.put(Tasks.Task.TITLE, title);
        values.put(Tasks.Task.DESCRIPTION, desc);
        values.put(Tasks.Task.IS_COMPLETED, Tasks.Task.NOT_COMPLETED);
        if (DemoUtils.isEmpty(taskId)) {
            values.put(Tasks.Task.TASK_ID, DemoUtils.getUUID());
            mContentResolver.insert(Tasks.Task.CONTENT_URI, values);
        } else {
            mContentResolver.update(Tasks.Task.CONTENT_URI, values,
                    Tasks.Task.TASK_ID + " = ?",
                    new String[]{taskId});
        }

    }

    public void completeTask(Task completedTask) {
        ContentValues values = new ContentValues();
        values.put(TaskContact.IS_COMPLETED, Tasks.Task.COMPLETED);
        mContentResolver.update(Tasks.Task.CONTENT_URI, values,
                TaskDataSource.TASK_ID + " = ?",
                new String[]{completedTask.getId()});
    }

    public void activateTask(Task activatedTask) {
        ContentValues values = new ContentValues();
        values.put(TaskContact.IS_COMPLETED, Tasks.Task.NOT_COMPLETED);
        mContentResolver.update(Tasks.Task.CONTENT_URI, values,
                TaskDataSource.TASK_ID + " = ?",
                new String[]{activatedTask.getId()});
    }
}
