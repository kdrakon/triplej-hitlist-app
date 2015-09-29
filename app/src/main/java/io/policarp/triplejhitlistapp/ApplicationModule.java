package io.policarp.triplejhitlistapp;

import android.content.Context;
import android.view.GestureDetector;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import io.policarp.triplejhitlistapp.imageloading.ArtistImageLoader;
import roboguice.inject.ContextSingleton;

/**
 * Created by kdrakon on 23/09/15.
 */
public class ApplicationModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        install(new HitListRecyclerListAdapterModule());

        binder.bind(Key.get(ImageLoader.class, Names.named("artistImageLoader"))).toProvider(ArtistImageLoader.class);
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
    @Named("globalRequestQueue")
    @ContextSingleton
    public RequestQueue getGlobalRequestQueue(@Named("applicationContext") Context context)
    {
        return Volley.newRequestQueue(context);
    }
}
