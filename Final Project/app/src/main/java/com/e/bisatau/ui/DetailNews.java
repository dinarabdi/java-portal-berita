package com.e.bisatau.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e.bisatau.R;
import com.e.bisatau.adapter.RelatedAdapter;
import com.e.bisatau.http.ApiClient;
import com.e.bisatau.http.ApiService;
import com.e.bisatau.model.RelatedNewsModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailNews extends AppCompatActivity {
    ArrayList<RelatedNewsModel> newsModels;
    ApiService apiService;
    ProgressBar loading;
    TextView title_news;
    TextView date_news;
    TextView content;
    ScrollView detail;
    ImageView image_news;
    private RelatedAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);


        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        loading = (ProgressBar) findViewById(R.id.loading);
        detail = (ScrollView) findViewById(R.id.layout_detail);
        title_news = (TextView) findViewById(R.id.title_news);
        content = (TextView) findViewById(R.id.content);
        date_news = (TextView) findViewById(R.id.date);
        image_news = (ImageView) findViewById(R.id.image_news);
        Bundle dataNews = getIntent().getExtras();
        newsModels= new ArrayList<>();
        if (dataNews != null) {
            loadDetail(dataNews.getString("id"));
        }
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


    private void loadDetail(String id) {
        newsModels.clear();
        loading.setVisibility(View.VISIBLE);
        detail.setVisibility(View.GONE);
        apiService.getNewsDetail(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(new Observer<JsonObject>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(JsonObject data) {
                    loading.setVisibility(View.GONE);
                    detail.setVisibility(View.VISIBLE);
                    JsonObject title = data.get("title").getAsJsonObject();
                    String renderedTitle = title.get("rendered").getAsString();
                    String date = data.get("date").getAsString();
                    String image = data.get("jetpack_featured_media_url").getAsString();
                    title_news.setText(renderedTitle);
                    date_news.setText(date);
                    Glide.with(getApplicationContext()).load(image).into(image_news);


                    // mendapatkan isi konten
                    JsonObject konten = data.get("content").getAsJsonObject();
                    String renderedKonten = konten.get("rendered").getAsString();
                    content.setText(Html.fromHtml(renderedKonten));

                    JsonArray related = data.get("jetpack-related-posts").getAsJsonArray();
                    for (int i= 0 ; i < related.size(); i ++) {
                        JsonObject dataRelated = related.get(i).getAsJsonObject();
                        String title_related = dataRelated.get("title").getAsString();
                        String id_related = dataRelated.get("id").getAsString();
                        JsonObject imageRelated = dataRelated.get("img").getAsJsonObject();
                        String imageUrl = imageRelated.get("src").getAsString();

                        newsModels.add(new RelatedNewsModel(id_related,title_related ,imageUrl));

                        RecyclerView recyclerView = findViewById(R.id.related);
                        LinearLayoutManager horizontalLayoutManager
                                = new LinearLayoutManager(DetailNews.this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(horizontalLayoutManager);

                        adapter = new RelatedAdapter(newsModels, DetailNews.this);
                        recyclerView.setAdapter(adapter);
                    }

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
    }

    public void clickRelated(final String id) {
        detail.fullScroll(ScrollView.FOCUS_UP);
        new android.os.Handler().postDelayed(
        new Runnable() {
            public void run() {
                loadDetail(id);
            }
        },
        500);

    }
}
