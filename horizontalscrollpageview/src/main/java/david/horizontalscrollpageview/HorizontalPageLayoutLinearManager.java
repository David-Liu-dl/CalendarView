package david.horizontalscrollpageview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by yuhaoliu on 12/05/2017.
 */

public class HorizontalPageLayoutLinearManager extends LinearLayoutManager {

    public HorizontalPageLayoutLinearManager(Context context) {
        super(context);
    }

    public HorizontalPageLayoutLinearManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public HorizontalPageLayoutLinearManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }
}
