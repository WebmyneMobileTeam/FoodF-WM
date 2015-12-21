package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import foodbazar.webmyne.com.foodbaazar.EasyImage.EasyImage;
import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.custom.ChangePasswordDialog;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.helpers.ScalingUtilities;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.UserProfile;

public class UpdateProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText edtFirstName, edtLastName, edtEmailId, edtAddress, edtZip, edtMobile;
    private CircleImageView imgProfile;
    private Button btnUpdate, btnChangePassword;
    ComplexPreferences complexPreferences;
    UserProfile currentUser;
    View parentView;
    String selectImage = "";
    File mFolder;
    ProgressDialog pd;
    File f;
    Bitmap bmpProfile = null;
    boolean change = false;
    int userID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        init();
        EasyImage.configuration(this).setImagesFolderName("Test");

        complexPreferences = ComplexPreferences.getComplexPreferences(UpdateProfileActivity.this, "user_pref", 0);
        currentUser = new UserProfile();
        currentUser = complexPreferences.getObject("current-user", UserProfile.class);
        userID = currentUser.UserId;

        displayDetails();

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnImage();
            }
        });
    }

    public void btnImage() {

        mFolder = new File(Environment.getExternalStorageDirectory(), "FooddFAD");

        if (!mFolder.exists()) {
            mFolder.mkdirs();
        }

        final CharSequence[] items = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    EasyImage.openCamera(UpdateProfileActivity.this);
                } else if (items[item].equals("Choose from Gallery")) {
                    EasyImage.openGalleryPicker(UpdateProfileActivity.this);
                }
            }
        });
        builder.show();
    }


    @Override
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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(UpdateProfileActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
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
    }

    private void handleCrop(int resultCode, Intent result) {
        imgProfile.setImageURI(null);
        if (resultCode == RESULT_OK) {

            Bitmap bitmap = decodeFile(Crop.getOutput(result).getPath(), 640, 640);
            selectImage = Functions.returnBas64Image(bitmap);
            imgProfile.setImageBitmap(bitmap);

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {

        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            // Part 2: Scale image
            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            } else {
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            }

            // Store to tmp file
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
            String s = timeStamp + ".png";

            f = new File(mFolder.getAbsolutePath(), s);

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

        } catch (Throwable e) {

        }

        return scaledBitmap;

    }

    private void init() {
        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Update Profile");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ImageView imgCart = (ImageView) toolbar.findViewById(R.id.imgCartMenu);
        imgCart.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findView();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Functions.isBlank(edtFirstName)) {
                    Functions.snack(parentView, "First Name is required.");
                } else if (Functions.isBlank(edtLastName)) {
                    Functions.snack(parentView, "Last Name is required.");
                } else if (Functions.isBlank(edtMobile)) {
                    Functions.snack(parentView, "Mobile Number is required.");
                } else if (Functions.isBlank(edtEmailId)) {
                    Functions.snack(parentView, "Email id is required.");
                } else if (!Functions.emailValidation(Functions.toStr(edtEmailId))) {
                    Functions.snack(parentView, "Email id is invalid");
                } else {
                    doUpdate();
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog dialog = new ChangePasswordDialog(UpdateProfileActivity.this);
                dialog.setCustomListner(new ChangePasswordDialog.changePasswordListener() {
                    @Override
                    public void onSuccess() {
                        Functions.snack(parentView, "Change Password Successfully.");
                    }
                });
                dialog.show();
            }
        });
    }

    private void doUpdate() {
        pd = ProgressDialog.show(UpdateProfileActivity.this, "Loading.", "Please wait..", false);

        HashMap userObject = null;
        try {
            userObject = new HashMap();
            userObject.put("Address", Functions.toStr(edtAddress));
            userObject.put("City", 1);
            userObject.put("CityName", "Vadodara");
            userObject.put("Country", 1);
            userObject.put("DOB", currentUser.DOB);
            userObject.put("DeviceId", "");
            userObject.put("DeviceType", "Android");
            userObject.put("EmailId", Functions.toStr(edtEmailId));
            userObject.put("FirstName", Functions.toStr(edtFirstName));
            userObject.put("IsActive", true);
            userObject.put("IsDeleted", false);
            userObject.put("LastName", Functions.toStr(edtLastName));
            userObject.put("LoginType", 0);
            userObject.put("MobileNo", Functions.toStr(edtMobile));
            userObject.put("Password", "");
            userObject.put("ProfilePic", selectImage);
            userObject.put("ProfilePicFolderName", "");
            userObject.put("ResponseId", "");
            userObject.put("ResponseMsg", "");
            userObject.put("RoleId", 3);
            userObject.put("State", 1);
            userObject.put("StateName", "Gujarat");
            userObject.put("UpdateBy", 3);
            userObject.put("UserId", userID);
            userObject.put("Zip", Functions.toStr(edtZip));
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }

        Log.e("update_req", userObject.toString());

        IceNet.connect()
                .createRequest()
                .post(userObject)
                .pathUrl(AppConstants.SIGNUP)
                .fromJsonObject()
                .mappingInto(UserProfile.class)
                .execute("RequestUpdate", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {

                        pd.dismiss();
                        currentUser = (UserProfile) o;
                        if (currentUser.ResponseId == 1) {
                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(UpdateProfileActivity.this, "user_pref", 0);
                            complexPreferences.putObject("current-user", currentUser);
                            complexPreferences.commit();
                            Functions.snack(parentView, "Update Successfull");
                            finish();

                        } else {
                            Functions.snack(parentView, "Sign up error. " + currentUser.ResponseMsg);
                        }
                    }

                    @Override
                    public void onRequestError(RequestError error) {
                        Functions.snack(parentView, error.toString());
                        Log.e("error", error.toString());
                        pd.dismiss();
                    }
                });
    }

    private void displayDetails() {

        try {
            if (currentUser.ProfilePic != null) {
                final String profileURL = AppConstants.IMAGE_PREFIX + currentUser.ProfilePicFolderName + currentUser.ProfilePic;
                Log.e("profileURL", profileURL);

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        bmpProfile = Functions.getBitmap(profileURL);
                        if (bmpProfile != null) {
                            selectImage = Functions.returnBas64Image(bmpProfile);
                            //Log.e("selectImage", selectImage);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                    }
                }.execute();

                Glide.with(this).load(profileURL).thumbnail(0.10f).into(imgProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("err", e.getMessage());
        }
        edtFirstName.setText(currentUser.FirstName);
        edtLastName.setText(currentUser.LastName);
        edtEmailId.setText(currentUser.EmailId);
        if (currentUser.Address != null)
            edtAddress.setText(currentUser.Address);

        if (currentUser.Zip != null)
            edtZip.setText(currentUser.Zip);

        if (currentUser.MobileNo != null)
            edtMobile.setText(currentUser.MobileNo);
    }

    private void findView() {
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        imgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        edtEmailId = (EditText) findViewById(R.id.edtEmail);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtZip = (EditText) findViewById(R.id.edtZip);
        edtMobile = (EditText) findViewById(R.id.edtMobile);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
    }

}
