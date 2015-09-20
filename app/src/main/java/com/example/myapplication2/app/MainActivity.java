package com.example.myapplication2.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.inject.Inject;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_main) public class MainActivity extends RoboActivity
{
    @InjectView(R.id.myListView) ListView listView;

    @InjectView(R.id.button) Button submitButton;

    @InjectView(R.id.button2) Button clearButton;

    @InjectView(R.id.textView) private TextView textView;

    @Inject HitListDaoManager hitListDaoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        submitButton.setOnClickListener(onClickListener());
        clearButton.setOnClickListener(onClickListener());
    }

    private View.OnClickListener onClickListener()
    {
        final Context context = this;
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v.equals(submitButton))
                {
                    final Intent intent =
                            new Intent(HitListParsingService.PARSE_HIT_LIST_ACTION, null, context, HitListParsingService.class);
                    intent.putExtra(HitListParsingService.HIT_LIST_URL_EXTRA, "http://triplejgizmo.abc.net.au:8080/jjj-hitlist/current/app/webroot/latest/play.txt");

                    startService(intent);
                }

                if (v.equals(clearButton))
                {
                    hitListDaoManager.clearAllHitListEntities();
                }
            }
        };
    }

}
