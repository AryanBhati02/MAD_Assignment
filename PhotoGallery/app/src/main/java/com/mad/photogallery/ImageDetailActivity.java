// ImageDetailActivity.java
package com.mad.photogallery;

import android.graphics.Color;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailActivity extends AppCompatActivity {

    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#0A0A0F"));
        window.setNavigationBarColor(Color.parseColor("#0A0A0F"));

        setContentView(R.layout.activity_image_detail);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);

        String imagePath = getIntent().getStringExtra("imagePath");
        imageFile = new File(imagePath);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Photo Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ImageView ivPreview   = findViewById(R.id.ivDetailPreview);
        TextView  tvName      = findViewById(R.id.tvDetailName);
        TextView  tvPath      = findViewById(R.id.tvDetailPath);
        TextView  tvSize      = findViewById(R.id.tvDetailSize);
        TextView  tvDate      = findViewById(R.id.tvDetailDateTaken);
        Button    btnDelete   = findViewById(R.id.btnDeleteImage);

        Glide.with(this)
                .load(imageFile)
                .fitCenter()
                .into(ivPreview);

        tvName.setText(imageFile.getName());
        tvPath.setText(imageFile.getAbsolutePath());
        tvSize.setText(getReadableSize(imageFile.length()));
        tvDate.setText(getDateTaken(imageFile));

        btnDelete.setOnClickListener(v -> {
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(80).start())
                .start();
            showDeleteDialog();
        });

        // Animate detail cards in
        findViewById(R.id.ivDetailPreview).setAlpha(0f);
        findViewById(R.id.ivDetailPreview).animate().alpha(1f).setDuration(400).start();
    }

    private String getReadableSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return new DecimalFormat("#.#").format(kb) + " KB";
        return new DecimalFormat("#.#").format(kb / 1024.0) + " MB";
    }

    private String getDateTaken(File file) {
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            String exifDate = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);
            if (exifDate != null && !exifDate.isEmpty()) {
                Date d = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault()).parse(exifDate);
                if (d != null)
                    return new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(d);
            }
        } catch (IOException | java.text.ParseException ignored) {}
        return new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(new Date(file.lastModified()));
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle("Delete Photo")
                .setMessage("Permanently delete \"" + imageFile.getName() + "\"?")
                .setPositiveButton("Delete", (d, w) -> deleteImage())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteImage() {
        if (imageFile.exists() && imageFile.delete()) {
            Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            Toast.makeText(this, "Could not delete photo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
