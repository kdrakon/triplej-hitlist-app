package io.policarp.triplejhitlistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity
{
    @InjectView(R.id.hitListView)
    private RecyclerView hitListView;

    @InjectView(R.id.archivedListView)
    private RecyclerView archivedListView;

    @Inject
    @Named("recyclerListAdapterForHitList")
    private HitListRecyclerListAdapter hitListRecyclerListAdapter;

    @Inject
    @Named("recyclerListAdapterForArchivedHitList")
    private HitListRecyclerListAdapter archivedHitListRecyclerListAdapter;

    @Inject
    @Named("hitListGestureListener")
    private GestureDetector gestureDetector;

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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // route touch events to GestureDetector
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case (R.id.refresh):
                refreshHitList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshHitList()
    {
        final Intent intent =
                new Intent(HitListParsingService.PARSE_HIT_LIST_ACTION, null, this, HitListParsingService.class);
        intent.putExtra(HitListParsingService.HIT_LIST_URL_EXTRA, "http://triplejgizmo.abc.net.au:8080/jjj-hitlist/current/app/webroot/latest/play.txt");

        startService(intent);
    }
}
