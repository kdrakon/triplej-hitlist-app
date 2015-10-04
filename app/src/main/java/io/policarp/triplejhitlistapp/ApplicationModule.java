package io.policarp.triplejhitlistapp;

import android.content.Context;
import android.view.GestureDetector;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import io.policarp.triplejhitlistapp.imageloading.ImageLoadingModule;
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
        install(new ImageLoadingModule());
    }

    @Provides
    @Named("applicationContext")
    public Context getApplicationContext(Context context)
    {
        return context;
    }

    @Provides
    @Named("tripleJHitListApi")
    @ContextSingleton
    public String getTripleJHitListApi()
    {
        return "http://triplejgizmo.abc.net.au:8080/jjj-hitlist/current/app/webroot/latest/play.txt";
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
