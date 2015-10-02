package io.policarp.triplejhitlistapp.imageloading;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.roboguice.shaded.goole.common.base.Optional;
import org.roboguice.shaded.goole.common.cache.Cache;
import org.roboguice.shaded.goole.common.cache.CacheBuilder;
import roboguice.inject.ContextSingleton;

/**
 * Created by kdrakon on 28/09/15.
 */
public class ArtistImageLoader implements Provider<ImageLoader>
{
    @Inject
    @Named("applicationContext")
    private Context context;

    @Inject
    @Named("imageLoadingDiskBasedCache")
    private DiskBasedCache imageLoadingDiskBasedCache;

    @Inject
    @Named("imageLoadingMemBasedCache")
    private Cache<String, Bitmap> imageLoadingMemBasedCache;

    @Inject
    @Named("networkImageLoaderRequestQueue")
    private RequestQueue networkImageLoaderRequestQueue;

    @Override
    @ContextSingleton
    public ImageLoader get()
    {
        final ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache()
        {
            @Override
            public Bitmap getBitmap(String url)
            {
                // check mem first
                Optional<Bitmap> bitmap = Optional.fromNullable(imageLoadingMemBasedCache.getIfPresent(url));
                if (bitmap.isPresent())
                {
                    return bitmap.get();
                }

                // check disk next
                Optional<com.android.volley.Cache.Entry> entry = Optional.fromNullable(imageLoadingDiskBasedCache.get(url));
                if (entry.isPresent())
                {
                    return BitmapFactory.decodeByteArray(entry.get().data, 0, entry.get().data.length);
                }

                return null;
            }

            @Override
            public void putBitmap(final String url, final Bitmap bitmap)
            {
                imageLoadingMemBasedCache.put(url, bitmap);

                new AsyncTask<Void, Void, Void>()
                {
                    @Override protected Void doInBackground(Void... params)
                    {
                        imageLoadingDiskBasedCache.put(url, new ArtistImageDiskCacheEntry(bitmap));
                        return null;
                    }
                }.doInBackground();
            }
        };

        return new ImageLoader(networkImageLoaderRequestQueue, imageCache);
    }

    private static class ArtistImageDiskCacheEntry extends com.android.volley.Cache.Entry
    {
        private ArtistImageDiskCacheEntry(Bitmap artistBitmap)
        {
            setData(artistBitmap);
        }

        private void setData(final Bitmap artistBitmap)
        {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            artistBitmap.compress(Bitmap.CompressFormat.PNG, 1, os);
            this.data = os.toByteArray();

            this.ttl = System.currentTimeMillis() + (86400 * 1000);
            this.softTtl = ttl;
        }
    }
}
