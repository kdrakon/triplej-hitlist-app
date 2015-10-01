package io.policarp.triplejhitlistapp.imageloading;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    @Override
    @ContextSingleton
    public ImageLoader get()
    {
        final Cache<String, Bitmap> memCache = CacheBuilder.newBuilder().maximumSize(200).expireAfterWrite(1, TimeUnit.HOURS).build();
        final DiskBasedCache diskBasedCache = new DiskBasedCache(context.getCacheDir());
        final RequestQueue networkImageLoaderRequestQueue = Volley.newRequestQueue(context);

        final ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache()
        {
            @Override
            public Bitmap getBitmap(String url)
            {
                // check mem first
                Optional<Bitmap> bitmap = Optional.fromNullable(memCache.getIfPresent(url));
                if (bitmap.isPresent())
                {
                    return bitmap.get();
                }

                // check disk next
                Optional<com.android.volley.Cache.Entry> entry = Optional.fromNullable(diskBasedCache.get(url));
                if (entry.isPresent())
                {
                    return BitmapFactory.decodeByteArray(entry.get().data, 0, entry.get().data.length);
                }

                return null;
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap)
            {
                memCache.put(url, bitmap);
                diskBasedCache.put(url, new ArtistImageDiskCacheEntry(bitmap));
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
