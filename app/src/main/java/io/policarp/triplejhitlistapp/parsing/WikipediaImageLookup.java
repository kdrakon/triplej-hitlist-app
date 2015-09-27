package io.policarp.triplejhitlistapp.parsing;

import java.io.File;
import java.util.List;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Created by kdrakon on 27/09/15.
 */
public class WikipediaImageLookup
{
    private static final String API_QUERY = "https://en.wikipedia.org/w/api.php?action=query&prop=images&format=json&titles={query}";

    @Inject
    @Named("applicationContext")
    private Context context;

    /**
     * Cache an image from a query lookup for use later
     */
    public void doArtistImageLookup(List<String> artists)
    {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        final WikipediaRequestListener requestListener = new WikipediaRequestListener(context.getCacheDir());

        for (String artist : artists)
        {
            final String apiRequestQuery = API_QUERY.replace("{query}", artist);

            final StringRequest imageRequest =
                    new StringRequest(Request.Method.GET, apiRequestQuery, requestListener, requestListener);

            requestQueue.add(imageRequest);
        }
    }

    private static class WikipediaRequestListener implements Response.Listener<String>, Response.ErrorListener
    {
        private File localCacheFolder;

        private WikipediaRequestListener(File localCacheFolder)
        {
            this.localCacheFolder = localCacheFolder;
        }

        @Override
        public void onErrorResponse(VolleyError error)
        {

        }

        @Override
        public void onResponse(String response)
        {

        }
    }
}
