package my.lee.android.l;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
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

    private int mEmptyId;
    private int mProgressId;
    private int mErrorId;
    private BaseAdvRecyclerViewAdapter mAdapter;

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
            mEmptyId = a.getResourceId(R.styleable.advrecyclerview_layout_empty, 0);
            mProgressId = a.getResourceId(R.styleable.advrecyclerview_layout_progress, 0);
            mErrorId = a.getResourceId(R.styleable.advrecyclerview_layout_error, 0);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_advanced_recyclerview, this);
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
            mErrorId = R.layout.layout_error;
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
            mEmptyId = R.layout.layout_empty;
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
            mProgressId = R.layout.layout_progress;
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

    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    public void setAdapter(BaseAdvRecyclerViewAdapter adapter) {
        this.mAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
        if (mAdapter.getAdvItemCount() > 0) {
            showData(true);
        }
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
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
        mAdapter.setEndVisible(visible);
    }

    public void setRefreshing(boolean isRefresh) {
        mSwipeEmpty.setRefreshing(isRefresh);
        mSwipe.setRefreshing(isRefresh);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
