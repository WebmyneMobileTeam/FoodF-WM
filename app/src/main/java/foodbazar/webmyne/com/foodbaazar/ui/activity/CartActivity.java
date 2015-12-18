package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.DatabaseHandler;
import foodbazar.webmyne.com.foodbaazar.model.CartPojo;
import foodbazar.webmyne.com.foodbaazar.ui.widget.CartView;

public class CartActivity extends AppCompatActivity {

    Toolbar toolbar;
    private LinearLayout cartItemLayout, taxLayout;
    View parentView;
    DatabaseHandler handler;
    List<CartPojo> cartPojoList;
    private RelativeLayout contentLayout;
    int subTotal = 0;
    double total = 0.00;
    double cartTax = 0.00;
    double tax = 0.00;
    private TextView txtSubTotal, txtTaxPrice, txtTotal, txtTax;
    private TextView emptyCart;
    private Button btnCheckOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();
    }

    private void init() {
        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        ImageView imgCart = (ImageView) toolbar.findViewById(R.id.imgCartMenu);
        imgCart.setVisibility(View.GONE);

        if (toolbar != null) {
            toolbar.setTitle("My Cart");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        findViewById();

        fetchCartDetails();

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(CartActivity.this, ConfirmOrderActivity.class);
                i.putExtra("total", Math.round(total));
                i.putExtra("subTotal", subTotal);
                i.putExtra("tax", tax);
                startActivity(i);
            }
        });

    }

    private void fetchCartDetails() {

        cartItemLayout.removeAllViews();
        cartItemLayout.invalidate();
        cartPojoList = new ArrayList<>();
        subTotal = 0;
        total = 0.00;

        handler = new DatabaseHandler(this);
        cartPojoList = handler.getCartPojos();

        if (cartPojoList.size() == 0) {
            contentLayout.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);

        } else {
            contentLayout.setVisibility(View.VISIBLE);
            emptyCart.setVisibility(View.GONE);

            for (int i = 0; i < cartPojoList.size(); i++) {
                subTotal += cartPojoList.get(i).getTotalPrice();

                CartView cartView = new CartView(this, cartPojoList.get(i));
                cartView.setOnQtyListener(new CartView.onQtyListener() {
                    @Override
                    public void changeQuantity() {
                        fetchCartDetails();
                    }
                });
                cartItemLayout.addView(cartView);

            }
            cartTax = cartPojoList.get(0).getTax();

            txtSubTotal.setText(getResources().getString(R.string.Rs) + " " + subTotal);

            if (cartTax == 0.00) {
                taxLayout.setVisibility(View.GONE);
                total = subTotal;

            } else {
                taxLayout.setVisibility(View.VISIBLE);
                txtTax.setText("Service Tax (" + cartTax + "%)");

                tax = (subTotal * cartTax) / 100.00;
                txtTaxPrice.setText(getResources().getString(R.string.Rs) + " " + String.format("%.2f", tax));

                total = subTotal + tax;
            }

            txtTotal.setText(getResources().getString(R.string.Rs) + " " + Math.round(total));

        }

    }

    private void findViewById() {
        taxLayout = (LinearLayout) findViewById(R.id.taxLayout);
        cartItemLayout = (LinearLayout) findViewById(R.id.cartItemLayout);
        contentLayout = (RelativeLayout) findViewById(R.id.contentLayout);
        txtSubTotal = (TextView) findViewById(R.id.txtSubTotal);
        txtTaxPrice = (TextView) findViewById(R.id.txtTaxPrice);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        emptyCart = (TextView) findViewById(R.id.emptyCart);
        btnCheckOut = (Button) findViewById(R.id.btnCheckOut);
        txtTax = (TextView) findViewById(R.id.txtTax);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
