package org.unimelb.itime.vendor.agendaview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by yuhaoliu on 1/09/16.
 */
public class AgendaBodyRecyclerView extends RecyclerView{
    private double scale;

    public AgendaBodyRecyclerView(Context context) {
        super(context);
    }

    public AgendaBodyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AgendaBodyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFlingScale(double scale){
        this.scale = scale;
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX *= scale;
        velocityY *= scale;

        return super.fling(velocityX, velocityY);
    }
}
