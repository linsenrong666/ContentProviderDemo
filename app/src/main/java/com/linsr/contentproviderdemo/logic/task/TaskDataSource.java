package com.linsr.contentproviderdemo.logic.task;

import com.linsr.contentproviderdemo.data.contacts.TaskContact;

/**
 * Description
 @author Linsr
 */

public class TaskDataSource implements TaskContact {
    public static final String[] TASK_PROJECTION = {TASK_ID, TITLE, DESCRIPTION, IS_COMPLETED};


}
