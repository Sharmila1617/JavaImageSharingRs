package com.example.javaimagesharing;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();
    ImageView imageView, galleryImage, cameraImage;
    Bitmap cameraBitmap;
    Uri selectedCameraUri;
    Uri selectedGalleryUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "------------->onCreate");

        imageView = findViewById(R.id.imageView);
        galleryImage = findViewById(R.id.imageView2);
        cameraImage = findViewById(R.id.imageView3);
        }

    ActivityResultLauncher<Intent> activityResultGalleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();

                        selectedGalleryUri = data.getData();

                        Log.d(TAG, "------------>galleryImage:" + selectedGalleryUri);

                        if (selectedGalleryUri != null) {
                            galleryImage.setImageURI(selectedGalleryUri);
                        }
                    }
                }
            });

    public void selectCameraMethod(View view) {

        Log.d(TAG, "-------------->cameraMethod");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        activityResultLauncher.launch(Intent.createChooser(intent, "capture Image"));
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();

                        selectedCameraUri = (Uri) data.getExtras().get("data");

                        selectedCameraUri  = getImageUri(cameraBitmap);

                        Log.d(TAG, "------------->camera:" + selectedCameraUri);

                        if (cameraBitmap != null) {
                            cameraImage.setImageBitmap(cameraBitmap);
                        }
                    }
                }
            });

    private Uri getImageUri(Bitmap uri) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        cameraBitmap.compress(Bitmap.CompressFormat.JPEG,100, bytes);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), cameraBitmap, "Title", null);
        return Uri.parse(path);
    }

    public void selectGalleryMethod(View view) {
        Log.d(TAG, "------------->selectGalleryMethod");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        activityResultGalleryLauncher.launch(Intent.createChooser(intent,"select Image"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_image_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "------------->onOptionsItemSelected");

        switch (item.getItemId()) {
            case R.id.drawable_id: {
                Log.d(TAG, "------------->share drawable Image");
                shareDrawableImage();
                break;
            }
            case R.id.camera_id:{
                Log.d(TAG, "------------->share camera Image");
                shareCameraImage();
                break;
            }
            case R.id.gallery_id:{
                Log.d(TAG, "------------->share gallery Image");
                shareGalleryImage();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void shareGalleryImage() {
        Log.d(TAG, "------------->shareGalleryImage");
        shareMethod(selectedGalleryUri);
    }

    private void shareCameraImage() {
        Log.d(TAG, "------------->shareCameraImage");
        shareMethod(selectedCameraUri);
    }

    private void shareDrawableImage() {
        Log.d(TAG, "------------->shareDrawableImage");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.daisy);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);

        Uri uri = Uri.parse(path);

        Log.d(TAG, "------------->Uri" + uri);

        shareMethod(uri);
    }

    private void shareMethod(Uri uri) {

        Log.d(TAG, "------------->shareMethod");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share Via"));
    }
}