package foodbazar.webmyne.com.foodbaazar.custom;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.dialog.widget.base.BaseDialog;

import foodbazar.webmyne.com.foodbaazar.R;

/**
 * Created by sagartahelyani on 24-09-2015.
 */
public class AskDialog extends BaseDialog {

    View parentView;
    Button btnYes, btnNo;
    OnButtonEventListener onButtonsClick;
    String message;
    TextView txtMessage;
    ImageView imgClose;

    public void setOnButtonsEventListener(OnButtonEventListener onButtonsClick) {
        this.onButtonsClick = onButtonsClick;
    }

    public AskDialog(Context context, String message) {
        super(context);
        this.message = message;
    }

    @Override
    public View onCreateView() {
        widthScale(0.9f);
        showAnim(new FadeEnter());

        parentView = View.inflate(context, R.layout.ask_dialog, null);
        init(parentView);

        return parentView;
    }

    private void init(final View parentView) {
        imgClose = (ImageView) parentView.findViewById(R.id.imgClose);
        txtMessage = (TextView) parentView.findViewById(R.id.txtMessage);
        btnYes = (Button) parentView.findViewById(R.id.btnYes);
        btnNo = (Button) parentView.findViewById(R.id.btnNo);

        txtMessage.setText(message);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onButtonsClick != null) {
                    onButtonsClick.clickYes();
                }
                dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onButtonsClick != null) {
                    onButtonsClick.clickNo();
                }
                dismiss();
            }
        });

    }

    @Override
    public boolean setUiBeforShow() {
        setCanceledOnTouchOutside(false);
        return false;
    }

    public interface OnButtonEventListener {
        void clickYes();

        void clickNo();
    }

}
