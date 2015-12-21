package foodbazar.webmyne.com.foodbaazar.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import foodbazar.webmyne.com.foodbaazar.EasyImage.EasyImage;
import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ScalingUtilities;

public class ProfilePicturePickerActivity extends AppCompatActivity {

    ProfileImage profileImage;
    CircleImageView imageView;
    File mFolder;
    Bitmap bitmap;
    String path = null;
    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_picker);

        imageView = (CircleImageView) findViewById(R.id.imageView);
        EasyImage.configuration(this).setImagesFolderName("Foodfad");

        String extr = Environment.getExternalStorageDirectory().toString();
        mFolder = new File(extr + "/Foodfad/Profile Pictures");
        if (!mFolder.exists()) {
            mFolder.mkdirs();
        }

        profileImage = new ProfileImage();

        showDialog();

    }

    public void btnImage(View view) {

        //showDialog();

    }

    public class ProfileImage implements Serializable {

        //public Bitmap bitmap;
        public String Base64String;

    }
    private void showDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    EasyImage.openCamera(ProfilePicturePickerActivity.this);
                } else if (items[item].equals("Choose from Gallery")) {
                    EasyImage.openGalleryPicker(ProfilePicturePickerActivity.this);
                }
            }
        });


        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();

            }
        });


        builder.show();
    }

    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
                //Handle the image
                onPhotoReturned(imageFile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (imageSource == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(ProfilePicturePickerActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
                finish();
            }
        });

        if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);

        }
    }

    private void onPhotoReturned(File imageFile) {
        beginCrop(imageFile);
    }


    private void beginCrop(File file) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(Uri.fromFile(file), destination).asSquare().start(this);

        // Toast.makeText(this, String.valueOf((int) file.length() / 1024d), Toast.LENGTH_SHORT).show();
    }


    private void handleCrop(int resultCode, Intent result) {
        imageView.setImageURI(null);
        if (resultCode == RESULT_OK) {
            bitmap = decodeFile(Crop.getOutput(result).getPath(), 640, 640);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_LONG).show();
        } else if (resultCode == 0) {
            finish();
        }
        if (bitmap != null) {
            final Intent intent = new Intent();
            intent.putExtra("path", path);
            setResult(AppConstants.ProfileImageRes, intent);
            finish();
        }

    }

    private Bitmap decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            }
            // Store to tmp file
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
            String s = timeStamp + ".png";

            File f = new File(mFolder.getAbsolutePath(), s);
            this.path = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }
            imageView.setImageBitmap(scaledBitmap);

        } catch (Throwable e) {
        }

        return scaledBitmap;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
