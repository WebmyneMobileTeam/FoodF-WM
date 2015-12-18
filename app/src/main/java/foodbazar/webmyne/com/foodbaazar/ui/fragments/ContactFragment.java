package foodbazar.webmyne.com.foodbaazar.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.HashMap;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.RatingResponse;


public class ContactFragment extends Fragment {

    View parentView;
    EditText edtName, edtEmail, edtEnquiry;
    RadioGroup radioGrp;
    RadioButton radioGeneral, radioBusiness;
    Button btnSubmit;
    ProgressDialog pd;
    boolean isGeneral = true;
    RatingResponse response;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_contact, container, false);
        init();
        return parentView;
    }

    private void init() {
        edtName = (EditText) parentView.findViewById(R.id.edtName);
        edtEmail = (EditText) parentView.findViewById(R.id.edtEmail);
        edtEnquiry = (EditText) parentView.findViewById(R.id.edtEnquiry);
        radioGrp = (RadioGroup) parentView.findViewById(R.id.radioGrp);
        radioGeneral = (RadioButton) parentView.findViewById(R.id.radioGeneral);
        radioBusiness = (RadioButton) parentView.findViewById(R.id.radioBusiness);
        btnSubmit = (Button) parentView.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Functions.toStr(edtName).length() == 0) {
                    Functions.snack(parentView, "Enter name");
                } else if (Functions.toStr(edtEmail).length() == 0) {
                    Functions.snack(parentView, "Enter email-id");
                } else if (!Functions.emailValidation(Functions.toStr(edtEmail))) {
                    Functions.snack(parentView, "Enter valid email-id");
                } else if (Functions.toStr(edtEnquiry).length() == 0) {
                    Functions.snack(parentView, "Enter enquiry");
                } else {

                    if (radioGrp.getCheckedRadioButtonId() == R.id.radioGeneral) {
                        isGeneral = true;
                    } else {
                        isGeneral = false;
                    }
                    
                    doEnquiry();
                }
            }
        });

    }

    private void doEnquiry() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(btnSubmit.getWindowToken(), 0);

        pd = ProgressDialog.show(getActivity(), "Loading.", "Please Wait..", false);

        HashMap userObject = null;
        try {
            userObject = new HashMap();
            userObject.put("EmailId", Functions.toStr(edtEmail));
            userObject.put("Message", Functions.toStr(edtEnquiry));
            userObject.put("Name", Functions.toStr(edtName));
            userObject.put("isGeneral", isGeneral);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("login_req", userObject.toString());

        IceNet.connect()
                .createRequest()
                .post(userObject)
                .pathUrl(AppConstants.CONTACT_US)
                .fromJsonObject()
                .mappingInto(RatingResponse.class)
                .execute("RequestLogin", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {

                        pd.dismiss();
                        response = (RatingResponse) o;

                        if (response.ResponseId.equals("1")) {
                            clearAll();
                            Functions.snack(parentView, "Send enquiry successfully.");
                        } else {
                            Functions.snack(parentView, "Error, Can't Sending Your Enquiry");
                        }
                    }

                    @Override
                    public void onRequestError(RequestError error) {
                        pd.dismiss();
                        Functions.snack(parentView, error.toString());
                    }
                });

    }

    private void clearAll() {
        edtName.setText("");
        edtEmail.setText("");
        edtEnquiry.setText("");
        radioGeneral.setChecked(true);
    }

}
