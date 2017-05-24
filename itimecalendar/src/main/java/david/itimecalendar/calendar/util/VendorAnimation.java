package david.itimecalendar.calendar.util;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;

/**
 * Created by yuhaoliu on 26/12/2016.
 */
public class VendorAnimation {
    private static VendorAnimation ourInstance = new VendorAnimation();

    public static VendorAnimation getInstance() {
        return ourInstance;
    }

    private VendorAnimation() {
    }

    public AlphaAnimation getFadeInAnim(){
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(1000);

        return anim;
    }

    public AlphaAnimation getFadeOutAnim(){
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);

        return anim;
    }

    public ValueAnimator getAlphaAnim(int from, int to, final View view){
        int alphaFrom = from;
        int alphaTo = to;
        ValueAnimator alphaAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), alphaFrom, alphaTo);
        alphaAnimation.setDuration(2000); // milliseconds
        alphaAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                view.getBackground().setAlpha((int) animator.getAnimatedValue());
            }

        });

        return alphaAnimation;
    }
}
