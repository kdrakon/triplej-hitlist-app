package io.policarp.triplejhitlistapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import io.policarp.triplejhitlistapp.dao.HitListEntity;
import io.policarp.triplejhitlistapp.imageloading.WikipediaImageLookup;
import org.roboguice.shaded.goole.common.base.Optional;
import org.roboguice.shaded.goole.common.cache.LoadingCache;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListRecyclerListAdapter extends RecyclerView.Adapter<HitListRecyclerListAdapter.HitListCardViewHolder>
{
    private static final int CARD_COLLECTION_SIZE = 20;

    private final WikipediaImageLookup imageLookup;
    private final LoadingCache<String, List<HitListEntity>> cachedHitList;
    private final RequestQueue networkImageLoaderRequestQueue;

    public HitListRecyclerListAdapter(LoadingCache<String, List<HitListEntity>> cachedHitList, WikipediaImageLookup imageLookup,
            RequestQueue networkImageLoaderRequestQueue)
    {
        this.imageLookup = imageLookup;
        this.cachedHitList = cachedHitList;
        this.networkImageLoaderRequestQueue = networkImageLoaderRequestQueue;
    }

    public static class HitListCardViewHolder extends RecyclerView.ViewHolder
    {
        public LinearLayout cardCollectionView;
        public HitListCardViewHolder(LinearLayout cardCollectionView)
        {
            super(cardCollectionView);
            this.cardCollectionView = cardCollectionView;
        }
    }

    @Override
    public HitListCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        LinearLayout cardCollectionView = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.hitlist_card_collection, viewGroup, false);

        HitListCardViewHolder viewHolder = new HitListCardViewHolder(cardCollectionView);
        return viewHolder;
    }

    /**
     * All CardView rendering happens here
     */
    @Override
    public void onBindViewHolder(HitListCardViewHolder viewHolder, int position)
    {
        final List<HitListEntity> hitListEntities = new ArrayList<>(getCachedHitList());

        int offset = (position * CARD_COLLECTION_SIZE);

        for (int i = offset; i < (offset + CARD_COLLECTION_SIZE) && i < hitListEntities.size(); i++)
        {
            final HitListEntity hitListEntity = hitListEntities.get(i);
            final CardView cardView = (CardView) viewHolder.cardCollectionView.getChildAt(i % CARD_COLLECTION_SIZE);
            loadCard(cardView, hitListEntity);
        }
    }

    private void loadCard(final CardView cardView, HitListEntity hitListEntity)
    {
        TextView artist = (TextView) cardView.findViewById(R.id.artist);
        artist.setText(hitListEntity.getArtist());
        artist.setMovementMethod(new ScrollingMovementMethod());

        TextView track = (TextView) cardView.findViewById(R.id.track);
        track.setText(hitListEntity.getTrack());

        final ImageView artistImage = (ImageView) cardView.findViewById(R.id.artistImageView);
        Optional<String> cachedImageUrl = imageLookup.getCachedImageUrl(hitListEntity.getArtist());
        RelativeLayout cardInfoSection = (RelativeLayout) cardView.findViewById(R.id.card_info_section);

        if (cachedImageUrl.isPresent())
        {
            artistImage.setVisibility(View.GONE);

            ImageRequest imageRequest = new ImageRequest(cachedImageUrl.get(), new Response.Listener<Bitmap>()
            {
                @Override
                public void onResponse(Bitmap response)
                {
                    artistImage.setImageBitmap(response);
                    artistImage.setVisibility(View.VISIBLE);
                }
            }, 800, 600, ImageView.ScaleType.FIT_CENTER, null, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                }
            });

            networkImageLoaderRequestQueue.add(imageRequest);
            cardInfoSection.setBackgroundColor(cardView.getResources().getColor(R.color.dark_card_info_section_color));
            ((TextView) cardView.findViewById(R.id.track)).setTextColor(Color.WHITE);

        } else
        {
            cardInfoSection.setBackgroundColor(cardView.getResources().getColor(R.color.light_card_info_section_color));
            ((TextView) cardView.findViewById(R.id.track)).setTextColor(Color.BLACK);
            artistImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount()
    {
        return getCachedHitList().size() / CARD_COLLECTION_SIZE;
    }

    public List<HitListEntity> getCachedHitList()
    {
        try
        {
            return cachedHitList.get("");

        } catch (ExecutionException e)
        {
            return Collections.emptyList();
        }
    }
}
