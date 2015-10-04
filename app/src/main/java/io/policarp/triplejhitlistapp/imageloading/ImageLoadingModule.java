package io.policarp.triplejhitlistapp.imageloading;

import java.io.File;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
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
    RequestQueue getNetworkImageLoaderRequestQueue(DiskBasedImageCache diskBasedImageCache)
    {
        diskBasedImageCache.initialize();

        final Network network = new BasicNetwork(new HurlStack());
        final RequestQueue networkImageLoaderRequestQueue = new RequestQueue(diskBasedImageCache, network);
        networkImageLoaderRequestQueue.start();

        return networkImageLoaderRequestQueue;
    }

    @Provides
    @Named("imageLoadingMemBasedCache")
    @ContextSingleton
    Cache<String, Bitmap> getImageLoadingMemBasedCache()
    {
        final Cache<String, Bitmap> memCache =
                CacheBuilder.newBuilder().maximumSize(200).expireAfterWrite(1, TimeUnit.HOURS).build();

        return memCache;
    }
}
