package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.HashMap;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.UserProfile;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private EditText edtFirstName, edtLastName, edtMobile, edtEmailId, edtPassword, edtConfirmPassword;
    private Button btnSignUp;
    private View parentView;
    UserProfile profile;
    SharedPreferences pref;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Sign Up");
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
    }

    private void findView() {
        parentView = findViewById(android.R.id.content);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtMobile = (EditText) findViewById(R.id.edtMobile);
        edtEmailId = (EditText) findViewById(R.id.edtEmailId);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                signupProcess();
                break;
        }
    }

    private void signupProcess() {
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
        } else if (Functions.isBlank(edtPassword)) {
            Functions.snack(parentView, "Password is required.");
        } else if (!Functions.toStr(edtPassword).equals(Functions.toStr(edtConfirmPassword))) {
            Functions.snack(parentView, "Password and Confirm Password must be same.");
        } else {
            doSignUp();
        }
    }

    private void doSignUp() {
        pd = ProgressDialog.show(SignUpActivity.this, "Loading.", "Please wait..", false);
        HashMap userObject = null;
        try {
            userObject = new HashMap();
            userObject.put("Address", "");
            userObject.put("City", 1);
            userObject.put("CityName", "Vadodara");
            userObject.put("Country", 1);
            userObject.put("DOB", "06-11-2015");
            userObject.put("DeviceId", "");
            userObject.put("DeviceType", "Android");
            userObject.put("EmailId", Functions.toStr(edtEmailId));
            userObject.put("FirstName", Functions.toStr(edtFirstName));
            userObject.put("IsActive", true);
            userObject.put("IsDeleted", false);
            userObject.put("LastName", Functions.toStr(edtLastName));
            userObject.put("LoginType", 0);
            userObject.put("MobileNo", Functions.toStr(edtMobile));
            userObject.put("Password", Functions.toStr(edtPassword));
            userObject.put("ProfilePic", "");
            userObject.put("ProfilePicFolderName", "");
            userObject.put("ResponseId", "");
            userObject.put("ResponseMsg", "");
            userObject.put("RoleId", 3);
            userObject.put("State", 1);
            userObject.put("StateName", "Gujarat");
            userObject.put("UpdateBy", 3);
            userObject.put("UserId", 0);
            userObject.put("Zip", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("signup_req", userObject.toString());

        IceNet.connect()
                .createRequest()
                .post(userObject)
                .pathUrl(AppConstants.SIGNUP)
                .fromJsonObject()
                .mappingInto(UserProfile.class)
                .execute("RequestSignUp", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {

                        pd.dismiss();

                        profile = (UserProfile) o;
                        if (profile.ResponseId == 1) {
                            Log.e("profile.ResponseMsg", profile.ResponseMsg);

                            Functions.snack(parentView, "User Created Successfully.");
                            pref = getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("isUserLogin", true);
                            editor.commit();

                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(SignUpActivity.this, "user_pref", 0);
                            complexPreferences.putObject("current-user", profile);
                            complexPreferences.commit();
                            finish();

                        } else {
                            Functions.snack(parentView, "Sign up error. " + profile.ResponseMsg);
                        }
                    }

                    @Override
                    public void onRequestError(RequestError error) {
                        pd.dismiss();
                        Functions.snack(parentView, error.toString());
                    }
                });
    }
}
