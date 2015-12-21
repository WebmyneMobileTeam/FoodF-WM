package foodbazar.webmyne.com.foodbaazar.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;

import java.util.HashMap;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.custom.AskDialog;
import foodbazar.webmyne.com.foodbaazar.custom.ForgetPasswordDialog;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.UserProfile;
import foodbazar.webmyne.com.foodbaazar.ui.activity.HomeScreen;
import foodbazar.webmyne.com.foodbaazar.ui.activity.SignUpActivity;
import foodbazar.webmyne.com.foodbaazar.ui.activity.UpdateProfileActivity;


public class LoginFragment extends Fragment implements View.OnClickListener {

    View parentView;
    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnLogout;
    private TextView txtSignUp, txtForgetPassword;
    UserProfile profile;
    SharedPreferences pref;
    private LinearLayout loginLayout, profileLayout;
    ProgressDialog pd;

    // Profile
    CircleImageView imgProfile;
    TextView txtName, txtEmail, txtAddress, txtZip, txtMobile;
    Button btnEdit;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_login, container, false);
        init();

        return parentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        pref = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        if (pref.contains("isUserLogin")) {
            loginLayout.setVisibility(View.GONE);
            profileLayout.setVisibility(View.VISIBLE);
            displayProfile();
            setHasOptionsMenu(true);

        } else {
            profileLayout.setVisibility(View.GONE);
            setHasOptionsMenu(false);
            loginLayout.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        findView();
        HomeScreen homeScreen = ((HomeScreen) getActivity());
        homeScreen.setTitle("Login or Register");
        homeScreen.setSubTitle("");

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(getActivity(), UpdateProfileActivity.class);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AskDialog askDialog = new AskDialog(getActivity(), "Are you sure want to logout?");
                askDialog.setOnButtonsEventListener(new AskDialog.OnButtonEventListener() {
                    @Override
                    public void clickYes() {
                        closeSession();
                    }

                    @Override
                    public void clickNo() {

                    }
                });
                askDialog.show();
            }
        });

    }

    private void findView() {
        btnLogout = (Button) parentView.findViewById(R.id.btnLogout);
        imgProfile = (CircleImageView) parentView.findViewById(R.id.imgProfile);
        txtName = (TextView) parentView.findViewById(R.id.txtName);
        txtEmail = (TextView) parentView.findViewById(R.id.txtEmail);
        txtAddress = (TextView) parentView.findViewById(R.id.txtAddress);
        txtZip = (TextView) parentView.findViewById(R.id.txtZip);
        txtMobile = (TextView) parentView.findViewById(R.id.txtMobile);
        btnEdit = (Button) parentView.findViewById(R.id.btnEdit);

        profileLayout = (LinearLayout) parentView.findViewById(R.id.profileLayout);
        loginLayout = (LinearLayout) parentView.findViewById(R.id.loginLayout);
        edtEmail = (EditText) parentView.findViewById(R.id.edtEmail);
        edtPassword = (EditText) parentView.findViewById(R.id.edtPassword);
        btnLogin = (Button) parentView.findViewById(R.id.btnLogin);
        txtSignUp = (TextView) parentView.findViewById(R.id.txtSignUp);
        txtForgetPassword = (TextView) parentView.findViewById(R.id.txtForgetPassword);

        btnLogin.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
        txtForgetPassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                loginProcess();
                break;

            case R.id.txtSignUp:
                Functions.fireIntent(getActivity(), SignUpActivity.class);
                break;

            case R.id.txtForgetPassword:
                final ForgetPasswordDialog dialog = new ForgetPasswordDialog(getActivity());
                dialog.setCustomListner(new ForgetPasswordDialog.customButtonListener() {
                    @Override
                    public void onSuccess() {
                        Functions.snack(parentView, "Password sent successfully on your email-id.");
                    }
                });
                dialog.show();
                break;

        }
    }

    private void loginProcess() {
        if (Functions.isBlank(edtEmail)) {
            Functions.snack(parentView, "Email id is required");

        } else if (Functions.isBlank(edtPassword)) {
            Functions.snack(parentView, "Password is required");

        } else if (!Functions.emailValidation(Functions.toStr(edtEmail))) {
            Functions.snack(parentView, "Enter valid email id");

        } else {
            doLogin();
        }
    }

    private void doLogin() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(btnLogin.getWindowToken(), 0);

        pd = ProgressDialog.show(getActivity(), "Loading.", "Please Wait..", false);
        HashMap userObject = null;
        try {
            userObject = new HashMap();
            userObject.put("EmailAddress", Functions.toStr(edtEmail));
            userObject.put("Password", Functions.toStr(edtPassword));
            userObject.put("Roleid", 3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("login_req", userObject.toString());

        IceNet.connect()
                .createRequest()
                .post(userObject)
                .pathUrl(AppConstants.LOGIN)
                .fromJsonObject()
                .mappingInto(UserProfile.class)
                .execute("RequestLogin", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {

                        pd.dismiss();
                        profile = (UserProfile) o;

                        if (profile.ResponseId == 1) {
                            loginLayout.setVisibility(View.GONE);
                            Functions.snack(parentView, "Login Successfull");
                            pref = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("isUserLogin", true);
                            editor.commit();

                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                            complexPreferences.putObject("current-user", profile);
                            complexPreferences.commit();

                            onResume();

                            HomeScreen home = (HomeScreen) getActivity();
                            home.setLogoutVisibility();

                        } else {
                            Functions.snack(parentView, "Invalid login credentials");
                        }
                    }

                    @Override
                    public void onRequestError(RequestError error) {
                        pd.dismiss();
                        Functions.snack(parentView, error.toString());
                    }
                });
    }

    private void closeSession() {
        ComplexPreferences complexPreferences;
        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        UserProfile blankUser = new UserProfile();
        complexPreferences.putObject("current-user", blankUser);
        complexPreferences.commit();

        SharedPreferences preferences2 = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences2.edit();
        editor2.remove("isUserLogin");
        editor2.commit();

        HomeScreen homeScreen = ((HomeScreen) getActivity());
        homeScreen.setTitle("Login");
        homeScreen.setSubTitle("");
        homeScreen.setLogoutVisibility();

        onResume();
    }

    private void displayProfile() {
        HomeScreen homeScreen = ((HomeScreen) getActivity());
        homeScreen.setTitle("Account Details");
        homeScreen.setSubTitle("");

        profileLayout.setVisibility(View.VISIBLE);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        UserProfile currentUser = new UserProfile();
        currentUser = complexPreferences.getObject("current-user", UserProfile.class);

        Log.e("UserId", currentUser.UserId + "");

        if (currentUser.ProfilePic == null) {

        } else {
            Log.e("User_pic", AppConstants.IMAGE_PREFIX + currentUser.ProfilePicFolderName + currentUser.ProfilePic);
            Glide.with(getActivity()).load(AppConstants.IMAGE_PREFIX + currentUser.ProfilePicFolderName + currentUser.ProfilePic).thumbnail(0.10f).into(imgProfile);
        }

        txtName.setText(currentUser.FirstName + " " + currentUser.LastName);
        txtEmail.setText(currentUser.EmailId);
        if (currentUser.Address != null)
            txtAddress.setText(currentUser.Address);

        if (currentUser.Zip != null)
            txtZip.setText(currentUser.Zip);

        if (currentUser.MobileNo != null)
            txtMobile.setText(currentUser.MobileNo);
    }
}
