package com.linsr.contentproviderdemo.gui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linsr.contentproviderdemo.R;

/**
 * Description
 *
 * @author linsenrong on 2017/10/24 16:07
 */

public class AddEditTaskFragment extends Fragment {

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    public static AddEditTaskFragment newInstance() {

        Bundle args = new Bundle();

        AddEditTaskFragment fragment = new AddEditTaskFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public AddEditTaskFragment() {
        // Required empty public constructor
    }

    private TextView mTitle;

    private TextView mDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.addtask_frag, container, false);
        mTitle = (TextView) root.findViewById(R.id.add_task_title);
        mDescription = (TextView) root.findViewById(R.id.add_task_description);
        setHasOptionsMenu(true);
        return root;
    }

}
