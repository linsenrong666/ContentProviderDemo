package com.linsr.contentproviderdemo.gui.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.linsr.contentproviderdemo.R;
import com.linsr.contentproviderdemo.base.BaseFragment;
import com.linsr.contentproviderdemo.data.Tasks;
import com.linsr.contentproviderdemo.data.contacts.TaskContact;
import com.linsr.contentproviderdemo.gui.activities.AddEditTaskActivity;
import com.linsr.contentproviderdemo.gui.adapters.TasksAdapter;
import com.linsr.contentproviderdemo.gui.widgets.ScrollChildSwipeRefreshLayout;
import com.linsr.contentproviderdemo.logic.task.TaskDataSource;
import com.linsr.contentproviderdemo.logic.task.TaskManager;
import com.linsr.contentproviderdemo.logic.task.TasksFilterType;
import com.linsr.contentproviderdemo.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 *
 * @author linsenrong on 2017/10/19 17:15
 */
public class TasksFragment extends BaseFragment {

    private static final int EMPTY = 0;
    private static final int ERROR = 1;
    private static final int NORMAL = 2;

    private TasksAdapter mListAdapter;

    private View mNoTasksView;

    private ImageView mNoTaskIcon;

    private TextView mNoTaskMainView;

    private TextView mNoTaskAddView;

    private LinearLayout mTasksView;

    private TextView mFilteringLabelView;

    private TaskManager mTaskManager;
    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    public static TasksFragment newInstance() {

        Bundle args = new Bundle();

        TasksFragment fragment = new TasksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TasksAdapter(new ArrayList<Task>(0), mItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTaskManager = TaskManager.getInstance();
        View root = inflater.inflate(R.layout.tasks_frag, container, false);

        // Set up tasks view
        ListView listView = (ListView) root.findViewById(R.id.tasks_list);
        listView.setAdapter(mListAdapter);
        mFilteringLabelView = (TextView) root.findViewById(R.id.filteringLabel);
        mTasksView = (LinearLayout) root.findViewById(R.id.tasksLL);

        // Set up  no tasks view
        mNoTasksView = root.findViewById(R.id.noTasks);
        mNoTaskIcon = (ImageView) root.findViewById(R.id.noTasksIcon);
        mNoTaskMainView = (TextView) root.findViewById(R.id.noTasksMain);
        mNoTaskAddView = (TextView) root.findViewById(R.id.noTasksAdd);
        mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTask();
            }
        });

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTask();
            }
        });

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTaskManager.loadTasks(false);
            }
        });

        //set loaderManager
        getLoaderManager().initLoader(0, null, mLoaderCallbacks);

        setHasOptionsMenu(true);
        return root;
    }

    private void setUIStatus(int status) {
        showFilterLabel();
        switch (status) {
            case NORMAL:
                mNoTasksView.setVisibility(View.GONE);
                mTasksView.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                mTasksView.setVisibility(View.GONE);
                mNoTasksView.setVisibility(View.VISIBLE);
                break;
            case EMPTY:
                mTasksView.setVisibility(View.GONE);
                mNoTasksView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getLoaderManager() != null) {
            getLoaderManager().destroyLoader(0);
        }
    }

    private void addNewTask() {
        Intent intent = new Intent(getActivity(), AddEditTaskActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
//                mTaskManager.clearCompletedTasks();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mTaskManager.loadTasks(true);
                break;
        }
        return true;
    }

    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.active:
                        mCurrentFiltering = TasksFilterType.ACTIVE_TASKS;
                        break;
                    case R.id.completed:
                        mCurrentFiltering = TasksFilterType.COMPLETED_TASKS;
                        break;
                    default:
                        mCurrentFiltering = TasksFilterType.ALL_TASKS;
                        break;
                }
                getLoaderManager().restartLoader(0, null, mLoaderCallbacks);
                return true;
            }
        });

        popup.show();
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mFilteringLabelView.setText(getResources().getString(R.string.label_active));
                break;
            case COMPLETED_TASKS:
                mFilteringLabelView.setText(getResources().getString(R.string.label_completed));
                break;
            default:
                mFilteringLabelView.setText(getResources().getString(R.string.label_all));
                break;
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    String selection = null;
                    String[] selectionArgs = null;
                    switch (mCurrentFiltering) {
                        case ACTIVE_TASKS:
                            selection = TaskContact.IS_COMPLETED + " = ?";
                            selectionArgs = new String[]{String.valueOf(Tasks.Task.NOT_COMPLETED)};
                            break;
                        case COMPLETED_TASKS:
                            selection = TaskContact.IS_COMPLETED + " = ?";
                            selectionArgs = new String[]{String.valueOf(Tasks.Task.COMPLETED)};
                            break;
                        default:
                            break;
                    }
                    return new CursorLoader(getActivity(), Tasks.Task.CONTENT_URI,
                            TaskDataSource.TASK_PROJECTION, selection, selectionArgs, null);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    List<Task> list = new ArrayList<>();
                    while (data.moveToNext()) {
                        Task task = new Task();
                        String title = data.getString(data.getColumnIndex(TaskDataSource.TITLE));
                        String desc = data.getString(data.getColumnIndex(TaskDataSource.DESCRIPTION));
                        task.setTitle(title);
                        task.setDescription(desc);
                        task.setId(data.getString(data.getColumnIndex(TaskDataSource.TASK_ID)));
                        task.setCompleted(data.getInt(data.getColumnIndex(TaskDataSource.IS_COMPLETED))
                        == Tasks.Task.COMPLETED);
                        list.add(task);
                    }
                    mListAdapter.replaceData(list);
                    setUIStatus(mListAdapter.getCount() == 0 ? EMPTY : NORMAL);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {

                }
            };

    TasksAdapter.TaskItemListener mItemListener = new TasksAdapter.TaskItemListener() {
        @Override
        public void onTaskClick(Task clickedTask) {
//            mPresenter.openTaskDetails(clickedTask);
        }

        @Override
        public void onCompleteTaskClick(Task completedTask) {
            mTaskManager.completeTask(completedTask);
            showMessage(getString(R.string.task_marked_complete));
        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {
            mTaskManager.activateTask(activatedTask);
        }
    };

}
