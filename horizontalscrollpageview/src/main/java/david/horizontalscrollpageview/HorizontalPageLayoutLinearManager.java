package david.horizontalscrollpageview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

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

    int count = 0;

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        count+= 1;
        Log.i("recycle", "recycle - onLayoutChildren: " + count);
    }
}
