package com.example.myapplication2.app;

import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import org.roboguice.shaded.goole.common.base.Optional;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListParsingListener implements Response.Listener<String>, Response.ErrorListener
{
    @Inject
    private HitListParser hitListParser;

    @Inject
    private HitListDaoManager hitListDaoManager;

    @Override public void onResponse(String response)
    {
        Optional<List<HitListEntity>> newHitListEntities = hitListParser.parseJsonResponse(response);
        if (newHitListEntities.isPresent())
        {
            hitListDaoManager.updateHitListEntities(newHitListEntities.get());
        } else {
            // TODO inform user of failure
        }
    }

    @Override public void onErrorResponse(VolleyError error)
    {
        // TODO inform user of failure
    }
}
