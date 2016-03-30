package l.android.lee.my.demo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.util.ArrayList;
import java.util.List;

import my.lee.android.l.AdvancedRecyclerView;

public class MainActivity extends AppCompatActivity {
    private int count;

    private AdvancedRecyclerView mRecyclerView;
    TestAdvRecyclerViewAdapter adapter;
    List<Integer> datas;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mRecyclerView = (AdvancedRecyclerView) findViewById(R.id.recyclerview);
        initDate();
    }

    private void initDate() {
        datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            datas.add(i);
        }
        adapter = new TestAdvRecyclerViewAdapter(this, datas);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(adapter);
                initSticky();
            }
        }, 3000);
        mRecyclerView.setOnLoadMoreListener(new AdvancedRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMore();
                    }
                }, 1000);
            }
        });
        mRecyclerView.setEndVisible(true);

    }

    private void initSticky() {
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        RecyclerView recyclerView = mRecyclerView.getRecyclerView();
        recyclerView.addItemDecoration(headersDecor);

        StickyRecyclerHeadersTouchListener touchListener =
                new StickyRecyclerHeadersTouchListener(recyclerView, headersDecor);
        touchListener.setOnHeaderClickListener(
                new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View header, int position, long headerId) {
                        Toast.makeText(MainActivity.this, "Header position: " + position + ", id: " + headerId,
                                Toast.LENGTH_SHORT).show();
                    }
                });
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
    }

    private void loadMore() {
        if (isLoading)
            return;
        isLoading = true;
        if (datas.size() > 50) {
            adapter.loadEnd();
            return;
        }
        for (int i = 0; i < 3; i++) {
            datas.add(i);
        }
        mRecyclerView.getRecyclerView().getAdapter().notifyItemRangeInserted(datas.size() - 3, 3);
        isLoading = false;
        mRecyclerView.setLoadingMore(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            switch (count%4){
                case 0:mRecyclerView.showError();break;
                case 1:mRecyclerView.showEmpty();break;
                case 2:mRecyclerView.showProgress();break;
                case 3:mRecyclerView.showDataWidthAnim();break;
            }
            count++;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
