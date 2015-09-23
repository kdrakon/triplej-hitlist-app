package io.policarp.triplejhitlistapp;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.inject.Inject;
import org.roboguice.shaded.goole.common.cache.Cache;
import org.roboguice.shaded.goole.common.cache.CacheBuilder;

/**
 * Created by kdrakon on 20/09/15.
 */
public class HitListRecyclerListAdapter extends RecyclerView.Adapter<HitListRecyclerListAdapter.HitListCardViewHolder>
{
    @Inject
    private HitListDaoManager hitListDaoManager;

    private Cache<String, List<HitListEntity>> cachedHitList = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build();

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
            return cachedHitList.get("", new Callable<List<HitListEntity>>()
            {
                @Override
                public List<HitListEntity> call() throws Exception
                {
                    return hitListDaoManager.getActiveHitList();
                }
            });

        } catch (ExecutionException e)
        {
            return hitListDaoManager.getActiveHitList();
        }
    }
}
