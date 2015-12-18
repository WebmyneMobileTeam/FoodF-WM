package foodbazar.webmyne.com.foodbaazar.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.custom.AskDialog;
import foodbazar.webmyne.com.foodbaazar.custom.QuantityView;
import foodbazar.webmyne.com.foodbaazar.helpers.DatabaseHandler;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.model.MenuTypeItemstrans;

/**
 * Created by sagartahelyani on 18-11-2015.
 */
public class PricingRow extends LinearLayout {

    LayoutInflater inflater;
    View parentView;
    Context context;
    private TextView txtType, txtPrice;
    private ImageView imgAdd;
    private MenuTypeItemstrans menuTypeItemstrans;
    private QuantityView quantityView;
    private LinearLayout extrasLayout, addExtras;
    private int RestaurantID;
    private double tax;
    private String itemName;
    private ArrayList<Integer> extraIDs;
    DatabaseHandler handler;
    String imgPath;
    addCartListener onAddCartListener;

    public void setOnAddCartListener(addCartListener onAddCartListener) {
        this.onAddCartListener = onAddCartListener;
    }

    public PricingRow(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public PricingRow(Context context, MenuTypeItemstrans menuTypeItemstrans, int RestaurantID, String itemName, double tax, String imgPath) {
        super(context);
        this.context = context;
        this.menuTypeItemstrans = menuTypeItemstrans;
        this.RestaurantID = RestaurantID;
        this.itemName = itemName;
        this.tax = tax;
        this.imgPath = imgPath;
        init();
    }

    private void init() {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parentView = inflater.inflate(R.layout.pricing_row, this);
        setOrientation(VERTICAL);

        findViewById();

        handler = new DatabaseHandler(context);
    }

    private void findViewById() {
        addExtras = (LinearLayout) parentView.findViewById(R.id.addExtras);
        extrasLayout = (LinearLayout) parentView.findViewById(R.id.extrasLayout);
        quantityView = (QuantityView) parentView.findViewById(R.id.quantityView);
        txtType = (TextView) parentView.findViewById(R.id.txtType);
        txtPrice = (TextView) parentView.findViewById(R.id.txtPrice);
        imgAdd = (ImageView) parentView.findViewById(R.id.imgAdd);
        imgAdd.bringToFront();

        setItemDetails();

        imgAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean canAdd = handler.checkIfAdded(RestaurantID);

                if (canAdd) {
                    if (quantityView.getQuantity() != 0) {

                        if (handler.isExists(menuTypeItemstrans.PriceID)) {

                            if (extraIDs.size() == 0) {

                                handler.update(menuTypeItemstrans.PriceID, quantityView.getQuantity(), menuTypeItemstrans.Price);
                                handler.close();
                                quantityView.setQuantity(1);

                                Functions.snack(parentView, "Product added to cart");

                            } else {
                                ArrayList<Integer> unique = new ArrayList<>();
                                unique.add(menuTypeItemstrans.PriceID);
                                for (int i = 0; i < extraIDs.size(); i++) {
                                    unique.add(extraIDs.get(i));
                                }

                                Collections.sort(unique);
                                String newUnique = Functions.concatValues(unique);

                                String oldUnique = handler.getUniqueId(menuTypeItemstrans.PriceID);

                                if (newUnique.equals(oldUnique)) {
                                    handler.updateWithExtra(menuTypeItemstrans, quantityView.getQuantity(), extraIDs, oldUnique);
                                    handler.close();
                                    quantityView.setQuantity(1);

                                } else {
                                    handler.addCartProduct(menuTypeItemstrans, quantityView.getQuantity(), RestaurantID, itemName, extraIDs, tax, imgPath);
                                    handler.close();
                                    quantityView.setQuantity(1);
                                }
                                Functions.snack(parentView, "Product added to cart");
                            }

                        } else {

                            if (extraIDs.size() > 0) {
                                // Log.e("extras", extraIDs.toString());
                            }

                            handler.addCartProduct(menuTypeItemstrans, quantityView.getQuantity(), RestaurantID, itemName, extraIDs, tax, imgPath);
                            handler.close();
                            quantityView.setQuantity(1);

                            Functions.snack(parentView, "Product added to cart");
                        }

                    }
                    if (onAddCartListener != null) {
                        onAddCartListener.onAdd();
                    }

                } else {

                    final AskDialog askDialog = new AskDialog(context, "If you add item from this restaurant, previous restaurant items from cart will be lost.");
                    askDialog.setOnButtonsEventListener(new AskDialog.OnButtonEventListener() {
                        @Override
                        public void clickYes() {
                            handler.deleteCart();
                            Functions.snack(parentView, "Product added to cart");
                            handler.addCartProduct(menuTypeItemstrans, quantityView.getQuantity(), RestaurantID, itemName, extraIDs, tax, imgPath);
                            handler.close();
                            quantityView.setQuantity(1);
                        }

                        @Override
                        public void clickNo() {
                            askDialog.dismiss();
                        }
                    });
                    askDialog.show();

                }

            }
        });
    }

    public interface addCartListener {
        void onAdd();
    }

    private void setItemDetails() {
        addExtras.removeAllViews();
        addExtras.invalidate();
        extraIDs = new ArrayList<>();

        txtType.setText(menuTypeItemstrans.TypeName);
        txtPrice.setText(getResources().getString(R.string.Rs) + " " + menuTypeItemstrans.Price);

        if (menuTypeItemstrans.Extras.size() > 0) {
            extrasLayout.setVisibility(VISIBLE);

            for (int j = 0; j < menuTypeItemstrans.Extras.size(); j++) {
                ExtraItem extraItem = new ExtraItem(context, menuTypeItemstrans.Extras.get(j));
                extraItem.setOnCheckListener(new ExtraItem.checkListener() {
                    @Override
                    public void checked(int extraID, boolean isChecked) {
                        if (isChecked) {
                            extraIDs.add(extraID);
                        } else {
                            int index = 0;
                            for (int i = 0; i < extraIDs.size(); i++) {
                                if (extraIDs.get(i) == extraID) {
                                    index = i;
                                }
                            }
                            extraIDs.remove(index);
                        }
                    }

                });
                addExtras.addView(extraItem);
            }

        } else {
            extrasLayout.setVisibility(GONE);
        }
    }

}
