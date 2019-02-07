package com.atharvredij.instagramclone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;

    public PostsAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.post_item_layout,
                parent, false);
        return new PostViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        Picasso.get().load(post.imageUrl).noPlaceholder()
                .centerCrop().fit().into(holder.postImage);

        holder.postTitle.setText(post.title);
        holder.postDescription.setText(post.description);
    }

    @Override
    public int getItemCount() {
        if (postList == null) {
            return 0;
        } else {
            return postList.size();
        }

    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView postImage;
        TextView postTitle, postDescription;

        public PostViewHolder(View itemView) {
            super(itemView);

            postImage = itemView.findViewById(R.id.postImage);
            postTitle = itemView.findViewById(R.id.postTitle);
            postDescription = itemView.findViewById(R.id.postDescription);
        }
    }
}
