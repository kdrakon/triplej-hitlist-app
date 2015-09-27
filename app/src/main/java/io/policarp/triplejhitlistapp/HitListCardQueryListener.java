package io.policarp.triplejhitlistapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.SearchManager;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;

/**
 * Created by kdrakon on 27/09/15.
 */
public class HitListCardQueryListener implements View.OnLongClickListener
{
    final private static Pattern curlyBracketPattern = Pattern.compile("\\{.+\\}");

    final private HitListEntity hitListEntity;

    public HitListCardQueryListener(HitListEntity hitListEntity)
    {
        this.hitListEntity = hitListEntity;
    }

    @Override
    public boolean onLongClick(View v)
    {
        final String artistQuery = curlyBracketPattern.matcher(hitListEntity.getArtist()).replaceAll("");
        final String trackQuery = curlyBracketPattern.matcher(hitListEntity.getTrack()).replaceAll("");
        final String genericQuery = artistQuery.concat(" ").concat(trackQuery);

        final Intent queryIntent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        queryIntent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/audio");
        queryIntent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, trackQuery);
        queryIntent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artistQuery);
        queryIntent.putExtra(SearchManager.QUERY, genericQuery);

        if (queryIntent.resolveActivity(v.getContext().getPackageManager()) != null)
        {
            v.getContext().startActivity(queryIntent);
        }

        return false;
    }
}
