package io.policarp.triplejhitlistapp.parsing;

import android.content.Intent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import roboguice.service.RoboIntentService;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListParsingService extends RoboIntentService
{
    public static final String PARSE_HIT_LIST_ACTION = "PARSE_HIT_LIST";
    public static final String PARSE_HIT_LIST_COMPLETE_ACTION = "PARSE_HIT_LIST_COMPLETE";
    public static final String CANCEL_PARSE_HIT_LIST_ACTION = "CANCEL_PARSE_HIT_LIST";
    public static final String HIT_LIST_URL_EXTRA = "HIT_LIST_URL";

    @Inject
    private HitListParsingListener hitListParsingListener;

    @Inject
    @Named("globalRequestQueue")
    private RequestQueue globalRequestQueue;

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

    private void startParseRequest(Intent intent)
    {
        String hitListUrl = intent.getStringExtra(HIT_LIST_URL_EXTRA);
        StringRequest request = new StringRequest(Request.Method.GET, hitListUrl, hitListParsingListener, hitListParsingListener);
        request.setTag(PARSE_HIT_LIST_ACTION);

        globalRequestQueue.add(request);
    }

    private void cancelParseRequest()
    {
        globalRequestQueue.cancelAll(PARSE_HIT_LIST_ACTION);
    }
}
