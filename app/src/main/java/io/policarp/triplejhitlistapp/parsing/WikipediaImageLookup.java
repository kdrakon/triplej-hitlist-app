package io.policarp.triplejhitlistapp.parsing;

import java.io.*;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.roboguice.shaded.goole.common.base.Optional;
import org.roboguice.shaded.goole.common.collect.ImmutableList;
import roboguice.util.Strings;

/**
 * Created by kdrakon on 27/09/15.
 */
public class WikipediaImageLookup
{
    private static final String CACHE_IMAGE_SUFFIX = ".img_cached";
    private static final String API_QUERY = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=original&titles={query}";

    @Inject
    @Named("applicationContext")
    private Context context;

    /**
     * Cache an image from a query lookup for use later
     */
    public void doArtistImageLookup(List<String> artists)
    {
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        for (String artist : artists)
        {
            final String apiRequestQuery = API_QUERY.replace("{query}", StringEscapeUtils.escapeHtml4(artist).replace(" ", "%20"));

            final WikipediaRequestListener requestListener = new WikipediaRequestListener(artist, context.getCacheDir(), requestQueue);

            final JsonObjectRequest imageRequest =
                    new JsonObjectRequest(Request.Method.GET, apiRequestQuery, requestListener, requestListener);

            requestQueue.add(imageRequest);
        }
    }

    public Optional<Bitmap> getCachedImageLookup(String artist)
    {
        File cachedImageFile = new File(context.getCacheDir(), getFilename(artist));
        if (cachedImageFile.exists() && cachedImageFile.canRead())
        {
            try
            {
                FileInputStream bitmapInputStream = new FileInputStream(cachedImageFile);
                Bitmap bitmap = BitmapFactory.decodeStream(bitmapInputStream);
                return Optional.fromNullable(bitmap);

            } catch (FileNotFoundException e)
            {
                return Optional.absent();
            }

        } else {
            return Optional.absent();
        }
    }

    static String getFilename(final String artist)
    {
        return Strings.md5(artist).concat(CACHE_IMAGE_SUFFIX);
    }

    private static class WikipediaRequestListener implements Response.Listener<JSONObject>, Response.ErrorListener
    {
        private final String artist;
        private final File localCacheFolder;
        private final RequestQueue requestQueue;

        private WikipediaRequestListener(String artist, File localCacheFolder, RequestQueue requestQueue)
        {
            this.artist = artist;
            this.localCacheFolder = localCacheFolder;
            this.requestQueue = requestQueue;
        }

        @Override
        public void onErrorResponse(VolleyError error)
        {
        }

        @Override
        public void onResponse(final JSONObject response)
        {
            try
            {
                final JSONObject pagesResponse = response.getJSONObject("query").getJSONObject("pages");
                final List<String> pageKeys = ImmutableList.copyOf(pagesResponse.keys());

                for (String pageKey :  pageKeys)
                {
                    if (!pageKey.equals("-1"))
                    {
                        String imageUrl = pagesResponse
                                                .getJSONObject(pageKey)
                                                .getJSONObject("thumbnail")
                                                .getString("original");

                        new DownloadAndCacheImageTask(artist, imageUrl, localCacheFolder).execute();
                    }
                }

            } catch (JSONException e)
            {
                // TODO handle this?
            }
        }
    }

    private static class DownloadAndCacheImageTask extends AsyncTask<String, Void, Void>
    {
        private final String artist;
        private final String imageUrl;
        private final File localCacheFolder;

        public DownloadAndCacheImageTask(String artist, String imageUrl, File localCacheFolder)
        {
            this.artist = artist;
            this.imageUrl = imageUrl;
            this.localCacheFolder = localCacheFolder;
        }

        @Override
        protected Void doInBackground(String... params)
        {
            try
            {
                final File newImageFile = new File(localCacheFolder, getFilename(artist));
                if (newImageFile.exists() && newImageFile.canRead()) return null;

                URL url = new URL(imageUrl);
                InputStream is = url.openConnection().getInputStream();
                Bitmap bitMap = BitmapFactory.decodeStream(is);
                FileOutputStream fileOutputStream = new FileOutputStream(newImageFile);
                bitMap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();

            } catch (IOException e)
            {
                // TODO handle this?
            }

            return null;
        }
    }
}
