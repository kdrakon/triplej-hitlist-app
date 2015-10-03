package io.policarp.triplejhitlistapp;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import io.policarp.triplejhitlistapp.dao.HitListEntity;
import io.policarp.triplejhitlistapp.imageloading.WikipediaImageLookup;
import org.roboguice.shaded.goole.common.base.Optional;
import org.roboguice.shaded.goole.common.cache.LoadingCache;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListRecyclerListAdapter extends RecyclerView.Adapter<HitListRecyclerListAdapter.HitListCardViewHolder>
{
    private final WikipediaImageLookup imageLookup;
    private final LoadingCache<String, List<HitListEntity>> cachedHitList;
    private final ImageLoader networkImageLoader;

    public HitListRecyclerListAdapter(LoadingCache<String, List<HitListEntity>> cachedHitList,
            WikipediaImageLookup imageLookup, ImageLoader networkImageLoader)
    {
        this.imageLookup = imageLookup;
        this.cachedHitList = cachedHitList;
        this.networkImageLoader = networkImageLoader;
    }

    public static class HitListCardViewHolder extends RecyclerView.ViewHolder
    {
        public CardView cardView;
        public HitListCardViewHolder(CardView cardView)
        {
            super(cardView);
            this.cardView = cardView;
        }
    }

    @Override
    public HitListCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hitlist_cardview, viewGroup, false);

        HitListCardViewHolder viewHolder = new HitListCardViewHolder(cardView);
        return viewHolder;
    }

    /**
     * All CardView rendering happens here
     */
    @Override
    public void onBindViewHolder(HitListCardViewHolder viewHolder, int position)
    {
        final HitListEntity hitListEntity = getCachedHitList().get(position);

        if (hitListEntity.isNewHitListEntity()) viewHolder.cardView.setCardElevation(4f);

        TextView artist = (TextView) viewHolder.cardView.findViewById(R.id.artist);
        artist.setText(hitListEntity.getArtist());
        artist.setMovementMethod(new ScrollingMovementMethod());

        TextView track = (TextView) viewHolder.cardView.findViewById(R.id.track);
        track.setText(hitListEntity.getTrack());

        loadCardImage(viewHolder, hitListEntity);

        // Attach query listener
        viewHolder.cardView.setOnLongClickListener(new HitListCardQueryListener(hitListEntity));
    }

    private void loadCardImage(HitListCardViewHolder viewHolder, HitListEntity hitListEntity)
    {
        NetworkImageView artistImage = (NetworkImageView) viewHolder.cardView.findViewById(R.id.artistImageView);
        Optional<String> cachedImageUrl = imageLookup.getCachedImageUrl(hitListEntity.getArtist());
        RelativeLayout cardInfoSection = (RelativeLayout) viewHolder.cardView.findViewById(R.id.card_info_section);

        if (cachedImageUrl.isPresent())
        {
            artistImage.setImageUrl(cachedImageUrl.get(), networkImageLoader);
            cardInfoSection.setBackgroundColor(viewHolder.cardView.getResources().getColor(R.color.dark_card_info_section_color));
            ((TextView) viewHolder.cardView.findViewById(R.id.track)).setTextColor(Color.WHITE);
            artistImage.setVisibility(View.VISIBLE);

        } else
        {
            cardInfoSection.setBackgroundColor(viewHolder.cardView.getResources().getColor(R.color.light_card_info_section_color));
            ((TextView) viewHolder.cardView.findViewById(R.id.track)).setTextColor(Color.BLACK);
            artistImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount()
    {
        return getCachedHitList().size();
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
