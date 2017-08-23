package my.lee.android.l;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public abstract class BaseAdvRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static final int VIEW_TYPE_DATA = 0x01110;
    static final int VIEW_TYPE_LOAD_MORE = 0x01111;

    protected List<T> datas;
    protected Context context;
    private boolean isLoadMoreEnd;
    private boolean isEndVisible;
    private AdvancedRecyclerView.OnItemClickListener onItemClickListener;
    private int lordMoreViewLayout = R.layout.adv_view_load_more;

    public BaseAdvRecyclerViewAdapter(Context context, List<T> datas) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOAD_MORE) {
            return new LoadMoreViewHolder(LayoutInflater.from(context).inflate(lordMoreViewLayout, parent, false));
        }
        return getViewHolder(parent, viewType);
    }

    public void loadEnd() {
        isLoadMoreEnd = true;
        notifyItemChanged(getItemCount() - 1);
    }

    public void showEnd() {
        isLoadMoreEnd = false;
        notifyItemChanged(getItemCount() - 1);
    }

    public void setEndVisible(boolean visible) {
        isEndVisible = visible;
    }

    @Override
    public final void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_LOAD_MORE)
            bindLoadViewHolder((LoadMoreViewHolder) holder, position);
        else {
            if (onItemClickListener != null)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(holder.getAdapterPosition());
                    }
                });
            bindData(holder, position);
        }
    }

    public abstract RecyclerView.ViewHolder getViewHolder(ViewGroup parent, int viewType);

    public abstract void bindData(RecyclerView.ViewHolder holder, int position);

    private void bindLoadViewHolder(LoadMoreViewHolder holder, int position) {
        holder.mProgress.setVisibility(isLoadMoreEnd ? View.GONE : View.VISIBLE);
        holder.mTextView.setVisibility(!isLoadMoreEnd ? View.GONE : View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (isLoadMoreEnd && !isEndVisible) {
            layoutParams.height = 1;
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    public boolean isLoadMoreEnd() {
        return isLoadMoreEnd;
    }

    @Override
    public final int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return VIEW_TYPE_LOAD_MORE;
        }
        return getAdvItemViewType(position);
    }

    public int getAdvItemViewType(int position) {
        return VIEW_TYPE_DATA;
    }

    @Override
    public final int getItemCount() {
        return getAdvItemCount() + 1;
    }

    public int getAdvItemCount() {
        return datas.size();
    }

    public void setOnItemClickListener(AdvancedRecyclerView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setLordMoreViewLayout(@LayoutRes int lordMoreViewLayout) {
        if (lordMoreViewLayout > 0)
            this.lordMoreViewLayout = lordMoreViewLayout;
    }

    static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mProgress;
        TextView mTextView;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            mProgress = (ProgressBar) itemView.findViewById(R.id.more_progress);
            mTextView = (TextView) itemView.findViewById(R.id.more_text);
        }
    }


}
