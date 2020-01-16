package com.e.bisatau.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.e.bisatau.R;
import com.e.bisatau.model.NewsModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<NewsModel> implements View.OnClickListener{

    private ArrayList<NewsModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageView image;
    }

    public NewsAdapter(ArrayList<NewsModel> data, Context context) {
        super(context, R.layout.list_news, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        NewsModel dataModel=(NewsModel) object;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NewsModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_news, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imageView);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        Glide.with(getContext()).load(dataModel.getImage()).into(viewHolder.image);
        viewHolder.txtName.setText(dataModel.getTitle());
        // Return the completed view to render on screen
        return convertView;
    }
}