package com.assignment.customcamerasample.screens;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.assignment.customcamerasample.BaseActivity;
import com.assignment.customcamerasample.R;
import com.assignment.customcamerasample.databinding.PreviewActivityBinding;
import com.assignment.customcamerasample.utils.Utils;

import java.io.File;
import java.io.FileInputStream;

public class PreviewActivity extends BaseActivity {

    private PreviewActivityBinding previewActivityBinding;
    private String filePath;
    private String fileName;
    private Bitmap bitmap;
    private int REQUEST_STORAGE_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        previewActivityBinding = DataBindingUtil.setContentView(this, R.layout.preview_activity);
        inIt();
    }

    void inIt() {
        setTitle(getString(R.string.preview_screen));
        filePath = getIntent().getStringExtra(CaptureActivity.PATH_PATH_EXTRA);
        fileName = getIntent().getStringExtra(CaptureActivity.PATH_NAME_EXTRA);
        if (filePath != null && fileName != null) {
            try {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File path1 = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File f = new File(path1, fileName);
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
                bitmap = Utils.rotateImage(bitmap, filePath);
                Thread.sleep(300);
                previewActivityBinding.imgPreview.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        previewActivityBinding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.fileDelete(filePath);
                finish();
            }
        });
        previewActivityBinding.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(PreviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PreviewActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                } else {
                    Utils.createDirectoryAndSaveFile(PreviewActivity.this, bitmap, fileName);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.fileDelete(filePath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "" + getString(R.string.storage_permission), Toast.LENGTH_LONG).show();
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Utils.createDirectoryAndSaveFile(PreviewActivity.this, bitmap, fileName);
                finish();
            }
        }
    }
}
