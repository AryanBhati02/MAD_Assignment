// MainActivity.java
package com.mad.photogallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvSelectedFolder;
    private File     saveFolder;
    private Uri      photoUri;

    private final ActivityResultLauncher<String[]> permissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean allGranted = true;
            for (Boolean granted : result.values())
                if (!granted) { allGranted = false; break; }
            if (allGranted)
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Some permissions denied.", Toast.LENGTH_LONG).show();
        });

    private final ActivityResultLauncher<Uri> cameraLauncher =
        registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (success) {
                Toast.makeText(this, "Photo saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera cancelled.", Toast.LENGTH_SHORT).show();
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide default ActionBar — we have our own header
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // App-private external pictures folder — works on all Android versions
        saveFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (saveFolder != null && !saveFolder.exists()) saveFolder.mkdirs();

        tvSelectedFolder = findViewById(R.id.tvSelectedFolder);
        tvSelectedFolder.setText(saveFolder != null ? saveFolder.getAbsolutePath() : "Unavailable");

        Button btnPermissions = findViewById(R.id.btnRequestPermissions);
        Button btnTakePhoto   = findViewById(R.id.btnTakePhoto);
        Button btnGallery     = findViewById(R.id.btnOpenGallery);

        btnPermissions.setOnClickListener(v -> {
            animateButton(v);
            requestAllPermissions();
        });

        btnTakePhoto.setOnClickListener(v -> {
            animateButton(v);
            takePhoto();
        });

        btnGallery.setOnClickListener(v -> {
            animateButton(v);
            openGallery();
        });

        // Animate buttons in on launch
        animateViewsIn();
    }

    // Staggered fade-in animation for all buttons
    private void animateViewsIn() {
        int[] ids = {
            R.id.btnRequestPermissions,
            R.id.btnChooseFolder,
            R.id.btnTakePhoto,
            R.id.btnOpenGallery
        };
        for (int i = 0; i < ids.length; i++) {
            View v = findViewById(ids[i]);
            v.setAlpha(0f);
            v.setTranslationY(30f);
            v.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(300 + (i * 80L))
                .start();
        }
    }

    // Scale press animation
    private void animateButton(View v) {
        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
            .withEndAction(() ->
                v.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
            ).start();
    }

    private void requestAllPermissions() {
        List<String> perms = new ArrayList<>();
        perms.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
                perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        permissionLauncher.launch(perms.toArray(new String[0]));
    }

    private void takePhoto() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Grant permissions first!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (saveFolder == null) {
            Toast.makeText(this, "Storage unavailable.", Toast.LENGTH_SHORT).show();
            return;
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File   photoFile = new File(saveFolder, "IMG_" + timestamp + ".jpg");

        photoUri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", photoFile);
        cameraLauncher.launch(photoUri);
    }

    private void openGallery() {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra("folderPath", saveFolder.getAbsolutePath());
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
