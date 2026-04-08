// adapter/ImageAdapter.java
package com.mad.photogallery.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mad.photogallery.R;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    // PUBLIC interface
    public interface OnImageClickListener {
        void onImageClick(File imageFile);
    }

    private final Context             context;
    private       List<File>          imageFiles;
    private final OnImageClickListener listener;
    private final int                 cellSize;

    public ImageAdapter(Context context, List<File> imageFiles, OnImageClickListener listener) {
        this.context    = context;
        this.imageFiles = imageFiles;
        this.listener   = listener;

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth   = dm.widthPixels;
        int spacing       = (int)(4 * dm.density);
        this.cellSize     = (screenWidth - spacing) / 3;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);

        // Force exact square dimensions on the root view
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width  = cellSize;
        lp.height = cellSize;
        v.setLayoutParams(lp);

        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        File imageFile = imageFiles.get(position);

        // Also set on the ImageView
        ViewGroup.LayoutParams lp = holder.imageView.getLayoutParams();
        lp.width  = cellSize;
        lp.height = cellSize;
        holder.imageView.setLayoutParams(lp);

        Glide.with(context)
                .load(imageFile)
                .override(cellSize, cellSize)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(150))
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v ->
                v.animate().scaleX(0.93f).scaleY(0.93f).setDuration(80)
                        .withEndAction(() -> {
                            v.animate().scaleX(1f).scaleY(1f).setDuration(80).start();
                            listener.onImageClick(imageFile);
                        }).start());
    }

    @Override
    public int getItemCount() { return imageFiles.size(); }

    // PUBLIC ViewHolder so it's accessible from outside the adapter
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivGridImage);
        }
    }
}
