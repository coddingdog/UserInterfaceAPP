package com.echo.echofarm.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.echo.echofarm.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<PostInfo> postInfoArrayList;

    public PostAdapter(Context context, ArrayList<PostInfo> postInfoArrayList) {
        this.context = context;
        this.postInfoArrayList = postInfoArrayList;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.post_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        PostInfo postInfo = postInfoArrayList.get(position);
        holder.title.setText(postInfo.getTitle());
        holder.tags.setText(postInfo.getTags());
        holder.postImage.setImageResource(postInfo.getImageUri());
    }

    @Override
    public int getItemCount() {
        return postInfoArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, tags;
        ImageView postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.imageView_postLayout);
            title = itemView.findViewById(R.id.title_postLayout);
            tags = itemView.findViewById(R.id.tags_postLayout);
        }
    }
}
