// GalleryActivity.java — FIXED: correct imports
package com.mad.photogallery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.photogallery.adapter.ImageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<File>   imageFiles;
    private LinearLayout emptyView;
    private String       folderPath;

    private static final String[] IMAGE_EXTENSIONS = {".jpg",".jpeg",".png",".webp",".bmp"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0A0A0F"));
        getWindow().setNavigationBarColor(Color.parseColor("#0A0A0F"));
        setContentView(R.layout.activity_gallery);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarGallery);
        setSupportActionBar(toolbar);

        folderPath = getIntent().getStringExtra("folderPath");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Photos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        emptyView    = findViewById(R.id.tvEmptyGallery);
        recyclerView = findViewById(R.id.recyclerViewGallery);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);

        imageFiles   = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageFiles, file -> openImageDetail(file));
        recyclerView.setAdapter(imageAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages();
    }

    private void loadImages() {
        imageFiles.clear();
        if (folderPath == null) { showEmpty(true); return; }

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) { showEmpty(true); return; }

        File[] files = folder.listFiles();
        if (files != null) {
            Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
            for (File f : files)
                if (f.isFile() && isImageFile(f.getName()))
                    imageFiles.add(f);
        }

        imageAdapter.notifyDataSetChanged();
        showEmpty(imageFiles.isEmpty());
    }

    private boolean isImageFile(String name) {
        String lower = name.toLowerCase();
        for (String ext : IMAGE_EXTENSIONS) if (lower.endsWith(ext)) return true;
        return false;
    }

    private void showEmpty(boolean empty) {
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void openImageDetail(File file) {
        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra("imagePath", file.getAbsolutePath());
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
