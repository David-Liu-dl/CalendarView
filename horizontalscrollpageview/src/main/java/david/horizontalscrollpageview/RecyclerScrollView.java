package david.horizontalscrollpageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;


/**
 * Created by yuhaoliu on 9/05/2017.
 */

public class RecyclerScrollView extends ScrollView {
    PagingScrollHelper scrollHelper;
    RecyclerView recyclerView;
    HorizontalScrollAdapter adapter;
    private RecyclerView.ItemDecoration lastItemDecoration = null;
    private HorizontalPageLayoutManager horizontalPageLayoutManager = null;
    private PagingItemDecoration pagingItemDecoration = null;
    
    private int columns = 1;
    private float cellHeight = 300;

    public RecyclerScrollView(Context context, int columns) {
        super(context);
        this.columns = columns;
        init();
    }

    public RecyclerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(attrs, context);
        init();
    }

    public RecyclerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadAttributes(attrs, context);
        init();
    }

    private void loadAttributes(AttributeSet attrs, Context context){
        if (attrs != null && context != null){
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RecyclerScrollView,0,0);
            columns = typedArray.getInteger(R.styleable.RecyclerScrollView_column, columns);
            cellHeight = typedArray.getDimension(R.styleable.RecyclerScrollView_cell_height, cellHeight);
        }
    }

    public void setAdapter(HorizontalScrollAdapter adapter){
        this.adapter = adapter;
        this.recyclerView.setAdapter(this.adapter);
        this.scrollHelper.scrollToPosition(HorizontalScrollAdapter.START_POSITION);
    }

    public void setOnPageChangeListener(PagingScrollHelper.onPageChangeListener onPageChangeListener){
        scrollHelper.setOnPageChangeListener(onPageChangeListener);
    }

    private void init(){
        recyclerView = new RecyclerView(getContext());
        scrollHelper = new PagingScrollHelper(columns);
        scrollHelper.setUpRecycleView(recyclerView);

        horizontalPageLayoutManager = new HorizontalPageLayoutManager(1,columns);
        pagingItemDecoration = new PagingItemDecoration(this.getContext(), horizontalPageLayoutManager);
        addView(recyclerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        initLayout();
    }

    private void initLayout() {
        RecyclerView.LayoutManager layoutManager = horizontalPageLayoutManager;
        RecyclerView.ItemDecoration itemDecoration = pagingItemDecoration;

        if (layoutManager != null) {
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.removeItemDecoration(lastItemDecoration);
            recyclerView.addItemDecoration(itemDecoration);
            scrollHelper.updateLayoutManger();
            lastItemDecoration = itemDecoration;
        }
    }

    public void scrollToPosition(int position){
        scrollHelper.scrollToPosition(position);
    }
}
