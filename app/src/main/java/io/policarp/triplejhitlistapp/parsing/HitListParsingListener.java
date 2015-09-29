package io.policarp.triplejhitlistapp.parsing;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.inject.Inject;
import io.policarp.triplejhitlistapp.dao.HitListDaoManager;
import io.policarp.triplejhitlistapp.dao.HitListEntity;
import io.policarp.triplejhitlistapp.imageloading.WikipediaImageLookup;
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

    @Inject
    private WikipediaImageLookup imageLookup;

    @Override public void onResponse(String response)
    {
        final Optional<List<HitListEntity>> newHitListEntities = hitListParser.parseJsonResponse(response);
        if (newHitListEntities.isPresent())
        {
            hitListDaoManager.updateHitListEntities(newHitListEntities.get());
            doArtistImageLookups(newHitListEntities.get());

        } else {
            // TODO inform user of failure
        }
    }

    private void doArtistImageLookups(final List<HitListEntity> hitListEntities)
    {
        final List<String> artists = new ArrayList<>();
        for (HitListEntity hitListEntity : hitListEntities) artists.add(hitListEntity.getArtist());
        imageLookup.doArtistImageLookup(artists);
    }

    @Override public void onErrorResponse(VolleyError error)
    {
        // TODO inform user of failure
    }
}
