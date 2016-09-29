package org.unimelb.itime.vendor.dayview;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by yuhaoliu on 25/09/16.
 */
public class ResizeAnimation extends Animation{
    // Used to define the type of animation upon construction
    public enum Type { WIDTH, HEIGHT }

    // The view to be animated
    private View view;

    // The dimensions and animation values (this is stored so other changes to layout don't interfere)
    private final int fromDimension; // Dimension to animate from
    private int toDimension; // Dimension to animate to
    private Type type; // See enum above, the type of animation

    // Constructor
    public ResizeAnimation(View view, int toDimension, Type type, long duration) {
        // Setup references
        // the view to animate
        this.view = view;
        // Get the current starting point of the animation (the current width or height of the provided view)
        this.fromDimension = type == Type.WIDTH ? view.getLayoutParams().width : view.getLayoutParams().height;
        // Dimension to animate to
        this.toDimension = toDimension;
        // See enum above, the type of animation
        this.type = type;

        // Set the duration of the animation
        this.setDuration(duration);
    }

    @Override
    public void applyTransformation(float interpolatedTime, Transformation t) {
        // Used to apply the animation to the view
        final int curPos = fromDimension + (int) ((toDimension - fromDimension) * interpolatedTime);

        // Animate given the height or width
        switch (type) {
            case WIDTH:
                view.getLayoutParams().width = curPos;
                break;
            case HEIGHT:
                view.getLayoutParams().height = curPos;
                break;
        }

        // Ensure the view is measured appropriately
        view.requestLayout();
    }
}
