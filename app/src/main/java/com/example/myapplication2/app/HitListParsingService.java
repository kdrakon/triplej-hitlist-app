package com.example.myapplication2.app;

import android.content.Intent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.inject.Inject;
import roboguice.service.RoboIntentService;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListParsingService extends RoboIntentService
{
    public static final String PARSE_HIT_LIST_ACTION = "PARSE_HIT_LIST";
    public static final String CANCEL_PARSE_HIT_LIST_ACTION = "CANCEL_PARSE_HIT_LIST";
    public static final String HIT_LIST_URL_EXTRA = "HIT_LIST_URL";

    @Inject
    private HitListParsingListener hitListParsingListener;

    public HitListParsingService()
    {
        super(HitListParsingService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        switch (intent.getAction())
        {
            case PARSE_HIT_LIST_ACTION:
                startParseRequest(intent);
                break;

            case CANCEL_PARSE_HIT_LIST_ACTION:
                cancelParseRequest();
                break;
        }
    }

    private RequestQueue getRequestQueue()
    {
        return Volley.newRequestQueue(this);
    }

    private void startParseRequest(Intent intent)
    {
        String hitListUrl = intent.getStringExtra(HIT_LIST_URL_EXTRA);
        StringRequest request = new StringRequest(Request.Method.GET, hitListUrl, hitListParsingListener, hitListParsingListener);
        request.setTag(PARSE_HIT_LIST_ACTION);

        getRequestQueue().add(request);
    }

    private void cancelParseRequest()
    {
        getRequestQueue().cancelAll(PARSE_HIT_LIST_ACTION);
    }
}
