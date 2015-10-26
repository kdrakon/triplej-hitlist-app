package io.policarp.triplejhitlistapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import io.policarp.triplejhitlistapp.dao.HitListEntity;
import org.roboguice.shaded.goole.common.cache.LoadingCache;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListRecyclerListAdapter extends RecyclerView.Adapter<HitListRecyclerListAdapter.HitListCardViewHolder>
{
    private static final int CARD_COLLECTION_SIZE = 5;

    private final LoadingCache<String, List<HitListEntity>> cachedHitList;

    public HitListRecyclerListAdapter(LoadingCache<String, List<HitListEntity>> cachedHitList)
    {
        this.cachedHitList = cachedHitList;
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

    @Override
    public void onBindViewHolder(HitListCardViewHolder viewHolder, int position)
    {
        final List<HitListEntity> hitListEntities = new ArrayList<>(getCachedHitList());

        int offset = (position * CARD_COLLECTION_SIZE);

        for (int i = offset; i < (offset + CARD_COLLECTION_SIZE) && i < hitListEntities.size(); i++)
        {
            final HitListEntity hitListEntity = hitListEntities.get(i);
            final HitListCardView hitListCardView = (HitListCardView) viewHolder.cardCollectionView.getChildAt(i % CARD_COLLECTION_SIZE);
            hitListCardView.loadCard(hitListEntity);
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
