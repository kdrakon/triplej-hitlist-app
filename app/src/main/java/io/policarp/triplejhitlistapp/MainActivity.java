package io.policarp.triplejhitlistapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.ViewFlipper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity
{
    @InjectView(R.id.hitListView)
    RecyclerView hitListView;

    @InjectView(R.id.archivedListView)
    RecyclerView archivedListView;

    @InjectView(R.id.viewflipper)
    ViewFlipper viewFlipper;

    @Inject
    HitListDaoManager hitListDaoManager;

    @Inject
    @Named("recyclerListAdapterForHitList")
    HitListRecyclerListAdapter hitListRecyclerListAdapter;

    @Inject
    @Named("recyclerListAdapterForArchivedHitList")
    HitListRecyclerListAdapter archivedHitListRecyclerListAdapter;

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

        final Context context = this;
        View.OnTouchListener onTouchListener = new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getHistorySize() > 3)
                {
                    float prevPos = event.getHistoricalAxisValue(MotionEvent.AXIS_X, 3);
                    float curPos = event.getAxisValue(MotionEvent.AXIS_X);

                    System.out.println(prevPos);
                    System.out.println(curPos);

                    if (curPos < prevPos)
                    {
                        viewFlipper.setInAnimation(context, R.anim.slide_left);
                        viewFlipper.showNext();
                    }

                    if (curPos > prevPos)
                    {
                        viewFlipper.setOutAnimation(context, R.anim.slide_right);
                        viewFlipper.showPrevious();
                    }
                }

                return false;
            }
        };

        hitListView.setOnTouchListener(onTouchListener);
        archivedListView.setOnTouchListener(onTouchListener);
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
