package io.policarp.triplejhitlistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity
{
    @InjectView(R.id.hitListView)
    RecyclerView hitListView;

    @Inject
    HitListDaoManager hitListDaoManager;
    @Inject
    HitListRecyclerListAdapter hitListRecyclerListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        configureHitListView();
    }

    private void configureHitListView()
    {
        hitListView.setHasFixedSize(true);
        hitListView.setLayoutManager(new LinearLayoutManager(this));
        hitListView.setAdapter(hitListRecyclerListAdapter);
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
