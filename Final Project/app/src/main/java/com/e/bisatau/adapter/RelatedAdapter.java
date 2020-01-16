package com.e.bisatau.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.bisatau.R;
import com.e.bisatau.model.RelatedNewsModel;
import com.e.bisatau.ui.DetailNews;

import java.util.ArrayList;
import java.util.List;

public class RelatedAdapter extends RecyclerView.Adapter<RelatedAdapter.MyViewHolder> {
    private List<RelatedNewsModel> mDataset;
    private Context context;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleContent;
        public ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            titleContent = (TextView) itemView.findViewById(R.id.title_content);
            image = (ImageView) itemView.findViewById(R.id.related_image);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RelatedAdapter(List<RelatedNewsModel> myDataset, Context context) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RelatedAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_related, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final RelatedNewsModel dataN = mDataset.get(position);

        Glide.with(context).load(dataN.getImage()).into(holder.image);
        holder.titleContent.setText(dataN.getTitle());
        holder.image.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (context instanceof DetailNews) {
                    ((DetailNews)context).clickRelated(dataN.getId());
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}