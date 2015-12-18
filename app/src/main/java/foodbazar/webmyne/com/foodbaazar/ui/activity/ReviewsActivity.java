package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.helpers.ToolHelper;
import foodbazar.webmyne.com.foodbaazar.model.HotelModel;
import foodbazar.webmyne.com.foodbaazar.ui.adapters.ReviewAdapter;
import foodbazar.webmyne.com.foodbaazar.ui.widget.SpacesItemDecoration;

public class ReviewsActivity extends AppCompatActivity {

    HotelModel hotelModel;
    View parentView;
    Toolbar toolbar;
    ToolHelper helper;
    private ImageView imgCartMenu;
    RecyclerView listReviews;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        hotelModel = new HotelModel();
        hotelModel = complexPreferences.getObject("reviews", HotelModel.class);

        init();
    }

    private void init() {
        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(hotelModel.HotelName);
            toolbar.setSubtitle("Reviews");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        helper = new ToolHelper(ReviewsActivity.this, toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findView();

        imgCartMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(ReviewsActivity.this, CartActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void findView() {
        imgCartMenu = (ImageView) findViewById(R.id.imgCartMenu);
        listReviews = (RecyclerView) findViewById(R.id.listReviews);

        listReviews.setLayoutManager(new GridLayoutManager(this, 1));
        listReviews.addItemDecoration(new SpacesItemDecoration(8));

        reviewAdapter = new ReviewAdapter(this, hotelModel.lstRR);
        listReviews.setAdapter(reviewAdapter);

    }
}
