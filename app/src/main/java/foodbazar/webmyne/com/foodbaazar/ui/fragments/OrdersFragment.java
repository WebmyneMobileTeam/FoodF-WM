package foodbazar.webmyne.com.foodbaazar.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.CheckOutOrderedList;
import foodbazar.webmyne.com.foodbaazar.model.UserOrder;
import foodbazar.webmyne.com.foodbaazar.model.UserProfile;
import foodbazar.webmyne.com.foodbaazar.ui.activity.HomeScreen;
import foodbazar.webmyne.com.foodbaazar.ui.adapters.AdapterOrders;
import foodbazar.webmyne.com.foodbaazar.ui.widget.SpacesItemDecoration;
import info.hoang8f.android.segmented.SegmentedGroup;

public class OrdersFragment extends Fragment {

    View parentView;
    ComplexPreferences complexPreferences;
    UserProfile currentUser;
    int userId;
    TextView txtNoOrders;
    RecyclerView listOrders;
    private CheckOutOrderedList orders;
    private AdapterOrders customAdapter;
    ProgressDialog pd;
    SegmentedGroup segmentedGrp;
    ArrayList<UserOrder> pendingList, completedList;
    int type = 0;
    SearchView searchView;

    public static OrdersFragment newInstance() {
        OrdersFragment fragment = new OrdersFragment();
        return fragment;
    }

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_orders, container, false);

        init();

        complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        currentUser = new UserProfile();
        currentUser = complexPreferences.getObject("current-user", UserProfile.class);
        userId = currentUser.UserId;

        getOrders();

        segmentedGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.tabPending) {
                    type = 0;
                    searchView.setVisibility(View.GONE);
                    if (pendingList.size() > 0) {
                        txtNoOrders.setVisibility(View.GONE);
                    } else {
                        txtNoOrders.setVisibility(View.VISIBLE);
                        txtNoOrders.setText("No Pending Orders");
                    }

                    customAdapter = new AdapterOrders(getActivity(), pendingList, type);
                    customAdapter.setOnRefreshListener(new AdapterOrders.onRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refreshOrders();
                        }
                    });

                    listOrders.setAdapter(customAdapter);

                } else {
                    searchView.setVisibility(View.VISIBLE);
                    type = 1;
                    if (completedList.size() > 0) {
                        txtNoOrders.setVisibility(View.GONE);
                    } else {
                        txtNoOrders.setVisibility(View.VISIBLE);
                        txtNoOrders.setText("No Completed/Cancelled Orders");
                    }

                    customAdapter = new AdapterOrders(getActivity(), completedList, type);
                    customAdapter.setOnRefreshListener(new AdapterOrders.onRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refreshOrders();
                        }
                    });

                    listOrders.setAdapter(customAdapter);
                }
            }
        });
        return parentView;
    }

    private void getOrders() {
        pd = ProgressDialog.show(getActivity(), "Loading", "Please wait..", false);
        orders = new CheckOutOrderedList();

        IceNet.connect()
                .createRequest()
                .get()
                .pathUrl(AppConstants.ORDER_HISTORY + userId)
                .fromJsonObject()
                .mappingInto(CheckOutOrderedList.class)
                .execute("RequestUpdate", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {

                        pd.dismiss();
                        orders = (CheckOutOrderedList) o;

                        if (orders.ResponseId != 1) {
                            txtNoOrders.setVisibility(View.VISIBLE);

                        } else {
                            if (orders.lstUSerOrderes.size() > 0) {
                                txtNoOrders.setVisibility(View.GONE);

                            } else {
                                txtNoOrders.setVisibility(View.VISIBLE);
                            }

                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                            complexPreferences.putObject("orders", orders);
                            complexPreferences.commit();

                            pendingList = new ArrayList<UserOrder>();
                            completedList = new ArrayList<UserOrder>();

                            for (int i = 0; i < orders.lstUSerOrderes.size(); i++) {

                                if (orders.lstUSerOrderes.get(i).OrderStatus == 1) {
                                    pendingList.add(orders.lstUSerOrderes.get(i));
                                    if (pendingList.size() > 0) {
                                        txtNoOrders.setVisibility(View.GONE);
                                        txtNoOrders.setText("No Pending Orders");
                                    } else {
                                        txtNoOrders.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    completedList.add(orders.lstUSerOrderes.get(i));
                                    if (completedList.size() > 0) {
                                        txtNoOrders.setVisibility(View.GONE);
                                        txtNoOrders.setText("No Completed/Cancelled Orders");
                                    } else {
                                        txtNoOrders.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            customAdapter = new AdapterOrders(getActivity(), pendingList, 0);
                            searchView.setVisibility(View.GONE);

                            listOrders.setAdapter(customAdapter);
                        }
                    }

                    @Override
                    public void onRequestError(RequestError error) {
                        Functions.snack(parentView, error.toString());
                        Log.e("error", error.toString());
                        pd.dismiss();
                    }
                });
    }

    private void init() {
        HomeScreen homeScreen = ((HomeScreen) getActivity());
        homeScreen.setTitle("Orders");
        homeScreen.setSubTitle("");

        findViewById();
    }

    private void findViewById() {
        txtNoOrders = (TextView) parentView.findViewById(R.id.txtNoOrders);
        listOrders = (RecyclerView) parentView.findViewById(R.id.listOrders);
        listOrders.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOrders.addItemDecoration(new SpacesItemDecoration(16));

        searchView = (SearchView) parentView.findViewById(R.id.searchView);
        searchView.setVisibility(View.GONE);
        searchView.setQueryHint(Html.fromHtml("<font color = #8c8c8c>" + getResources().getString(R.string.hintSearchMess) + "</font>"));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (TextUtils.isEmpty(query)) {
                    type = 1;
                    customAdapter = new AdapterOrders(getActivity(), completedList, type);
                    customAdapter.setOnRefreshListener(new AdapterOrders.onRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refreshOrders();
                        }
                    });

                    listOrders.setAdapter(customAdapter);

                } else {
                    type = 0;
                    final ArrayList<UserOrder> orders = filter(completedList, query);
                    customAdapter = new AdapterOrders(getActivity(), orders, type);
                    customAdapter.setOnRefreshListener(new AdapterOrders.onRefreshListener() {
                        @Override
                        public void onRefresh() {
                            refreshOrders();
                        }
                    });

                    listOrders.setAdapter(customAdapter);
                }
                return false;
            }
        });

        segmentedGrp = (SegmentedGroup) parentView.findViewById(R.id.segmented2);
        segmentedGrp.setTintColor(getResources().getColor(R.color.choco_color), getResources().getColor(R.color.button_bg));

    }

    private ArrayList<UserOrder> filter(ArrayList<UserOrder> completedList, String query) {

        query = query.toLowerCase();

        final ArrayList<UserOrder> newOrders = new ArrayList<>();

        for (UserOrder order : completedList) {
            final String text = order.HotelName.toLowerCase();
            if (text.contains(query)) {
                newOrders.add(order);
            }
        }

        return newOrders;
    }

    public void refreshOrders() {
        HomeScreen homeScreen = ((HomeScreen) getActivity());
        homeScreen.displayOrders();
    }
}
