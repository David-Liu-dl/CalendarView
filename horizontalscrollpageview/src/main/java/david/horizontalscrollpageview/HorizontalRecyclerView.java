package david.horizontalscrollpageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ScrollView;


/**
 * Created by yuhaoliu on 9/05/2017.
 */

public class HorizontalRecyclerView extends RecyclerView {
    PagingScrollHelper scrollHelper;
    private RecyclerView.ItemDecoration lastItemDecoration = null;
    private HorizontalPageLayoutManager horizontalPageLayoutManager = null;
    private PagingItemDecoration pagingItemDecoration = null;

    private int columns = 1;
    private float cellHeight = 300;

    public HorizontalRecyclerView(Context context, int columns) {
        super(context);
        this.columns = columns;
        init();
    }

    public HorizontalRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadAttributes(attrs, context);
        init();
    }

    public HorizontalRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    public void setOnPageChangeListener(PagingScrollHelper.onPageChangeListener onPageChangeListener){
        scrollHelper.setOnPageChangeListener(onPageChangeListener);
    }

    private void init(){
//        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
//        this.setRecycledViewPool(viewPool);
//        viewPool.setMaxRecycledViews(0, 5);

        scrollHelper = new PagingScrollHelper(columns);
        scrollHelper.setUpRecycleView(this);
        horizontalPageLayoutManager = new HorizontalPageLayoutManager(1,columns);
        pagingItemDecoration = new PagingItemDecoration(this.getContext(), horizontalPageLayoutManager);
        initLayout();
    }

    private void initLayout() {
        RecyclerView.LayoutManager layoutManager = horizontalPageLayoutManager;
        RecyclerView.ItemDecoration itemDecoration = pagingItemDecoration;

        if (layoutManager != null) {
//            this.setLayoutManager(new HorizontalPageLayoutLinearManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
            this.setLayoutManager(layoutManager);
            this.removeItemDecoration(lastItemDecoration);
            this.addItemDecoration(itemDecoration);
            scrollHelper.updateLayoutManger();
            lastItemDecoration = itemDecoration;
        }
    }

    public void scrollToPosition(int position){
        scrollHelper.scrollToPosition(position);
    }

//    @Override
//    public boolean fling(int velocityX, int velocityY) {
//        return super.fling(0, 0);
//    }
}
