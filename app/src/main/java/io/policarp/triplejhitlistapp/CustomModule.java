package io.policarp.triplejhitlistapp;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.view.GestureDetector;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import io.policarp.triplejhitlistapp.dao.HitListDaoManager;
import io.policarp.triplejhitlistapp.dao.HitListEntity;
import org.roboguice.shaded.goole.common.cache.CacheBuilder;
import org.roboguice.shaded.goole.common.cache.CacheLoader;
import org.roboguice.shaded.goole.common.cache.LoadingCache;

/**
 * Created by kdrakon on 23/09/15.
 */
public class CustomModule extends AbstractModule
{
    @Override
    protected void configure()
    {

    }

    @Provides
    @Named("hitListGestureListener")
    public GestureDetector getHitListGestureDector(HitListGestureListener hitListGestureListener)
    {
        return new GestureDetector(hitListGestureListener);
    }

    @Provides
    @Named("recyclerListAdapterForHitList")
    public HitListRecyclerListAdapter getHitListRecyclerListAdapter(
            @Named("hitListCache") LoadingCache<String, List<HitListEntity>> hitListCache)
    {
        return new HitListRecyclerListAdapter(hitListCache);
    }

    @Provides
    @Named("hitListCache")
    public LoadingCache<String, List<HitListEntity>> getHitListCache(final HitListDaoManager hitListDaoManager)
    {
        CacheLoader<String, List<HitListEntity>> cacheLoader = new CacheLoader<String, List<HitListEntity>>()
        {
            @Override
            public List<HitListEntity> load(String s) throws Exception
            {
                return hitListDaoManager.getActiveHitList();
            }
        };

        LoadingCache<String, List<HitListEntity>> cachedHitList =
                CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build(cacheLoader);

        return cachedHitList;
    }

    @Provides
    @Named("recyclerListAdapterForArchivedHitList")
    public HitListRecyclerListAdapter getArchivedHitListRecyclerListAdapter(
            @Named("archivedListCache") LoadingCache<String, List<HitListEntity>> archivedHitListCache)
    {
        return new HitListRecyclerListAdapter(archivedHitListCache);
    }

    @Provides
    @Named("archivedListCache")
    public LoadingCache<String, List<HitListEntity>> getArchivedListCache(final HitListDaoManager hitListDaoManager)
    {
        CacheLoader<String, List<HitListEntity>> cacheLoader = new CacheLoader<String, List<HitListEntity>>()
        {
            @Override
            public List<HitListEntity> load(String s) throws Exception
            {
                return hitListDaoManager.getArchivedHitList();
            }
        };

        LoadingCache<String, List<HitListEntity>> cachedArchivedList =
                CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).build(cacheLoader);

        return cachedArchivedList;
    }
}
