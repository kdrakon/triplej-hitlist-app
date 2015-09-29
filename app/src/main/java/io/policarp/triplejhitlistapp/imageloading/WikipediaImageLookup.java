package io.policarp.triplejhitlistapp.imageloading;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.roboguice.shaded.goole.common.base.Optional;
import org.roboguice.shaded.goole.common.collect.ImmutableList;
import roboguice.inject.ContextSingleton;
import roboguice.util.Strings;

/**
 * Created by kdrakon on 27/09/15.
 */
@ContextSingleton
public class WikipediaImageLookup
{
    private static final String CACHE_IMAGE_SUFFIX = ".img_url_cached";
    private static final String API_QUERY = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=original&titles={query}";

    @Inject
    @Named("applicationContext")
    private Context context;

    @Inject
    @Named("globalRequestQueue")
    private RequestQueue globalRequestQueue;

    /**
     * Cache an image from a query lookup for use later
     */
    public void doArtistImageLookup(List<String> artists)
    {
        for (String artist : artists)
        {
            if (!getFileHandleForCachedArtistImageUrl(context, artist).exists())
            {
                final String apiRequestQuery = API_QUERY.replace("{query}", StringEscapeUtils.escapeHtml4(artist).replace(" ", "%20"));

                final WikipediaRequestListener requestListener = new WikipediaRequestListener(artist, context);

                final JsonObjectRequest imageRequest = new JsonObjectRequest(Request.Method.GET, apiRequestQuery, requestListener, requestListener);

                globalRequestQueue.add(imageRequest);
            }
        }
    }

    public Optional<String> getCachedImageUrl(String artist)
    {
        File cachedImageUrlFile = getFileHandleForCachedArtistImageUrl(context, artist);
        if (cachedImageUrlFile.exists() && cachedImageUrlFile.canRead())
        {
            try(FileInputStream fileInputStream = new FileInputStream(cachedImageUrlFile))
            {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                final String firstLine = bufferedReader.readLine();
                return Strings.isEmpty(firstLine)? Optional.<String>absent() : Optional.fromNullable(firstLine);

            } catch (IOException e)
            {
                return Optional.absent();
            }

        } else {
            return Optional.absent();
        }
    }

    private static String getFilename(final String artist)
    {
        return Strings.md5(artist).concat(CACHE_IMAGE_SUFFIX);
    }

    private static File getFileHandleForCachedArtistImageUrl(final Context context, final String artist)
    {
        return new File(context.getCacheDir(), getFilename(artist));
    }

    private static class WikipediaRequestListener implements Response.Listener<JSONObject>, Response.ErrorListener
    {
        private final String artist;
        private final Context context;

        private WikipediaRequestListener(String artist, Context context)
        {
            this.artist = artist;
            this.context = context;
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

                for (String pageKey : pageKeys)
                {
                    if (!pageKey.equals("-1"))
                    {
                        final String imageUrl =
                                pagesResponse.getJSONObject(pageKey).getJSONObject("thumbnail").getString("original");

                        // save cached file with imageUrl if one doesn't already exist
                        final File newImageFile = getFileHandleForCachedArtistImageUrl(context, artist);
                        if (newImageFile.createNewFile())
                        {
                            try(FileOutputStream fileOutputStream = new FileOutputStream(newImageFile))
                            {
                                fileOutputStream.write(imageUrl.getBytes(Charset.defaultCharset()));
                                fileOutputStream.close();
                            }
                        }
                    }
                }

            } catch (JSONException | IOException e)
            {
                // TODO handle this?
            }
        }
    }
}
