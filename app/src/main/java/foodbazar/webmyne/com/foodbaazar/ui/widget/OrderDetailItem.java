package foodbazar.webmyne.com.foodbaazar.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.model.OrderItem;

/**
 * Created by sagartahelyani on 18-11-2015.
 */
public class OrderDetailItem extends LinearLayout {

    LayoutInflater inflater;
    View parentView;
    Context context;
    private TextView itemName, itemQty, itemPrice, itemExtra;
    OrderItem orderItem;

    public OrderDetailItem(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public OrderDetailItem(Context context, OrderItem orderItem) {
        super(context);
        this.context = context;
        this.orderItem = orderItem;
        init();
    }

    private void init() {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = inflater.inflate(R.layout.order_item, this);
        setOrientation(VERTICAL);

        findViewById();

        setDetails();

    }

    private void setDetails() {
        int extraPrice = 0;
        StringBuilder sb = new StringBuilder();
        itemName.setText(orderItem.ItemName + " (" + orderItem.Typename + ")");
        itemQty.setText(orderItem.Quantity + "");

        if (orderItem.Extras.size() > 0) {
            for (int i = 0; i < orderItem.Extras.size(); i++) {
                extraPrice = extraPrice + orderItem.Extras.get(i).Price;
            }

        }

        extraPrice = extraPrice * orderItem.Quantity;
        itemPrice.setText(getResources().getString(R.string.Rs) + " " + (orderItem.Price + extraPrice));

        if (orderItem.Extras.size() > 0) {
            itemExtra.setVisibility(VISIBLE);

            for (int i = 0; i < orderItem.Extras.size(); i++) {
                sb.append(orderItem.Extras.get(i).ExtraName + ", ");
            }
            //itemExtra.setText("Extras: " + sb.toString());
            String str = sb.toString().substring(0, sb.toString().length() - 2);
            itemExtra.setText("Extras: " + str);

        } else {
            itemExtra.setVisibility(GONE);
        }

    }

    private void findViewById() {
        itemName = (TextView) parentView.findViewById(R.id.itemName);
        itemQty = (TextView) parentView.findViewById(R.id.itemQty);
        itemPrice = (TextView) parentView.findViewById(R.id.itemPrice);
        itemExtra = (TextView) parentView.findViewById(R.id.itemExtra);
    }

}
