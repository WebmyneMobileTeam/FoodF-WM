package foodbazar.webmyne.com.foodbaazar.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;

import java.util.HashMap;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.UserProfile;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class LoginDialog extends BaseDialog {

    View parentView;
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    UserProfile profile;
    SharedPreferences pref;
    private ImageView imgClose;
    ProgressDialog pd;
    private TextView txtForgetPassword;

    public LoginDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.login_dialog, null);
        init(parentView);

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return parentView;
    }

    private void init(final View parentView) {
        imgClose = (ImageView) parentView.findViewById(R.id.imgClose);
        edtEmail = (EditText) parentView.findViewById(R.id.edtEmail);
        edtPassword = (EditText) parentView.findViewById(R.id.edtPassword);
        btnLogin = (Button) parentView.findViewById(R.id.btnLogin);
        txtForgetPassword = (TextView) parentView.findViewById(R.id.txtForgetPassword);

        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                final ForgetPasswordDialog dialog = new ForgetPasswordDialog(context);
                dialog.setCustomListner(new ForgetPasswordDialog.customButtonListener() {
                    @Override
                    public void onSuccess() {
                        Functions.snack(parentView, "Password sent successfully on your email-id.");
                    }
                });
                dialog.show();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProcess();
            }
        });

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
        pd = ProgressDialog.show(context, "Loading..", "Please wait..", false);
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
                            Functions.snack(parentView, "Login Successfull");
                            pref = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("isUserLogin", true);
                            editor.commit();

                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(context, "user_pref", 0);
                            complexPreferences.putObject("current-user", profile);
                            complexPreferences.commit();

                            if (customListner != null) {
                                customListner.onSuccess();
                            }
                            dismiss();

                        } else {
                            pd.dismiss();
                            Functions.snack(parentView, "Invalid login credentials");
                        }
                    }

                    @Override
                    public void onRequestError(RequestError error) {
                        Functions.snack(parentView, error.toString());
                    }
                });
    }

    @Override
    public boolean setUiBeforShow() {

        return false;
    }

    public void setCustomListner(changePasswordListener customListner) {
        this.customListner = customListner;
    }

    changePasswordListener customListner;

    public interface changePasswordListener {
        public void onSuccess();
    }

}
