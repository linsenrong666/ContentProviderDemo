package com.linsr.contentproviderdemo.data;

import android.net.Uri;

import com.linsr.contentproviderdemo.data.contacts.TaskContact;

/**
 * Description
 *
 * @author linsenrong on 2017/10/20 14:10
 */

public class Tasks {
    public static final String AUTHORITY = "com.linsr.contentproviderdemo.data.Tasks";

    static class TasksBase {

    }

    public static class Task extends TasksBase implements TaskContact {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/Task");
    }

}
