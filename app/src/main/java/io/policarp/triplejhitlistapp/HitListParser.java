package io.policarp.triplejhitlistapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.roboguice.shaded.goole.common.base.Optional;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListParser
{
    public Optional<List<HitListEntity>> parseJsonResponse(final String jsonResponse)
    {
        final Optional<JSONArray> hitListArray = parseHitListArray(jsonResponse);

        if (!hitListArray.isPresent()) return Optional.absent();

        return Optional.of(parseToHitListEntities(hitListArray.get()));
    }

    private Optional<JSONArray> parseHitListArray(final String jsonReponse)
    {
        try
        {
            return Optional.fromNullable(new JSONArray(jsonReponse));

        } catch (JSONException e)
        {
            return Optional.absent();
        }
    }

    private List<HitListEntity> parseToHitListEntities(final JSONArray hitListArray)
    {
        final List<HitListEntity> hitListEntities = new ArrayList<>(hitListArray.length());

        for (int i=0; i < hitListArray.length(); i++)
        {
            try
            {
                final JSONObject jsonObject = hitListArray.getJSONObject(i).getJSONObject("HitlistEntry");
                final String artist = jsonObject.getString("artist").trim();
                final String track = jsonObject.getString("track").trim();
                final String label_name = jsonObject.getString("label_name").trim();
                hitListEntities.add(new HitListEntity(artist, track, label_name));

            } catch (JSONException e)
            {
                // TODO report this somewhere, for now skip JSONObject
            }
        }

        return hitListEntities;
    }
}
