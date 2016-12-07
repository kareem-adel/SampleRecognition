package com.example.kareem.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 0;
    static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView MainActivity_preview;
    private TextView MainActivity_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity_preview = (ImageView) findViewById(R.id.MainActivity_preview);
        Button mainActivity_testSample = (Button) findViewById(R.id.MainActivity_testSample);

        mainActivity_testSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        MainActivity_result = (TextView) findViewById(R.id.MainActivity_result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            if (imageBitmap != null) {
                MainActivity_preview.setImageBitmap(imageBitmap);
                int centerColor = imageBitmap.getPixel(0, 0);
                Toast.makeText(getApplicationContext(), "Red : " + Color.red(centerColor) + ", Green : " + Color.green(centerColor) + ", Blue : " + Color.blue(centerColor), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            analyzeImage();
        }

    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("sample", ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void analyzeImage() {
        int targetW = MainActivity_preview.getWidth();
        int targetH = MainActivity_preview.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmapScaled = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        MainActivity_preview.setImageBitmap(bitmapScaled);

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);

        int centerColor = bitmap.getPixel(bitmap.getWidth() / 2, bitmap.getHeight() / 2);

        int red = Color.red(centerColor);
        int green = Color.green(centerColor);
        int blue = Color.blue(centerColor);
        int alpha = Color.alpha(centerColor);

        MainActivity_result.setText("Red = " + red + " " + "Green = " + green + " " + "Blue = " + blue + " " + "Alpha = " + alpha);
        System.out.println("Red = " + red);
        System.out.println("Green = " + green);
        System.out.println("Blue = " + blue);
        System.out.println("Alpha = " + alpha);

        //get the red channel intensity for each sample and the known result and use https://mycurvefit.com/ to insert the red channel intensity as X
        //axis and the known result as Y axis to get the linear relation or any other relation
        //use this relation to know new results for new intensities

    }
}
