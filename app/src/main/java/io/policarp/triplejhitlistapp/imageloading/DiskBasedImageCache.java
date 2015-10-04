package io.policarp.triplejhitlistapp.imageloading;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.android.volley.toolbox.DiskBasedCache;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.roboguice.shaded.goole.common.base.Optional;
import roboguice.inject.ContextSingleton;

/**
 * Created by kdrakon on 04/10/15.
 */
@ContextSingleton
class DiskBasedImageCache extends DiskBasedCache
{
    private static final int DEFAULT_CACHE_BYTE_SIZE = 128_000_000;
    private static final float MAX_IMAGE_WIDTH = 600f;

    @Inject
    public DiskBasedImageCache(@Named("applicationContext") Context context)
    {
        super(new File(context.getCacheDir(), "artist_image_cache"), DEFAULT_CACHE_BYTE_SIZE);
    }

    @Override
    public synchronized void put(String key, Entry entry)
    {
        super.put(key, postProcessImageEntry(entry).or(entry));
    }

    private Optional<Entry> postProcessImageEntry(final Entry originalEntry)
    {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeByteArray(originalEntry.data, 0, originalEntry.data.length, options);

        final Bitmap originalBitmap = BitmapFactory.decodeByteArray(originalEntry.data, 0, originalEntry.data.length);
        if (originalBitmap == null) return Optional.absent();

        // free some VM memory
        originalEntry.data = null;

        final BitmapFactory.Options scaledOptions = scaleImageDown(options);
        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledOptions.outWidth, scaledOptions.outHeight, false);

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 75, os);

        originalEntry.data = os.toByteArray();

        return Optional.of(originalEntry);
    }

    private BitmapFactory.Options scaleImageDown(final BitmapFactory.Options originalOptions)
    {
        final float width = originalOptions.outWidth;
        final float height = originalOptions.outHeight;

        if (width <= MAX_IMAGE_WIDTH)
        {
            return originalOptions;
        }

        final float ratio = height/width;
        originalOptions.outWidth = (int) MAX_IMAGE_WIDTH;
        originalOptions.outHeight = (int) (MAX_IMAGE_WIDTH * ratio);

        return originalOptions;
    }
}
