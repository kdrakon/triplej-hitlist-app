package io.policarp.triplejhitlistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.policarp.triplejhitlistapp.dao.HitListDaoManager;
import io.policarp.triplejhitlistapp.parsing.HitListParsingService;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity
{
    @InjectView(R.id.mainToolbar)
    private Toolbar mainToolbar;

    @InjectView(R.id.hitListView)
    private RecyclerView hitListView;

    @InjectView(R.id.hitListViewRefreshLayout)
    private SwipeRefreshLayout hitListViewRefreshLayout;

    @InjectView(R.id.archivedListView)
    private RecyclerView archivedListView;

    @Inject
    @Named("tripleJHitListApi")
    private String tripleJHitListApi;

    @Inject
    @Named("recyclerListAdapterForHitList")
    private HitListRecyclerListAdapter hitListRecyclerListAdapter;

    @Inject
    @Named("recyclerListAdapterForArchivedHitList")
    private HitListRecyclerListAdapter archivedHitListRecyclerListAdapter;

    @Inject
    @Named("hitListGestureListener")
    private GestureDetector gestureDetector;

    @Inject
    private HitListDaoManager hitListDaoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        configureHitListViews();
    }

    private void configureHitListViews()
    {
        hitListView.setHasFixedSize(true);
        hitListView.setLayoutManager(new LinearLayoutManager(this));
        hitListView.setAdapter(hitListRecyclerListAdapter);
        hitListView.setItemViewCacheSize(hitListDaoManager.getActiveHitList().size());

        archivedListView.setHasFixedSize(true);
        archivedListView.setLayoutManager(new LinearLayoutManager(this));
        archivedListView.setAdapter(archivedHitListRecyclerListAdapter);

        // route touch events to GestureDetector
        View.OnTouchListener onTouchListener = new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return gestureDetector.onTouchEvent(event);
            }
        };
        hitListView.setOnTouchListener(onTouchListener);
        archivedListView.setOnTouchListener(onTouchListener);

        // configure swipe refresh
        hitListViewRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                refreshHitList();
                hitListViewRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshHitList()
    {
        final Intent intent =
                new Intent(HitListParsingService.PARSE_HIT_LIST_ACTION, null, this, HitListParsingService.class);
        intent.putExtra(HitListParsingService.HIT_LIST_URL_EXTRA, tripleJHitListApi);

        startService(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // route touch events to GestureDetector
        return gestureDetector.onTouchEvent(event);
    }
}
