package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.helpers.ToolHelper;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.Gallery;
import foodbazar.webmyne.com.foodbaazar.model.GalleryPojo;

public class StreetFoodActivity extends AppCompatActivity {

    Toolbar toolbar;
    View parentView;

    private GridView gridView1;
    ProgressDialog pd;
    Gallery gallery;
    List<GalleryPojo> galleryPojos;
    String strCuisine;
    private ImageView imgCartMenu;
    ToolHelper helper;
    RelativeLayout linearMain;
    TextView txtLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_food);

        init();

        imgCartMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(StreetFoodActivity.this, CartActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void init() {
        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Street Food");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        helper = new ToolHelper(StreetFoodActivity.this, toolbar);

        imgCartMenu = (ImageView) findViewById(R.id.imgCartMenu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        findViewById();

        fetchStreetFood();

    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.displayBadge();
    }

    private void fetchStreetFood() {

        pd = ProgressDialog.show(this, "Loading.", "Please wait..");
        gridView1.setVisibility(View.GONE);

        galleryPojos = new ArrayList<>();
        IceNet.connect()
                .createRequest()
                .get()
                .pathUrl(AppConstants.GET_GALLERY_INFO)
                .fromJsonObject()
                .mappingInto(Gallery.class)
                .execute("RequestLocations", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {

                        try {
                            gallery = (Gallery) o;
                            galleryPojos = gallery.lstGallery;

                            StringBuilder sb = new StringBuilder();

                            for (int i = 0; i < galleryPojos.size(); i++) {
                                sb.append(galleryPojos.get(i).GalleryName + ", ");
                                strCuisine = sb.toString().substring(0, sb.toString().length() - 2);
                            }

                            gridView1.setAdapter(new CustomAdapter(StreetFoodActivity.this, galleryPojos));
                            gridView1.setVisibility(View.VISIBLE);

                            gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    Intent i = new Intent(StreetFoodActivity.this, HotelDetailsActivity.class);
                                    i.putExtra("hotelId", 1);
                                    i.putExtra("hotelName", "Street Food");
                                    i.putExtra("hotelCuisine", strCuisine);
                                    i.putExtra("IsDelivery", true);
                                    i.putExtra("IsPickUp", false);
                                    i.putExtra("cuisineID", galleryPojos.get(position).GalleryName);
                                    i.putExtra("ID", galleryPojos.get(position).CuisineID);

                                    startActivity(i);
                                }
                            });

                            pd.dismiss();
                            txtLoading.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onRequestError(RequestError requestError) {
                        pd.dismiss();
                        Functions.snack(parentView, "Error " + gallery.ResponseMsg);
                    }
                });
    }

    private void findViewById() {
        txtLoading = (TextView) findViewById(R.id.txtLoading);
        linearMain = (RelativeLayout) findViewById(R.id.linearMain);
        gridView1 = (GridView) findViewById(R.id.gridView1);
    }

    private class CustomAdapter extends BaseAdapter {

        Context context;
        List<GalleryPojo> galleryPojos;
        private LayoutInflater inflater = null;

        public CustomAdapter(Context context, List<GalleryPojo> galleryPojos) {
            this.context = context;
            this.galleryPojos = galleryPojos;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return galleryPojos.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View vi = convertView;
            ViewHolder holder;

            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            if (convertView == null) {
                vi = inflater.inflate(R.layout.item_street_food, null);
                holder = new ViewHolder();

                holder.txtStreetFood = (TextView) vi.findViewById(R.id.txtStreetFood);
                holder.imgStreetFood = (ImageView) vi.findViewById(R.id.imgStreetFood);
                vi.setTag(holder);

            } else {
                holder = (ViewHolder) vi.getTag();
            }

            holder.txtStreetFood.setText(galleryPojos.get(position).GalleryName);

            if (!galleryPojos.get(position).Image.equals("")) {
                Glide.with(context).load(AppConstants.IMAGE_PREFIX + galleryPojos.get(position).ImagePath + galleryPojos.get(position).Image).into(holder.imgStreetFood);
            } else {
                Glide.with(context).load(R.drawable.bg_image_small).into(holder.imgStreetFood);
            }
            txtLoading.setVisibility(View.GONE);

            return vi;
        }

        private class ViewHolder {
            ImageView imgStreetFood;
            TextView txtStreetFood;
        }
    }
}
