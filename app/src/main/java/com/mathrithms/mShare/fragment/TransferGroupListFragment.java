package com.mathrithms.mShare.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mathrithms.mShare.R;
import com.mathrithms.mShare.activity.ConnectionManagerActivity;
import com.mathrithms.mShare.activity.ConnectionManagerRecieveActivity;
import com.mathrithms.mShare.activity.ContentSharingActivity;
import com.mathrithms.mShare.activity.ViewTransferActivity;
import com.mathrithms.mShare.adapter.TransferGroupListAdapter;
import com.mathrithms.mShare.app.EditableListFragment;
import com.mathrithms.mShare.app.EditableListFragmentImpl;
import com.mathrithms.mShare.app.GroupEditableListFragment;
import com.mathrithms.mShare.database.AccessDatabase;
import com.mathrithms.mShare.service.CommunicationService;
import com.mathrithms.mShare.ui.callback.IconSupport;
import com.mathrithms.mShare.ui.callback.TitleSupport;
import com.mathrithms.mShare.util.AppUtils;
import com.mathrithms.mShare.widget.GroupEditableListAdapter;
import com.genonbeta.android.database.SQLQuery;
import com.genonbeta.android.framework.widget.PowerfulActionMode;

import java.util.ArrayList;
import java.util.Map;

/**
 * created by: Veli
 * date: 10.11.2017 00:15
 */

public class TransferGroupListFragment
        extends GroupEditableListFragment<TransferGroupListAdapter.PreloadedGroup, GroupEditableListAdapter.GroupViewHolder, TransferGroupListAdapter>
        implements IconSupport, TitleSupport
{
    private SQLQuery.Select mSelect;
    private IntentFilter mFilter = new IntentFilter();
    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (AccessDatabase.ACTION_DATABASE_CHANGE.equals(intent.getAction())
                    && intent.hasExtra(AccessDatabase.EXTRA_TABLE_NAME)
                    && (intent.getStringExtra(AccessDatabase.EXTRA_TABLE_NAME).equals(AccessDatabase.TABLE_TRANSFERGROUP)
                    || intent.getStringExtra(AccessDatabase.EXTRA_TABLE_NAME).equals(AccessDatabase.TABLE_TRANSFER)
            ))
                refreshList();
            else if (CommunicationService.ACTION_TASK_RUNNING_LIST_CHANGE.equals(intent.getAction())
                    && intent.hasExtra(CommunicationService.EXTRA_TASK_LIST_RUNNING)) {
                getAdapter().updateActiveList(intent.getLongArrayExtra(CommunicationService.EXTRA_TASK_LIST_RUNNING));
                refreshList();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setFilteringSupported(true);
        setDefaultOrderingCriteria(TransferGroupListAdapter.MODE_SORT_ORDER_DESCENDING);
        setDefaultSortingCriteria(TransferGroupListAdapter.MODE_SORT_BY_DATE);
        //setDefaultGroupingCriteria(TransferGroupListAdapter.MODE_GROUP_BY_DATE);
        setDefaultSelectionCallback(new SelectionCallback(this));
        setUseDefaultPaddingDecoration(true);
        setUseDefaultPaddingDecorationSpaceForEdges(true);
        setDefaultPaddingDecorationSize(getResources().getDimension(R.dimen.padding_list_content_parent_layout));
    }

    @Override
    protected RecyclerView onListView(View mainContainer, ViewGroup listViewContainer)
    {
        @SuppressLint("InflateParams") View adaptedView = getLayoutInflater().inflate(R.layout.layout_transfer_group_list, null, false);
        ((ViewGroup) mainContainer).addView(adaptedView);

        return super.onListView(mainContainer, (FrameLayout) adaptedView.findViewById(R.id.fragmentContainer));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //setEmptyImage(R.drawable.centre_image);
        //setEmptyText(getString(R.string.ms_nothing_to_show));

        View viewSend = view.findViewById(R.id.sendLayoutButton);
        View viewReceive = view.findViewById(R.id.receiveLayoutButton);

        viewSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(), ContentSharingActivity.class)
                        .putExtra(ConnectionManagerActivity.EXTRA_ACTIVITY_SUBTITLE, getString(R.string.ms_send)));
            }
        });

        viewReceive.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(), ConnectionManagerRecieveActivity.class)
                        .putExtra(ConnectionManagerRecieveActivity.EXTRA_ACTIVITY_SUBTITLE, getString(R.string.ms_recieve))
                        .putExtra(ConnectionManagerRecieveActivity.EXTRA_REQUEST_TYPE, ConnectionManagerActivity.RequestType.MAKE_ACQUAINTANCE.toString()));
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mFilter.addAction(AccessDatabase.ACTION_DATABASE_CHANGE);
        mFilter.addAction(CommunicationService.ACTION_TASK_RUNNING_LIST_CHANGE);

        if (getSelect() == null)
            setSelect(new SQLQuery.Select(AccessDatabase.TABLE_TRANSFERGROUP));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(super.getActivity() != null){
            getActivity().registerReceiver(mReceiver, mFilter);
        }

        AppUtils.startForegroundService(getActivity(), new Intent(getActivity(), CommunicationService.class)
                .setAction(CommunicationService.ACTION_REQUEST_TASK_RUNNING_LIST_CHANGE));
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(getActivity() != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onSortingOptions(Map<String, Integer> options)
    {
        options.put(getString(R.string.text_sortByDate), TransferGroupListAdapter.MODE_SORT_BY_DATE);
        options.put(getString(R.string.text_sortBySize), TransferGroupListAdapter.MODE_SORT_BY_SIZE);
    }

    @Override
    public TransferGroupListAdapter onAdapter()
    {
        final AppUtils.QuickActions<GroupEditableListAdapter.GroupViewHolder> quickActions = new AppUtils.QuickActions<GroupEditableListAdapter.GroupViewHolder>()
        {
            @Override
            public void onQuickActions(final GroupEditableListAdapter.GroupViewHolder clazz)
            {
                if (!clazz.isRepresentative()) {
                    registerLayoutViewClicks(clazz);

                    clazz.getView().findViewById(R.id.layout_image).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (getSelectionConnection() != null)
                                getSelectionConnection().setSelected(clazz.getAdapterPosition());
                        }
                    });
                }
            }
        };

        return new TransferGroupListAdapter(getActivity(), AppUtils.getDatabase(getContext()))
        {
            @NonNull
            @Override
            public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                return AppUtils.quickAction(super.onCreateViewHolder(parent, viewType), quickActions);
            }
        }.setSelect(getSelect());
    }

    @Override
    public boolean onDefaultClickAction(GroupEditableListAdapter.GroupViewHolder holder)
    {
        try {
            if(getActivity() != null) {
                ViewTransferActivity.startInstance(getActivity(), getAdapter().getItem(holder).groupId);
            }
            return true;
        } catch (Exception e) {
        }

        return false;
    }

    @Override
    public int getIconRes()
    {
        return R.drawable.ic_swap_vert_white_24dp;
    }

    @Override
    public CharSequence getTitle(Context context)
    {
        return context.getString(R.string.text_transfers);
    }

    public SQLQuery.Select getSelect()
    {
        return mSelect;
    }

    public TransferGroupListFragment setSelect(SQLQuery.Select select)
    {
        mSelect = select;
        return this;
    }

    private static class SelectionCallback extends EditableListFragment.SelectionCallback<TransferGroupListAdapter.PreloadedGroup>
    {
        public SelectionCallback(EditableListFragmentImpl<TransferGroupListAdapter.PreloadedGroup> fragment)
        {
            super(fragment);
        }

        @Override
        public boolean onPrepareActionMenu(Context context, PowerfulActionMode actionMode)
        {
            super.onPrepareActionMenu(context, actionMode);
            return true;
        }

        @Override
        public boolean onCreateActionMenu(Context context, PowerfulActionMode actionMode, Menu menu)
        {
            super.onCreateActionMenu(context, actionMode, menu);
            actionMode.getMenuInflater().inflate(R.menu.action_mode_group, menu);
            return true;
        }

        @Override
        public boolean onActionMenuItemSelected(Context context, PowerfulActionMode actionMode, MenuItem item)
        {
            int id = item.getItemId();

            ArrayList<TransferGroupListAdapter.PreloadedGroup> selectionList = new ArrayList<>(getFragment().getSelectionConnection().getSelectedItemList());

            if (id == R.id.action_mode_group_delete)
                AppUtils.getDatabase(getFragment().getContext())
                        .removeAsynchronous(getFragment().getActivity(), selectionList);

            return true;
        }
    }
}