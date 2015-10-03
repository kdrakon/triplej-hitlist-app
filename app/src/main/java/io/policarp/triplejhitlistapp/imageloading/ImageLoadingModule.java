package io.policarp.triplejhitlistapp.imageloading;

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.roboguice.shaded.goole.common.cache.Cache;
import org.roboguice.shaded.goole.common.cache.CacheBuilder;
import roboguice.inject.ContextSingleton;

/**
 * Created by kdrakon on 01/10/15.
 */
public class ImageLoadingModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        binder.bind(Key.get(ImageLoader.class, Names.named("artistImageLoader"))).toProvider(ArtistImageLoader.class);
    }

    @Provides
    @Named("networkImageLoaderRequestQueue")
    @ContextSingleton
    RequestQueue getNetworkImageLoaderRequestQueue(@Named("applicationContext") Context context)
    {
        final RequestQueue networkImageLoaderRequestQueue = Volley.newRequestQueue(context, 256_000_000);
        return networkImageLoaderRequestQueue;
    }

    @Provides
    @Named("imageLoadingMemBasedCache")
    @ContextSingleton
    Cache<String, Bitmap> getImageLoadingMemBasedCache()
    {
        final Cache<String, Bitmap> memCache =
                CacheBuilder.newBuilder().maximumSize(200).expireAfterWrite(1, TimeUnit.MINUTES).build();

        return memCache;
    }
}
