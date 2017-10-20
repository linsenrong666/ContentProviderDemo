package com.linsr.contentproviderdemo.data.contacts;

import android.provider.BaseColumns;

/**
 * Description
 *
 * @author linsenrong on 2017/10/20 12:01
 */

public interface TaskContact extends BaseColumns {

    String TABLE_NAME = "biz_task";

    String TASK_ID = "task_id";

    String TITLE = "task_title";

    String DESCRIPTION = "task_description";

    String IS_COMPLETED = "is_completed";
}
