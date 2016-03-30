package l.android.lee.my.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.List;

import my.lee.android.l.BaseAdvRecyclerViewAdapter;

/**
 * User: Smile(lijianhy1990@gmail.com)
 * Date: 2016-03-15
 * Time: 10:40
 */
public class TestAdvRecyclerViewAdapter extends BaseAdvRecyclerViewAdapter<Integer> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    public TestAdvRecyclerViewAdapter(Context context, List<Integer> datas) {
        super(context, datas);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder holder, int position) {
        bindDataViewHolder((MyViewHolder) holder, datas, position);
    }

    private void bindDataViewHolder(MyViewHolder holder, List<Integer> datas, int position) {
        holder.mTextView.setText(datas.get(position) + "");
    }

    @Override
    public long getHeaderId(int position) {
        if (position==datas.size()){
            return -1;
        }
        return datas.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.setText(getHeaderId(position) + "");
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        View mViewDiv;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.item_tv);
            mViewDiv = itemView.findViewById(R.id.item_div);
        }
    }
}
