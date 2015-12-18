package foodbazar.webmyne.com.foodbaazar.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import foodbazar.webmyne.com.foodbaazar.model.ChangePassword;
import foodbazar.webmyne.com.foodbaazar.model.UserProfile;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class ChangePasswordDialog extends BaseDialog {

    View parentView;
    Button btnSubmit;
    private EditText edtOldPassword, edtNewPassword;
    ProgressDialog pd;
    private int userId;
    ComplexPreferences complexPreferences;
    String password, passwordError, msg;
    ImageView imgClose;

    public ChangePasswordDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.change_password, null);
        init(parentView);

        UserProfile userProfile = new UserProfile();
        complexPreferences = ComplexPreferences.getComplexPreferences(context, "user_pref", 0);
        userProfile = complexPreferences.getObject("current-user", UserProfile.class);
        userId = userProfile.UserId;

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return parentView;
    }

    private void init(final View parentView) {
        imgClose = (ImageView) parentView.findViewById(R.id.imgClose);
        btnSubmit = (Button) parentView.findViewById(R.id.btnSubmit);
        edtOldPassword = (EditText) parentView.findViewById(R.id.edtOldPassword);
        edtNewPassword = (EditText) parentView.findViewById(R.id.edtNewPassword);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Functions.isBlank(edtOldPassword)) {
                    Functions.snack(parentView, "Old Password is required");
                } else if (Functions.isBlank(edtNewPassword)) {
                    Functions.snack(parentView, "New Password is required");
                } else {
                    doChangePassword();
                }
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void doChangePassword() {
        HashMap userObject = null;
        try {
            userObject = new HashMap();
            userObject.put("NewPass", Functions.toStr(edtNewPassword));
            userObject.put("OldPass", Functions.toStr(edtOldPassword));
            userObject.put("UserId", userId);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }

        Log.e("change_pwd_req", userObject.toString());

        IceNet.connect()
                .createRequest()
                .post(userObject)
                .pathUrl(AppConstants.CHANGE_PASSWORD)
                .fromJsonObject()
                .mappingInto(ChangePassword.class)
                .execute("RequestUpdate", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {
                        ChangePassword password = (ChangePassword) o;
                        if (password.ResponseId.equals("1")) {
                            if (customListner != null) {
                                customListner.onSuccess();
                            }
                            dismiss();
                        } else {
                            Functions.snack(parentView, password.ResponseMsg);
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
