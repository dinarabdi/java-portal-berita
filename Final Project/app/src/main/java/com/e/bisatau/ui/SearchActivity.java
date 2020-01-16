package com.e.bisatau.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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


public class SearchActivity extends AppCompatActivity {
    ArrayList<NewsModel> newsModels;
    ListView listView;
    private static NewsAdapter adapter;
    ApiService apiService;
    ProgressBar progressBar;
    TextView error;
    EditText inputSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        progressBar = (ProgressBar) findViewById(R.id.loading);
        error = (TextView) findViewById(R.id.error);
        // get data dari api
        listView=(ListView)findViewById(R.id.list);
        newsModels= new ArrayList<>();

        Bundle dataSearch = getIntent().getExtras();
        loadData(dataSearch.getString("keyword"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadData(String keyword) {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getSearch(keyword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<JsonArray>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JsonArray response) {
                        progressBar.setVisibility(View.GONE);
                        for (int i= 0 ; i < response.size(); i ++) {
                            JsonObject data = response.get(i).getAsJsonObject();
                            JsonObject title = data.get("title").getAsJsonObject();

                            String renderedTitle = title.get("rendered").getAsString();

                            newsModels.add(new NewsModel(data.get("id").toString(),renderedTitle, renderedTitle,data.get("jetpack_featured_media_url").getAsString()));
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
                                Intent intent = new Intent(SearchActivity.this, DetailNews.class);
                                Bundle data = new Bundle();
                                data.putString("id", dataModel.getId());
                                intent.putExtras(data);
                                startActivity(intent);
                            }
                        });
                    }
                });
    }
}