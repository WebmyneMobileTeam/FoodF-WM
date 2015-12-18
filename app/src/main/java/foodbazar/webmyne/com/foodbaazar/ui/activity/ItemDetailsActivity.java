package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.helpers.ToolHelper;
import foodbazar.webmyne.com.foodbaazar.model.MenuItemNew;
import foodbazar.webmyne.com.foodbaazar.ui.widget.PricingRow;

public class ItemDetailsActivity extends AppCompatActivity {

    MenuItemNew itemDetails;
    private Toolbar toolbar;
    private View parentView;
    private TextView txtItemCategory, txtRestaurant, txtTagLine;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView backdrop, imgVegNonveg, imgCartMenu;
    private LinearLayout pricingLayout;
    private double serviceTax;
    ToolHelper helper;
    boolean home, pickup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        itemDetails = new MenuItemNew();
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        itemDetails = complexPreferences.getObject("item", MenuItemNew.class);

        serviceTax = getIntent().getDoubleExtra("serviceTax", 0.0);
        home = getIntent().getBooleanExtra("home", false);
        pickup = getIntent().getBooleanExtra("pickup", false);
        init();

    }

    private void init() {
        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(itemDetails.ItemName);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        helper = new ToolHelper(ItemDetailsActivity.this, toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findView();

        loadItemDetails();

        imgCartMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(ItemDetailsActivity.this, CartActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void loadItemDetails() {
        pricingLayout.removeAllViews();
        pricingLayout.invalidate();

        collapsingToolbar.setTitle(itemDetails.ItemName);
        if (!itemDetails.MenuIcon.equals("No-Image.jpg")) {
            Glide.with(this).load(AppConstants.IMAGE_PREFIX + itemDetails.MenuIconFolderPath + itemDetails.MenuIcon).thumbnail(0.10f).into(backdrop);
        }

        if (itemDetails.VegNonveg == 0) { // not defined
            imgVegNonveg.setVisibility(View.GONE);

        } else {
            imgVegNonveg.setVisibility(View.VISIBLE);

            if (itemDetails.VegNonveg == 1) { // veg
                imgVegNonveg.setImageResource(R.drawable.veg_revised);

            } else if (itemDetails.VegNonveg == 2) { // non-veg
                imgVegNonveg.setImageResource(R.drawable.nonveg_revised);
            }
        }
        if (itemDetails.CateGoryName == null) {
            txtItemCategory.setText("Combo Offer,");
        } else {
            txtItemCategory.setText(itemDetails.CateGoryName + ",");
        }
        txtRestaurant.setText(itemDetails.RestaurantName);

        for (int i = 0; i < itemDetails.lstMenuTypeItemstrans.size(); i++) {
            String image = AppConstants.IMAGE_PREFIX + itemDetails.MenuIconFolderPath + itemDetails.MenuIcon;
            PricingRow row = new PricingRow(this, itemDetails.lstMenuTypeItemstrans.get(i), itemDetails.RestaurantID, itemDetails.ItemName, serviceTax, image);
            row.setOnAddCartListener(new PricingRow.addCartListener() {
                @Override
                public void onAdd() {
                    helper.displayBadge();

                    SharedPreferences pref = getSharedPreferences("delivery", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("home", home);
                    editor.putBoolean("pickup", pickup);
                    editor.commit();
                }
            });
            pricingLayout.addView(row);
        }

        txtTagLine.setText(itemDetails.TagLine);

    }

    private void findView() {
        imgCartMenu = (ImageView) findViewById(R.id.imgCartMenu);
        pricingLayout = (LinearLayout) findViewById(R.id.pricingLayout);
        imgVegNonveg = (ImageView) findViewById(R.id.imgVegNonveg);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        backdrop = (ImageView) findViewById(R.id.backdrop);
        txtItemCategory = (TextView) findViewById(R.id.txtItemCategory);
        txtRestaurant = (TextView) findViewById(R.id.txtRestaurant);
        txtTagLine = (TextView) findViewById(R.id.txtTagLine);
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.displayBadge();
    }
}
