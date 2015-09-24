package io.policarp.triplejhitlistapp;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import org.roboguice.shaded.goole.common.cache.LoadingCache;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListRecyclerListAdapter extends RecyclerView.Adapter<HitListRecyclerListAdapter.HitListCardViewHolder>
{
    private final LoadingCache<String, List<HitListEntity>> cachedHitList;

    public static class HitListCardViewHolder extends RecyclerView.ViewHolder
    {
        public CardView cardView;
        public HitListCardViewHolder(CardView cardView)
        {
            super(cardView);
            this.cardView = cardView;
        }
    }

    public HitListRecyclerListAdapter(LoadingCache<String, List<HitListEntity>> cachedHitList)
    {
        this.cachedHitList = cachedHitList;
    }

    @Override
    public HitListCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hitlist_cardview, viewGroup, false);

        HitListCardViewHolder viewHolder = new HitListCardViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HitListCardViewHolder viewHolder, int position)
    {
        HitListEntity hitListEntity = getCachedHitList().get(position);
        TextView infoText = (TextView) viewHolder.cardView.findViewById(R.id.info_text);
        infoText.setText(hitListEntity.getHash());
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
