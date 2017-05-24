package david.horizontalscrollpageview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by yuhaoliu on 9/05/2017.
 */

public abstract class HorizontalScrollAdapter extends RecyclerView.Adapter{
    public static final int START_POSITION = 50;
    private int layout = -1;

    public HorizontalScrollAdapter(int layout) {
        this.layout = layout;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layout == -1){
            return null;
        }
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layout, parent, false);
        Log.i("test", "onCreateViewHolder: ");
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        this.onBindViewHolderOuter(holder, position);
    }

    public abstract void onBindViewHolderOuter(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return START_POSITION*2;
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        MyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
