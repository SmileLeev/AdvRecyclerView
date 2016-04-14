package my.lee.android.l;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;


/**
 * User: Smile(lijianhy1990@gmail.com)
 * Date: 2016-03-14
 * Time: 14:52
 */
public class AdvancedRecyclerView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipe;
    private SwipeRefreshLayout mSwipeEmpty;
    private FrameLayout mViewEmpty;
    private FrameLayout mVewProgress;
    private FrameLayout mViewError;
    private RecyclerView mRecyclerView;

    private OnRefreshListener mRefreshListener;
    private OnLoadMoreListener mLoadMoreListener;
    private OnItemClickListener mItemClickListener;

    private int mEmptyId;
    private int mProgressId;
    private int mErrorId;
    private boolean endVisible;
    private BaseAdvRecyclerViewAdapter mAdapter;
    private int gridCount;

    public AdvancedRecyclerView(Context context) {
        super(context);
        initView();
    }

    public AdvancedRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public AdvancedRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initView();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.advrecyclerview);
        try {
            mEmptyId = a.getResourceId(R.styleable.advrecyclerview_adv_layout_empty, 0);
            mProgressId = a.getResourceId(R.styleable.advrecyclerview_adv_layout_progress, 0);
            mErrorId = a.getResourceId(R.styleable.advrecyclerview_adv_layout_error, 0);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.adv_layout_advanced_recyclerview, this);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeEmpty = (SwipeRefreshLayout) findViewById(R.id.swipe_empty);
        mViewEmpty = (FrameLayout) findViewById(R.id.empty);
        mVewProgress = (FrameLayout) findViewById(R.id.more_progress);
        mViewError = (FrameLayout) findViewById(R.id.error);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mSwipe.setOnRefreshListener(this);
        mSwipeEmpty.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleCount = recyclerView.getChildCount();
                int itemCount = lm.getItemCount();
                int firstPosition = lm.findFirstVisibleItemPosition();
                if (itemCount > 0 && visibleCount + firstPosition == itemCount && mLoadMoreListener != null) {
                    mLoadMoreListener.onLoadMore();
                }
            }
        });
        showProgress();
    }

    public void showError() {
        showData(false);
        mViewError.setVisibility(View.VISIBLE);
        mViewEmpty.setVisibility(View.INVISIBLE);
        mVewProgress.setVisibility(View.INVISIBLE);
        if (mErrorId <= 0)
            mErrorId = R.layout.adv_layout_error;
        if (mViewError.getChildCount() <= 0) {
            LayoutInflater.from(getContext()).inflate(mErrorId, mViewError);
        }
    }

    public void showEmpty() {
        showData(false);
        mViewError.setVisibility(View.INVISIBLE);
        mViewEmpty.setVisibility(View.VISIBLE);
        mVewProgress.setVisibility(View.INVISIBLE);
        if (mEmptyId <= 0)
            mEmptyId = R.layout.adv_layout_empty;
        if (mViewEmpty.getChildCount() <= 0) {
            LayoutInflater.from(getContext()).inflate(mEmptyId, mViewEmpty);
        }
    }

    public void showProgress() {
        showData(false);
        mViewError.setVisibility(View.INVISIBLE);
        mViewEmpty.setVisibility(View.INVISIBLE);
        mVewProgress.setVisibility(View.VISIBLE);
        if (mProgressId <= 0)
            mProgressId = R.layout.adv_layout_progress;
        if (mVewProgress.getChildCount() <= 0) {
            LayoutInflater.from(getContext()).inflate(mProgressId, mVewProgress);
        }
    }

    public void loadEnd() {
        if (mAdapter != null)
            mAdapter.loadEnd();
    }

    public void showData(boolean isShow) {
        mSwipe.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        mSwipeEmpty.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
    }

    public void showDataWidthAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mSwipe.setAlpha(value);
                mSwipeEmpty.setAlpha(1 - value);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mSwipe.setAlpha(0);
                mSwipeEmpty.setAlpha(1);
                mSwipe.setVisibility(View.VISIBLE);
                mSwipeEmpty.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSwipeEmpty.setVisibility(View.INVISIBLE);
                mSwipeEmpty.setAlpha(1);
            }
        });
        animator.setDuration(1000);
        animator.start();
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setAdapter(BaseAdvRecyclerViewAdapter adapter) {
        this.mAdapter = adapter;
        mAdapter.setOnItemClickListener(mItemClickListener);
        mRecyclerView.setAdapter(adapter);
        mAdapter.setEndVisible(endVisible);
        if (mAdapter.getAdvItemCount() > 0) {
            showDataWidthAnim();
        }
        setLoadingMore(false);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * 设置grid类型单行个数
     */
    public void setGridItemCount(int count) {
        this.gridCount = count;
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), count);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == mAdapter.getItemCount() - 1)
                    return gridCount;
                return 1;
            }
        });
        setLayoutManager(layoutManager);
    }

    public void setLayoutManagerSpan(final GridLayoutManager.SpanSizeLookup gs) {
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == mAdapter.getItemCount() - 1)
                    return gridCount;
                return gs.getSpanSize(position);
            }
        });
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onRefresh() {
        if (mRefreshListener != null) {
            mRefreshListener.onRefresh();
        } else {
            setRefreshing(false);
        }
    }

    public void setEndVisible(boolean visible) {
        this.endVisible = visible;
    }

    public void setRefreshing(boolean isRefresh) {
        mSwipeEmpty.setRefreshing(isRefresh);
        mSwipe.setRefreshing(isRefresh);
    }

    public void setLoadingMore(boolean isLoadMore) {
        if (mLoadMoreListener != null && isLoadMore)
            mLoadMoreListener.onLoadMore();
        if (!isLoadMore) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (!mAdapter.isLoadMoreEnd() && mAdapter.getItemCount() == mRecyclerView.getLayoutManager().getChildCount())
                        setLoadingMore(true);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(listener);
            mAdapter.notifyDataSetChanged();
        }
    }

    public final void notifyDataSetChanged() {
        showData(mAdapter.getAdvItemCount() > 0);
        mAdapter.notifyDataSetChanged();
    }

    public final void notifyItemChanged(int position) {
        showData(mAdapter.getAdvItemCount() > 0);
        mAdapter.notifyItemChanged(position);
    }

    public final void notifyItemChanged(int position, Object payload) {
        showData(mAdapter.getAdvItemCount() > 0);
        mAdapter.notifyItemChanged(position, payload);
    }

    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        showData(mAdapter.getAdvItemCount() > 0);
        mAdapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    public final void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
        showData(mAdapter.getAdvItemCount() > 0);
        mAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
    }

    public final void notifyItemInserted(int position) {
        showData(mAdapter.getAdvItemCount() > 0);
        mAdapter.notifyItemInserted(position);
    }

    public final void notifyItemMoved(int fromPosition, int toPosition) {
        showData(mAdapter.getAdvItemCount() > 0);
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    public final void notifyItemRangeInserted(int positionStart, int itemCount) {
        showData(mAdapter.getAdvItemCount() > 0);
        mAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    public final void notifyItemRemoved(int position) {
        showData(mAdapter.getAdvItemCount() > 0);
        mAdapter.notifyItemRemoved(position);
    }

    public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
        showData(mAdapter.getAdvItemCount() > 0);
        mAdapter.notifyItemRangeRemoved(positionStart, itemCount);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
