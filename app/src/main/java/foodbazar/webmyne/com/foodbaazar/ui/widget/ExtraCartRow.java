package foodbazar.webmyne.com.foodbaazar.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.model.ExtraPojo;

/**
 * Created by sagartahelyani on 23-11-2015.
 */
public class ExtraCartRow extends LinearLayout {
    LayoutInflater inflater;
    View parentView;
    Context context;
    ExtraPojo pojo;
    private TextView txtExtraName, txtExtraPrice;

    public ExtraCartRow(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ExtraCartRow(Context context, ExtraPojo pojo) {
        super(context);
        this.context = context;
        this.pojo = pojo;
        init();
    }

    private void init() {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = inflater.inflate(R.layout.extra_cart_row, this);
        setOrientation(VERTICAL);

        findViewById();

        setDetails();

    }

    private void setDetails() {
        txtExtraName.setText(pojo.getExtraName());
        txtExtraPrice.setText(getResources().getString(R.string.Rs) + " " + pojo.getExtraPrice());

    }

    private void findViewById() {
        txtExtraName = (TextView) parentView.findViewById(R.id.txtExtraName);
        txtExtraPrice = (TextView) parentView.findViewById(R.id.txtExtraPrice);
    }
}
