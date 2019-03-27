package roborus.com.roborusfacesdk.faceeagsample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import io.fabric.sdk.android.Fabric;
import roborus.com.roborusfacesdk.FaceResult;
import roborus.com.roborusfacesdk.RoborusFaceClient;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Roborus";

    public static String YOUR_API_KEY = "";
    private int RESULT_LOAD_IMG = 333;
    private ProgressBar progressBar;
    private ImageView imageViewFace;
    private TextView textViewStatus, textViewResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        imageViewFace = findViewById(R.id.imageViewFace);
        imageViewFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detect();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.buttonChangeImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });
        findViewById(R.id.buttonDetect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detect();
            }
        });
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewResult = findViewById(R.id.textViewResult);
        RoborusFaceClient.initWithApiKey(this, YOUR_API_KEY);
        checkPermission();
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {

                // Method 1
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageViewFace.setImageBitmap(selectedImage);
                detect();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void detect() {
        final Bitmap bitmap = ((BitmapDrawable)imageViewFace.getDrawable()).getBitmap();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        RoborusFaceClient.getInstance().detect(inputStream, true, true, true, new RoborusFaceClient.DetectCallback() {
            @Override
            public void onDetect(FaceResult faceResult) {
                Log("detect success: " + faceResult.faces.size());

                Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                myPaint.setColor(0x00FF00);
                String retValue = faceResult.toJSON();
                Log(retValue);
                textViewResult.setText(faceResult.toJSON());
            }
            @Override
            public void onError(String error) {
                Log("detect error : " + error);
            }
        });

    }

    private void checkPermission() {
        Log.d("MainActivity", "checkPermission");
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission OK", Toast.LENGTH_LONG).show();
                detect();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Need Permission :" + deniedPermissions.toString(), Toast.LENGTH_LONG).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .check();
    }


    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void Log(String strLog) {
        Log.d(TAG, strLog);
        textViewStatus.setText(strLog);
    }

    public static void showAlert(Context context, final String strTitle, final String strMessage) {
        Log.v("ALERT", strMessage);
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        ab.setTitle(strTitle);
        ab.setMessage(strMessage);
        ab.setPositiveButton(android.R.string.ok, null);
        ab.show();
    }

}
