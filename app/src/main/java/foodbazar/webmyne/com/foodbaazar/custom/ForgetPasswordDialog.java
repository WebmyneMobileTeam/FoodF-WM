package foodbazar.webmyne.com.foodbaazar.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.ForgetPassword;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class ForgetPasswordDialog extends BaseDialog {

    View parentView;
    private Button btnSubmit;
    private ImageView imgClose;
    private EditText edtEmailId;
    ForgetPassword forget;

    public ForgetPasswordDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.forget_password, null);
        init();

        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return parentView;
    }

    private void init() {
        imgClose = (ImageView) parentView.findViewById(R.id.imgClose);
        btnSubmit = (Button) parentView.findViewById(R.id.btnSubmit);
        edtEmailId = (EditText) parentView.findViewById(R.id.edtEmailId);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Functions.isBlank(edtEmailId)) {
                    Functions.snack(parentView, "Email Id is required");
                } else {
                    doForgetPassword();
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

    private void doForgetPassword() {
        final ProgressDialog pd = ProgressDialog.show(context, "Loading.", "Please wait..", false);
        IceNet.connect()
                .createRequest()
                .get()
                .pathUrl(AppConstants.FORGOT_PASSWORD + Functions.toStr(edtEmailId))
                .fromJsonObject()
                .mappingInto(ForgetPassword.class)
                .execute("RequestForget", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {
                        pd.dismiss();
                        forget = (ForgetPassword) o;
                        if (forget.ResponseCode.equals("1")) {
                            Functions.snack(parentView, "Password sent to your email-id.");
                            if (customListner != null) {
                                customListner.onSuccess();
                            }
                            dismiss();
                        } else {
                            Functions.snack(parentView, forget.ResponseMsg);
                        }
                    }

                    @Override
                    public void onRequestError(RequestError error) {
                        pd.dismiss();
                        Functions.snack(parentView, error.toString());
                    }
                });
    }

    @Override
    public boolean setUiBeforShow() {

        return false;
    }

    public void setCustomListner(customButtonListener customListner) {
        this.customListner = customListner;
    }

    customButtonListener customListner;

    public interface customButtonListener {
        public void onSuccess();
    }

}
