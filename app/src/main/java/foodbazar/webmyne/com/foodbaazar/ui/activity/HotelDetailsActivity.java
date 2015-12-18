package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.helpers.ToolHelper;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.ListHeader;
import foodbazar.webmyne.com.foodbaazar.model.MenuItemNew;
import foodbazar.webmyne.com.foodbaazar.model.MenuTypeItemstrans;
import foodbazar.webmyne.com.foodbaazar.model.ResponseHotel;
import foodbazar.webmyne.com.foodbaazar.model.ResponseHotelMenuNew;
import info.hoang8f.android.segmented.SegmentedGroup;

public class HotelDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int hotelId;
    private String hotelName, hotelCuisine;
    private View parentView;
    ResponseHotel hotelObject;
    ArrayList<CatObject> catArray;
    ResponseHotelMenuNew menu;
    private LinearLayout catLayout;
    ArrayList<ItemClass> itemArray;
    StringBuilder sb;
    private CardView timingLayout, ratingLayout;

    ExpandableListView exp_list;
    List<String> listDataHeaderTitles;

    List<ListHeader> headers;

    HashMap<String, List<ItemClass>> listDataChild;
    ExpandableListAdapter listAdapter;
    private RelativeLayout noMenu;
    private Button btnTry;
    ProgressDialog pd;
    private ImageView imgCartMenu, imgHome, imgPick;
    private TextView txtCuisine, txtAddress, txtTime, txtRate, txtMoreRating;
    boolean IsDelivery, IsPickUp;
    private double serviceTax;
    SegmentedGroup segmentedGrp;
    private LinearLayout infoLayout;
    int rating = 0;
    ToolHelper helper;
    String cuisineID;
    int ID;
    CardView addressCard;

    private ImageView imgVeg, imgNonveg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details);

        hotelId = getIntent().getIntExtra("hotelId", 0);
        hotelName = getIntent().getStringExtra("hotelName");
        hotelCuisine = getIntent().getStringExtra("hotelCuisine");
        cuisineID = getIntent().getStringExtra("cuisineID");
        ID = getIntent().getIntExtra("ID", 0);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        hotelObject = new ResponseHotel();
        hotelObject = complexPreferences.getObject("hotels", ResponseHotel.class);

        init();
    }


    private class CatObject {
        public int catId;

        public String getCatName() {
            return catName;
        }

        public void setCatName(String catName) {
            this.catName = catName;
        }

        public int getCatId() {
            return catId;
        }

        public void setCatId(int catId) {
            this.catId = catId;
        }

        public String catName;
    }

    private class ItemClass {
        public int catID;
        public int itemID;
        public String itemName;

        public int getItemLowPrice() {
            return itemLowPrice;
        }

        public void setItemLowPrice(int itemLowPrice) {
            this.itemLowPrice = itemLowPrice;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getItemID() {
            return itemID;
        }

        public void setItemID(int itemID) {
            this.itemID = itemID;
        }

        public int getCatID() {
            return catID;
        }

        public void setCatID(int catID) {
            this.catID = catID;
        }

        public int itemLowPrice;
    }

    private void init() {
        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(hotelName);
            toolbar.setSubtitle("Menu");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        helper = new ToolHelper(HotelDetailsActivity.this, toolbar);

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
                Functions.fireIntent(HotelDetailsActivity.this, CartActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        loadMenu();

        segmentedGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.tabInfo) {
                    infoLayout.setVisibility(View.VISIBLE);
                    exp_list.setVisibility(View.GONE);

                } else {
                    infoLayout.setVisibility(View.GONE);
                    exp_list.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadMenu() {

        catArray = new ArrayList<>();
        pd = ProgressDialog.show(this, "Please wait", "Loading..", false);

        IceNet.connect()
                .createRequest()
                .get()
                .pathUrl(AppConstants.GET_HOTELS_MENU + hotelId)
                .fromJsonObject()
                .mappingInto(ResponseHotelMenuNew.class)
                .execute("RequestForget", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {

                        menu = (ResponseHotelMenuNew) o;
                        if (menu.responseId == 1) {

                            IsDelivery = menu.HotelDetail.get(0).IsDelivery;
                            IsPickUp = menu.HotelDetail.get(0).IsPickUp;
                            serviceTax = menu.HotelDetail.get(0).ServiceFees;

                            int index = menu.Menus.size();
                            itemArray = new ArrayList<ItemClass>();
                            for (int i = 0; i < index; i++) {

                                CatObject obj = new CatObject();
                                obj.setCatId(menu.Menus.get(i).CategoryID);
                                if (menu.Menus.get(i).CateGoryName == null) {
                                    obj.setCatName("Combo Offer");
                                } else {
                                    obj.setCatName(menu.Menus.get(i).CateGoryName);
                                }

                                catArray.add(obj);

                                ItemClass item = new ItemClass();
                                item.setCatID(menu.Menus.get(i).CategoryID);
                                item.setItemName(menu.Menus.get(i).ItemName);
                                item.setItemID(menu.Menus.get(i).ItemId);

                                ArrayList<Integer> prices = new ArrayList<Integer>();
                                ArrayList<MenuTypeItemstrans> lstMenuTypeItemstrans = menu.Menus.get(i).lstMenuTypeItemstrans;

                                for (int k = 0; k < lstMenuTypeItemstrans.size(); k++) {
                                    prices.add(lstMenuTypeItemstrans.get(k).Price);
                                }

                                Object obj1 = Collections.min(prices);

                                item.setItemLowPrice((Integer) obj1);

                                itemArray.add(item);
                            }

                        } else {
                            Functions.snack(parentView, menu.responseMessage);
                        }

                        if (catArray.size() > 0) {
                            noMenu.setVisibility(View.GONE);
                            catLayout.setVisibility(View.VISIBLE);
                            setMenuDetails();

                        } else {
                            catLayout.setVisibility(View.GONE);
                            noMenu.setVisibility(View.VISIBLE);
                        }
                        pd.dismiss();

                    }

                    @Override
                    public void onRequestError(RequestError error) {
                        Functions.snack(parentView, error.toString());
                        pd.dismiss();
                    }
                });

    }

    private void setMenuDetails() {
       /* Log.e("hotelId", hotelId + "");

        if (hotelId == 0)
            addressCard.setVisibility(View.GONE);
        else
            addressCard.setVisibility(View.VISIBLE);*/

        txtCuisine.setText(hotelCuisine);
        if (menu.HotelDetail.get(0).StreetAddress.equals("blank") || menu.HotelDetail.get(0).StreetAddress.trim().length() == 0) {
            txtAddress.setText("N/A");
        } else {
            txtAddress.setText(menu.HotelDetail.get(0).StreetAddress);
        }

        sb = new StringBuilder();
        if (menu.HotelDetail.get(0).Timings.size() == 0) {
            timingLayout.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < menu.HotelDetail.get(0).Timings.size(); i++) {

                String startTime[] = menu.HotelDetail.get(0).Timings.get(i).StartTimeString.toString().split(":");
                String endTime[] = menu.HotelDetail.get(0).Timings.get(i).EndTimeString.split(":");

                String newStart = startTime[0] + ":" + startTime[1];
                String newEnd = endTime[0] + ":" + endTime[1];

                String start = Functions.parseDate(newStart, "hh:mm", "hh:mm a");
                String end = Functions.parseDate(newEnd, "hh:mm", "hh:mm a");

                sb.append(start + " To " + end + "\n");
            }
            String str = sb.toString().substring(0, sb.toString().length() - 1);
            txtTime.setText(str);
            timingLayout.setVisibility(View.VISIBLE);
        }

        if (IsDelivery)
            imgHome.setVisibility(View.VISIBLE);
        else
            imgHome.setVisibility(View.GONE);

        if (IsPickUp)
            imgPick.setVisibility(View.VISIBLE);
        else
            imgPick.setVisibility(View.GONE);


        int vegNonveg = menu.HotelDetail.get(0).VegNonveg;
        switch (vegNonveg) {
            case 1: // veg
                imgVeg.setVisibility(View.VISIBLE);
                imgNonveg.setVisibility(View.GONE);
                break;

            case 2:// non-veg
                imgVeg.setVisibility(View.GONE);
                imgNonveg.setVisibility(View.VISIBLE);
                break;

            case 3:// both
                imgVeg.setVisibility(View.VISIBLE);
                imgNonveg.setVisibility(View.VISIBLE);
                break;
        }

        prepareListData();

        rating = 0;
        int ratingSize = menu.HotelDetail.get(0).lstRR.size();
        if (ratingSize > 0) {
            for (int j = 0; j < ratingSize; j++) {
                rating += menu.HotelDetail.get(0).lstRR.get(j).Rating;
            }

            txtRate.setText("Rating: " + new DecimalFormat("##.#").format((double) rating / ratingSize));
            ratingLayout.setVisibility(View.VISIBLE);

        } else {
            ratingLayout.setVisibility(View.GONE);
        }

        listAdapter = new ExpandableListAdapter(this, listDataHeaderTitles, listDataChild);

        exp_list.setAdapter(listAdapter);

        int pos = 0;

        for (int i = 0; i < listDataHeaderTitles.size(); i++) {

            if (listDataHeaderTitles.get(i).equalsIgnoreCase(cuisineID)) {
                pos = i;
            }
        }

        exp_list.expandGroup(pos);

        exp_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                int index = 0;
                for (int i = 0; i < menu.Menus.size(); i++) {
                    if (listDataChild.get(
                            listDataHeaderTitles.get(groupPosition)).get(
                            childPosition).getItemID() == menu.Menus.get(i).ItemId) {
                        index = i;
                    }
                }

                getItemDetails(index);

                return false;
            }
        });

        infoLayout.setVisibility(View.GONE);
        exp_list.setVisibility(View.VISIBLE);
    }

    private void getItemDetails(int index) {

        MenuItemNew itemDetails = menu.Menus.get(index);

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(HotelDetailsActivity.this, "user_pref", 0);
        complexPreferences.putObject("item", itemDetails);
        complexPreferences.commit();

        Intent fireIntent = new Intent(HotelDetailsActivity.this, ItemDetailsActivity.class);
        fireIntent.putExtra("serviceTax", serviceTax);
        fireIntent.putExtra("home", menu.HotelDetail.get(0).IsDelivery);
        fireIntent.putExtra("pickup", menu.HotelDetail.get(0).IsPickUp);
        startActivity(fireIntent);
    }

    private void prepareListData() {

        listDataChild = new HashMap<String, List<ItemClass>>();
        listDataHeaderTitles = new ArrayList<>();

        headers = new ArrayList<>();

        if (catArray.size() > 0) {

            for (int i = 0; i < catArray.size(); i++) {

                /*ListHeader header = new ListHeader();
                header.setId(catArray.get(i).getCatId());
                header.setName(catArray.get(i).getCatName());
                headers.add(header);*/

                listDataHeaderTitles.add(catArray.get(i).getCatName());
                ArrayList<String> arr = new ArrayList<>();
                ArrayList<ItemClass> items = new ArrayList<>();

                for (int j = 0; j < itemArray.size(); j++) {

                    if (catArray.get(i).getCatId() == itemArray.get(j).getCatID()) {

                        ItemClass it = new ItemClass();
                        it.setItemID(itemArray.get(j).getItemID());
                        it.setItemName(itemArray.get(j).getItemName());
                        it.setItemLowPrice(itemArray.get(j).getItemLowPrice());
                        items.add(it);
                        arr.add(it.getItemName());
                    }
                }

                listDataChild.put(catArray.get(i).getCatName(), items);
            }

        }

        HashSet hs = new HashSet();
        hs.addAll(listDataHeaderTitles);
        listDataHeaderTitles.clear();
        listDataHeaderTitles.addAll(hs);

        /*HashSet<ListHeader> hs1 = new HashSet();
        hs1.addAll(headers);
        headers.clear();
        headers.addAll(hs1);*/

    }

    private void findView() {
        addressCard = (CardView) findViewById(R.id.addressCard);
        txtMoreRating = (TextView) findViewById(R.id.txtMoreRating);
        imgVeg = (ImageView) findViewById(R.id.imgVeg);
        imgNonveg = (ImageView) findViewById(R.id.imgNonveg);

        ratingLayout = (CardView) findViewById(R.id.ratingLayout);
        ratingLayout.setVisibility(View.GONE);
        txtMoreRating = (TextView) findViewById(R.id.txtMoreRating);
        txtRate = (TextView) findViewById(R.id.txtRate);
        exp_list = (ExpandableListView) findViewById(R.id.exp_list);
        infoLayout = (LinearLayout) findViewById(R.id.infoLayout);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtCuisine = (TextView) findViewById(R.id.txtCuisine);
        imgCartMenu = (ImageView) findViewById(R.id.imgCartMenu);
        btnTry = (Button) findViewById(R.id.btnTry);
        noMenu = (RelativeLayout) findViewById(R.id.noMenu);
        catLayout = (LinearLayout) findViewById(R.id.catLayout);
        imgHome = (ImageView) findViewById(R.id.imgHome);
        imgPick = (ImageView) findViewById(R.id.imgPick);
        timingLayout = (CardView) findViewById(R.id.timingLayout);
        segmentedGrp = (SegmentedGroup) parentView.findViewById(R.id.segmented2);
        segmentedGrp.setTintColor(getResources().getColor(R.color.choco_color), getResources().getColor(R.color.button_bg));

        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtMoreRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(HotelDetailsActivity.this, "user_pref", 0);
                complexPreferences.putObject("reviews", menu.HotelDetail.get(0));
                complexPreferences.commit();

                Functions.fireIntent(HotelDetailsActivity.this, ReviewsActivity.class);

            }
        });

    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<ItemClass>> _listDataChild;

        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<ItemClass>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon).getItemName();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);
            final int childLowPrice = getPrice(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.child_item, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.txtItemName);
            TextView txtPrice = (TextView) convertView.findViewById(R.id.txtItemLowPrice);

            txtListChild.setText(childText);
            txtPrice.setText("From Rs. " + childLowPrice);
            return convertView;
        }

        private int getPrice(int groupPosition, int childPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosition).getItemLowPrice();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.group_item, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.txtCatName);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class ExpandableListAdapterAnother extends BaseExpandableListAdapter {

        private Context _context;
        private List<ListHeader> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<ItemClass>> _listDataChild;

        public ExpandableListAdapterAnother(Context context, List<ListHeader> listDataHeader,
                                            HashMap<String, List<ItemClass>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition).getName())
                    .get(childPosititon).getItemName();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);
            final int childLowPrice = getPrice(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.child_item, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.txtItemName);
            TextView txtPrice = (TextView) convertView.findViewById(R.id.txtItemLowPrice);

            txtListChild.setText(childText);
            txtPrice.setText("From Rs. " + childLowPrice);
            return convertView;
        }

        private int getPrice(int groupPosition, int childPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition).getName())
                    .get(childPosition).getItemLowPrice();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition).getName())
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            // String headerTitle = (String) getGroup(groupPosition);

            ListHeader header = (ListHeader) getGroup(groupPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.group_item, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.txtCatName);
            lblListHeader.setText(header.getName());

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.displayBadge();
    }
}
