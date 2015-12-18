package foodbazar.webmyne.com.foodbaazar.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.model.Extra;

/**
 * Created by sagartahelyani on 18-11-2015.
 */
public class ExtraItem extends LinearLayout {

    LayoutInflater inflater;
    View parentView;
    Context context;
    Extra extra;
    private TextView txtExtraItemName, txtExtraPrice;
    private CheckBox checkBox;

    public ExtraItem(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ExtraItem(Context context, Extra extra) {
        super(context);
        this.extra = extra;
        this.context = context;
        init();
    }

    private void init() {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = inflater.inflate(R.layout.extras_row, this);
        setOrientation(VERTICAL);

        findViewById();

        setDetails();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onCheckListener != null) {
                    onCheckListener.checked(extra.ExtraItemID, isChecked);
                }
            }
        });
    }

    private void setDetails() {
        txtExtraItemName.setText(extra.ExtraName);
        txtExtraPrice.setText(getResources().getString(R.string.Rs) + " " + extra.Price);
    }

    private void findViewById() {
        txtExtraItemName = (TextView) parentView.findViewById(R.id.txtExtraItemName);
        txtExtraPrice = (TextView) parentView.findViewById(R.id.txtExtraPrice);
        checkBox = (CheckBox) parentView.findViewById(R.id.checkBox);
    }

    public void setOnCheckListener(checkListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    checkListener onCheckListener;

    public interface checkListener {
        public void checked(int extraID, boolean isChecked);
    }
}
