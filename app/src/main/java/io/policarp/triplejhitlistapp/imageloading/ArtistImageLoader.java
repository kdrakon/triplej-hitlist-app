package io.policarp.triplejhitlistapp.imageloading;

import android.content.Context;
import android.graphics.Bitmap;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.roboguice.shaded.goole.common.base.Optional;
import org.roboguice.shaded.goole.common.cache.Cache;
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

                // fall-back to disk-based cache or live call
                return null;
            }

            @Override
            public void putBitmap(final String url, final Bitmap bitmap)
            {
                imageLoadingMemBasedCache.put(url, bitmap);
            }
        };

        return new ImageLoader(networkImageLoaderRequestQueue, imageCache);
    }
}
