package foodbazar.webmyne.com.foodbaazar.ui.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.custom.AskDialog;
import foodbazar.webmyne.com.foodbaazar.custom.QuantityView;
import foodbazar.webmyne.com.foodbaazar.helpers.DatabaseHandler;
import foodbazar.webmyne.com.foodbaazar.model.CartPojo;

/**
 * Created by sagartahelyani on 19-11-2015.
 */
public class CartView extends LinearLayout {

    LayoutInflater inflater;
    View parentView;
    Context context;
    private LinearLayout extrasLayout;
    private ImageView imgDelete;
    private CircleImageView imgProduct;
    private TextView txtItemName, txtSubTotal;
    private QuantityView quantityView;
    CartPojo cartPojo;
    DatabaseHandler handler;

    public CartView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CartView(Context context, CartPojo cartPojo) {
        super(context);
        this.context = context;
        this.cartPojo = cartPojo;
        init();
    }

    private void init() {
        handler = new DatabaseHandler(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = inflater.inflate(R.layout.cart_row, this);
        setOrientation(VERTICAL);

        findViewById();

        imgDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final AskDialog askDialog = new AskDialog(context, "Are you sure want to remove this item from cart?");
                askDialog.setOnButtonsEventListener(new AskDialog.OnButtonEventListener() {
                    @Override
                    public void clickYes() {
                        handler.removeItem(cartPojo.getUuId());
                        handler.close();
                        if (onQtyListener != null) {
                            onQtyListener.changeQuantity();
                        }

                    }

                    @Override
                    public void clickNo() {
                        askDialog.dismiss();
                    }
                });
                askDialog.show();
            }
        });

        quantityView.setOnQuantityChangeListener(new QuantityView.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged(int newQuantity, boolean programmatically) {
                Log.e("newQuantity", newQuantity + "");

                handler.updateQuantity(newQuantity, cartPojo.getUuId(), cartPojo.getPriceId());
                handler.close();

                if (onQtyListener != null) {
                    onQtyListener.changeQuantity();
                }
            }

            @Override
            public void onLimitReached() {

            }
        });

        setCartItems();

    }

    private void setCartItems() {
        extrasLayout.removeAllViews();
        extrasLayout.invalidate();

        txtItemName.setText(cartPojo.getItemName() + " (" + cartPojo.getItemTypeName() + ")");
        quantityView.setQuantity(cartPojo.getQuantity());
        txtSubTotal.setText(getResources().getString(R.string.Rs) + " " + cartPojo.getTotalPrice());

        // Log.e("image", cartPojo.getImgPath());
        if (cartPojo.getImgPath() == null || cartPojo.getImgPath().equals("") || cartPojo.getImgPath().contains("No-Image.jpg")) {
            Glide.with(context).load(R.drawable.bg_image_small).into(imgProduct);

        } else {
            Glide.with(context).load(cartPojo.getImgPath()).into(imgProduct);

        }
//        Log.e("extra size", cartPojo.getExtraPojos().size() + "");

        if (cartPojo.getExtraPojos().size() > 0) {
            for (int i = 0; i < cartPojo.getExtraPojos().size(); i++) {
                ExtraCartRow cartRow = new ExtraCartRow(context, cartPojo.getExtraPojos().get(i));
                extrasLayout.addView(cartRow);
            }
        }

    }

    private void findViewById() {
        extrasLayout = (LinearLayout) parentView.findViewById(R.id.extrasLayout);
        txtItemName = (TextView) parentView.findViewById(R.id.txtItemName);
        imgDelete = (ImageView) parentView.findViewById(R.id.imgDelete);
        quantityView = (QuantityView) parentView.findViewById(R.id.quantityView);
        txtSubTotal = (TextView) parentView.findViewById(R.id.txtSubTotal);
        imgProduct = (CircleImageView) parentView.findViewById(R.id.imgProduct);
    }

    public void setOnQtyListener(CartView.onQtyListener onQtyListener) {
        this.onQtyListener = onQtyListener;
    }

    onQtyListener onQtyListener;

    public interface onQtyListener {
        public void changeQuantity();
    }
}
