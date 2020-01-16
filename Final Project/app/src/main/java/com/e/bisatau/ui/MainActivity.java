package com.e.bisatau.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.e.bisatau.R;
import com.e.bisatau.adapter.NewsAdapter;
import com.e.bisatau.http.ApiClient;
import com.e.bisatau.http.ApiService;
import com.e.bisatau.model.NewsModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    ArrayList<NewsModel> newsModels;
    ListView listView;
    private static NewsAdapter adapter;
    ApiService apiService;
    ProgressBar progressBar;
    TextView error;
    EditText inputSearch;
    private ProgressDialog dialog;
    Integer page = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //custom toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("Bisa Tau");

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(); // your code
                pullToRefresh.setRefreshing(false);
            }
        });

        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        progressBar = (ProgressBar) findViewById(R.id.loading);
        error = (TextView) findViewById(R.id.error);
        // get data dari api
        listView=(ListView)findViewById(R.id.list);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        newsModels= new ArrayList<>();
        loadData();
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    Bundle data = new Bundle();
                    data.putString("keyword",  inputSearch.getText().toString());
                    intent.putExtras(data);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        inputSearch.setVisibility(View.GONE);
        newsModels.clear();
        apiService.getNews("30", "desc")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<JsonArray>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JsonArray response) {
                        progressBar.setVisibility(View.GONE);
                        inputSearch.setVisibility(View.VISIBLE);
                        for (int i= 0 ; i < response.size(); i ++) {
                            JsonObject data = response.get(i).getAsJsonObject();
                            JsonObject title = data.get("title").getAsJsonObject();

                            String renderedTitle = title.get("rendered").getAsString();

                            newsModels.add(new NewsModel(data.get("id").toString(),renderedTitle, renderedTitle,
                                    data.get("jetpack_featured_media_url").getAsString()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                        error.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onComplete() {
                        adapter= new NewsAdapter(newsModels,getApplicationContext());
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Context context = getApplicationContext();
                                NewsModel dataModel= newsModels.get(position);
                                //Toast.makeText(context, dataModel.getTitle(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, DetailNews.class);
                                Bundle data = new Bundle();
                                data.putString("id", dataModel.getId());
                                intent.putExtras(data);
                                startActivity(intent);
                            }
                        });

                        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

                            @Override
                            public void onScrollStateChanged(AbsListView view, int scrollState) {
                                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                                        && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
                                        listView.getFooterViewsCount()) >= (adapter.getCount() - 1)) {

                                    dialog.setMessage("Loadmore, please wait.");
                                    dialog.show();
                                    MainActivity.this.loadMoreData();
                                }
                            }

                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                            }
                        });
                    }
                });
    }

    private void loadMoreData() {
        page = page + 1;
        apiService.getNewsLoadmore("30", "desc", page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    new Observer<JsonArray>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(JsonArray response) {
                            for (int i= 0 ; i < response.size(); i ++) {
                                JsonObject data = response.get(i).getAsJsonObject();
                                JsonObject title = data.get("title").getAsJsonObject();

                                String renderedTitle = title.get("rendered").getAsString();

                                newsModels.add(new NewsModel(data.get("id").toString(),renderedTitle, renderedTitle,data.get("jetpack_featured_media_url").getAsString()));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                );
    }


}
