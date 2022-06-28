package com.example.mig.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mig.databinding.ItemBlogSliderBinding;
import com.prof.rssparser.Article;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogPostVH> {

    private List<Article> articles;
    private Context mContext;
    private ItemBlogSliderBinding binding;

    public BlogAdapter(Context mContext, List<Article> articles) {
        this.mContext = mContext;
        this.articles = articles;
    }

    @NonNull
    @Override
    public BlogPostVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        binding = ItemBlogSliderBinding.inflate(inflater, parent, false);
        return new BlogPostVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogPostVH holder, int position) {
        Article article = articles.get(position);
        holder.binding.articleNameTv.setText(article.getTitle());
        holder.binding.pubDateTv.setText(article.getPubDate());

        holder.itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getLink()));
            mContext.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    static class BlogPostVH extends RecyclerView.ViewHolder {

        private ItemBlogSliderBinding binding;

        public BlogPostVH(ItemBlogSliderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}