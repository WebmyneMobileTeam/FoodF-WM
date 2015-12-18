package foodbazar.webmyne.com.foodbaazar.custom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;

import java.util.HashMap;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.RatingResponse;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class AddRatingDialog extends BaseDialog {

    View parentView;
    Button btnSubmit;
    ImageView imgClose;
    OnButtonEventListener onButtonsClick;
    RatingBar ratingBar;
    EditText edtReview;
    ProgressDialog pd;
    int hotelId;
    String orderId;
    RatingResponse response;

    public void setOnButtonsEventListener(OnButtonEventListener onButtonsClick) {
        this.onButtonsClick = onButtonsClick;
    }

    public AddRatingDialog(Context context, int hotelId, String orderId) {
        super(context);
        this.context = context;
        this.hotelId = hotelId;
        this.orderId = orderId;
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.add_rating, null);
        init(parentView);

        return parentView;
    }

    private void init(final View parentView) {
        btnSubmit = (Button) parentView.findViewById(R.id.btnSubmit);
        ratingBar = (RatingBar) parentView.findViewById(R.id.ratingBar);
        edtReview = (EditText) parentView.findViewById(R.id.edtReview);
        imgClose = (ImageView) parentView.findViewById(R.id.imgClose);

        Drawable progress = ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, context.getResources().getColor(R.color.button_bg));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ratingBar.getRating() == 0.0) {
                    Functions.snack(parentView, "Rating must be given");
                } else {
                    doRating();
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

    private void doRating() {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(btnSubmit.getWindowToken(), 0);

        pd = ProgressDialog.show(context, "Loading.", "Please Wait..", false);
        HashMap userObject = null;
        try {
            userObject = new HashMap();
            userObject.put("OrderID", orderId);
            userObject.put("Rating", (int) ratingBar.getRating());
            userObject.put("RestaurantID", hotelId);
            userObject.put("Review", Functions.toStr(edtReview));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("add_rev_req", userObject.toString());

        IceNet.connect()
                .createRequest()
                .post(userObject)
                .pathUrl(AppConstants.GIVE_RATING)
                .fromJsonObject()
                .mappingInto(RatingResponse.class)
                .execute("RequestLogin", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {

                        pd.dismiss();
                        response = (RatingResponse) o;

                        if (response.ResponseId.equals("1")) {
                            if (onButtonsClick != null) {
                                Functions.snack(parentView, "Thank you for share your review.");
                                onButtonsClick.onSubmit();
                            }
                            dismiss();
                        } else {
                            Functions.snack(parentView, "Error " + response.ResponseMsg);
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
        setCanceledOnTouchOutside(false);
        return false;
    }

    public interface OnButtonEventListener {

        void onSubmit();
    }

}
