package io.policarp.triplejhitlistapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ViewFlipper;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by kdrakon on 23/09/15.
 */
@ContentView(R.layout.activity_main)
public class HitListGestureListener extends GestureDetector.SimpleOnGestureListener
{
    @InjectView(R.id.hitListViewFlipper)
    private ViewFlipper hitListViewFlipper;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        if (Math.abs(velocityX) >= 1000)
        {
            final int direction = (velocityX > 0) ? 1 : -1;
            final int translate = direction * 100;
            final int translateBack = translate * -1;
            hitListViewFlipper.animate().alpha(0f).setDuration(100).translationXBy(translate).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    hitListViewFlipper.showNext();
                    hitListViewFlipper.animate().alpha(1f).setDuration(100).translationXBy(translateBack).setListener(null);
                }
            });
        }

        return super.onFling(e1, e2, velocityX, velocityY);
    }
}
