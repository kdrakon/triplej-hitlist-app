package io.policarp.triplejhitlistapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.policarp.triplejhitlistapp.dao.HitListEntity;
import io.policarp.triplejhitlistapp.imageloading.DiskBasedImageCache;
import io.policarp.triplejhitlistapp.imageloading.WikipediaImageLookup;
import org.roboguice.shaded.goole.common.base.Optional;
import roboguice.RoboGuice;

/**
 * Created by kdrakon on 25/10/15.
 */
public class HitListCardView extends CardView
{
    @Inject
    private WikipediaImageLookup imageLookup;

    @Inject
    @Named("networkImageLoaderRequestQueue")
    private RequestQueue networkImageLoaderRequestQueue;

    @Inject
    private DiskBasedImageCache diskBasedImageCache;

    public HitListCardView(Context context)
    {
        super(context);
        RoboGuice.getInjector(getContext()).injectMembers(this);
    }

    public HitListCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        RoboGuice.getInjector(getContext()).injectMembers(this);
    }

    public HitListCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        RoboGuice.getInjector(getContext()).injectMembers(this);
    }

    public void loadCard(HitListEntity hitListEntity)
    {
        TextView artist = (TextView) this.findViewById(R.id.artist);
        artist.setText(hitListEntity.getArtist());
        artist.setMovementMethod(new ScrollingMovementMethod());

        TextView track = (TextView) this.findViewById(R.id.track);
        track.setText(hitListEntity.getTrack());

        final View artistImageLayout = this.findViewById(R.id.artistImageLayout);
        final ImageView artistImage = (ImageView) this.findViewById(R.id.artistImageView);
        Optional<String> cachedImageUrl = imageLookup.getCachedImageUrl(hitListEntity.getArtist());
        RelativeLayout cardInfoSection = (RelativeLayout) this.findViewById(R.id.card_info_section);

        if (cachedImageUrl.isPresent())
        {
            artistImageLayout.setVisibility(View.GONE);

            Cache.Entry cachedImageEntry = diskBasedImageCache.get("0:".concat(cachedImageUrl.get()));
            if (cachedImageEntry != null)
            {
                artistImage.setImageBitmap(BitmapFactory.decodeByteArray(cachedImageEntry.data, 0, cachedImageEntry.data.length));
                artistImageLayout.setVisibility(View.VISIBLE);

            } else {

                ImageRequest imageRequest = new ImageRequest(cachedImageUrl.get(), new Response.Listener<Bitmap>()
                {
                    @Override
                    public void onResponse(Bitmap response)
                    {
                        artistImage.setImageBitmap(response);
                        artistImageLayout.setVisibility(View.VISIBLE);
                    }
                }, 800, 600, ImageView.ScaleType.FIT_CENTER, null, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                    }
                });

                networkImageLoaderRequestQueue.add(imageRequest);
            }

            cardInfoSection.setBackgroundColor(this.getResources().getColor(R.color.dark_card_info_section_color));
            ((TextView) this.findViewById(R.id.track)).setTextColor(Color.WHITE);

        } else
        {
            cardInfoSection.setBackgroundColor(this.getResources().getColor(R.color.light_card_info_section_color));
            ((TextView) this.findViewById(R.id.track)).setTextColor(Color.BLACK);
            artistImageLayout.setVisibility(View.GONE);
        }
    }
}
