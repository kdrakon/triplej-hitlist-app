package io.policarp.triplejhitlistapp;

import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.view.GestureDetector;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import io.policarp.triplejhitlistapp.dao.HitListDaoManager;
import io.policarp.triplejhitlistapp.dao.HitListEntity;
import io.policarp.triplejhitlistapp.parsing.WikipediaImageLookup;
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
    @Named("applicationContext")
    public Context getApplicationContext(Context context)
    {
        return context;
    }

    @Provides
    @Named("hitListGestureListener")
    public GestureDetector getHitListGestureDector(HitListGestureListener hitListGestureListener)
    {
        return new GestureDetector(hitListGestureListener);
    }

    @Provides
    @Named("recyclerListAdapterForHitList")
    public HitListRecyclerListAdapter getHitListRecyclerListAdapter(WikipediaImageLookup imageLookup,
            @Named("hitListCache") LoadingCache<String, List<HitListEntity>> hitListCache)
    {
        return new HitListRecyclerListAdapter(imageLookup, hitListCache);
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
    public HitListRecyclerListAdapter getArchivedHitListRecyclerListAdapter(WikipediaImageLookup imageLookup,
            @Named("archivedListCache") LoadingCache<String, List<HitListEntity>> archivedHitListCache)
    {
        return new HitListRecyclerListAdapter(imageLookup, archivedHitListCache);
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
