package com.gabrieltorelo.whatsappclone.view.activities.status;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gabrieltorelo.whatsappclone.R;
import com.gabrieltorelo.whatsappclone.databinding.ActivityStatusBinding;
import com.gabrieltorelo.whatsappclone.tools.CameraPreview;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class StatusActivity extends AppCompatActivity {

    private ActivityStatusBinding binding;
    private int IMAGE_GALLERY_REQUEST = 111;
    private static final int CAMERA_OPEN_CODE = 123;
    public int i = 0;
    public boolean pic_video = false;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture = getPictureCallback();
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    public static Bitmap bitmap;
    public Handler handler = new Handler();
    public Runnable run;
    private int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_status);
        orientation = this.getResources().getConfiguration().orientation;

        hideToolBr();
        initBtnClick();
        initialize();
    }

    public void onResume() {
        super.onResume();
        if(mCamera == null) {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
            mPreview.refreshCamera(mCamera);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    public void hideToolBr(){
        int newUiOptions = getWindow().getDecorView().getSystemUiVisibility();

        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void initialize() {
        if (checkCameraHardware(getApplicationContext())) {
            if (!checkPermissionFromDevice()) {
                startCamera();
            } else {
                requestPermission();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Dispositivo não tem suporte a Camera", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startCamera() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        mCamera =  Camera.open();
        checkCameraOrientation();
        cameraPreview = findViewById(R.id.cPreview);
        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        mCamera.startPreview();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    private void checkCameraOrientation(){
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT :
                mCamera.setDisplayOrientation(90);
                break;
            case Configuration.ORIENTATION_LANDSCAPE :
                mCamera.setDisplayOrientation(0);
                break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int camera_result =
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int record_audio_result =
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int write_external_storage_result =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read_external_storage_result =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        return camera_result == PackageManager.PERMISSION_DENIED ||
                write_external_storage_result == PackageManager.PERMISSION_DENIED ||
                record_audio_result == PackageManager.PERMISSION_DENIED ||
                read_external_storage_result == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, CAMERA_OPEN_CODE);
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void chooseCamera() {
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                mCamera = Camera.open(cameraId);
                checkCameraOrientation();
                mPreview.refreshCamera(mCamera);
            }
            mCamera.autoFocus(myAutoFocusCallback);
        }
        else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                mCamera = Camera.open(cameraId);
                checkCameraOrientation();
                mPreview.refreshCamera(mCamera);
            }
            mCamera.autoFocus(myAutoFocusCallback);
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success){
                binding.clickFocus.getBackground().setColorFilter(getResources().getColor(
                        android.R.color.holo_green_light), PorterDuff.Mode.SRC_ATOP);
            }
            else {
                binding.clickFocus.getBackground().setColorFilter(getResources().getColor(
                        android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
            }
            run = new Runnable() {
                @Override
                public void run(){
                    binding.clickFocus.setVisibility(View.GONE);
                }
            };
            handler.postDelayed(run, 1000);
        }
    };

    private Camera.PictureCallback getPictureCallback() {
        return new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (cameraFront){
                        bitmap = rotateImage(bitmap, 180);
                    }
                    bitmap = rotateImage(bitmap, 90);
                }
                startActivity(new Intent(StatusActivity.this, AddStatusActivity.class));
                recreate();
            }
        };
    }

    private void cameraAutoFocus(int x, int y){
        mCamera.autoFocus(myAutoFocusCallback);
        binding.clickFocus.setVisibility(View.VISIBLE);
        binding.clickFocus.setX(x);
        binding.clickFocus.setY(y);
        binding.clickFocus.getBackground().setColorFilter(getResources().getColor(
                android.R.color.white), PorterDuff.Mode.SRC_ATOP);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initBtnClick(){
        binding.btnPic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.btnPic.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
                binding.btnPic.setCardBackgroundColor(Color.RED);
                binding.layoutGallery.setVisibility(View.GONE);
                binding.layoutFlash.setVisibility(View.GONE);
                binding.layoutCameraInverter.setVisibility(View.GONE);

                binding.btnPic.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        binding.btnPicCircular.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_baseline_pause_24, null));
                        binding.btnPicCircular.setScaleType(ImageView.ScaleType.CENTER);
                        binding.textUserInfo.setVisibility(View.INVISIBLE);
                        binding.layoutChronometer.setVisibility(View.VISIBLE);
                        binding.btnChronometer.setBase(SystemClock.elapsedRealtime());
                        binding.btnChronometer.start();
                        pic_video = true;

                        return false;
                    }
                });

                binding.btnPic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(pic_video){
                            binding.btnPicCircular.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.ic_baseline_play_arrow_24, null));
                            binding.btnChronometer.stop();
                            recreate();
                            Toast.makeText(getApplicationContext(), "Indisponível no momento!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            mCamera.takePicture(null, null, mPicture);
                        }
                        binding.clickFocus.setVisibility(View.GONE);
                    }
                });

                return false;
            }
        });

        binding.layoutPrimary.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int x = (int) event.getX();
                final int y = (int) event.getY();

                cameraAutoFocus(x, y);
                binding.layoutGallery.setVisibility(View.GONE);

                return false;
            }
        });

        binding.layoutFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                Camera.Parameters p = mCamera.getParameters();
                switch (i){
                    case 0 :
                        binding.btnFlash.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_baseline_flash_off_24, null));
                        if (!cameraFront){
                            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        }
                        break;
                    case 1 :
                        binding.btnFlash.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_baseline_flash_auto_24, null));
                        if (!cameraFront){
                            p.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        }
                        break;
                    case 2 :
                        binding.btnFlash.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_baseline_flash_on_24, null));
                        if (!cameraFront){
                            p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        }
                        i = -1;
                        break;
                }
                mCamera.setParameters(p);
            }
        });

        binding.layoutCameraInverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_refresh);
                rotation.setRepeatCount(Animation.ABSOLUTE);
                binding.btnCameraInverter.startAnimation(rotation);
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    releaseCamera();
                    chooseCamera();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Dispositivo não possui camera frontal!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

    }

    private void openGallery() {
        Intent intent =  new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione a imagem"), IMAGE_GALLERY_REQUEST);
    }
}