package foodbazar.webmyne.com.foodbaazar.ui.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lacronicus.easydatastorelib.DatastoreBuilder;

import java.util.ArrayList;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ApplicationPrefs;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.Hotel;
import foodbazar.webmyne.com.foodbaazar.model.ResponseHotel;
import foodbazar.webmyne.com.foodbaazar.ui.activity.HomeScreen;
import foodbazar.webmyne.com.foodbaazar.ui.adapters.AdapterHotel;
import foodbazar.webmyne.com.foodbaazar.ui.widget.SpacesItemDecoration;


public class HotelFragment extends Fragment {

    private HomeScreen homeScreen;
    private ApplicationPrefs applicationPrefs;
    private String areaID;
    private ResponseHotel responseHotel;
    private DrawerLayout drawer_layout;
    private RecyclerView listHotels;
    private ArrayList<Hotel> hotels;
    private TextView txtResult;
    private Menu menuFilter;
    private ListView listCuisine;
    private RadioGroup grpSort;
    private View parentView;
    ProgressDialog pd;
    AdapterHotel adapterHotel;

    public static HotelFragment newInstance() {
        HotelFragment fragment = new HotelFragment();
        return fragment;
    }

    public HotelFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeScreen = (HomeScreen) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parentView = inflater.inflate(R.layout.fragment_hotel, container, false);
        drawer_layout = (DrawerLayout) parentView.findViewById(R.id.drawer_layout);
        listHotels = (RecyclerView) parentView.findViewById(R.id.listHotels);
        txtResult = (TextView) parentView.findViewById(R.id.txtResult);
        listCuisine = (ListView) parentView.findViewById(R.id.listCuisine);
        grpSort = (RadioGroup) parentView.findViewById(R.id.grpSort);

        setHasOptionsMenu(false);

        listCuisine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (hotels.size() > 0) {

                    SparseBooleanArray checked = listCuisine.getCheckedItemPositions();
                    ArrayList<Integer> selectedItems = new ArrayList<Integer>();

                    for (int i = 0; i < checked.size(); i++) {
                        int pos = checked.keyAt(i);

                        if (checked.valueAt(i)) {
                            selectedItems.add(responseHotel.lstCuisine.get(pos).CuisineID);
                        }
                    }

                    adapterHotel.setCuisineFilter(selectedItems);
                }
            }
        });

        grpSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (hotels.size() > 0) {

                    if (checkedId == R.id.radioAlpha) {
                        adapterHotel.setAlpha();

                    } else if (checkedId == R.id.radioVeg) {
                        adapterHotel.setVeg();

                    } else if (checkedId == R.id.radioNonveg) {
                        adapterHotel.setNonveg();

                    } else if (checkedId == R.id.radioHome) {
                        adapterHotel.setHomeDelivery();
                    }
                }

            }
        });

        applicationPrefs = new DatastoreBuilder(PreferenceManager.getDefaultSharedPreferences(getActivity()))
                .create(ApplicationPrefs.class);
        homeScreen.setTitle("Vadodara");
        homeScreen.setSubTitle(applicationPrefs.selectedAreaName().get());
        areaID = applicationPrefs.selectedAreaId().get();

        hotels = new ArrayList<>();

        if (areaID == null || areaID.equals("")) {
            txtResult.setText("First Select Area");
            listHotels.setVisibility(View.GONE);
            setHasOptionsMenu(false);

        } else {
            setHasOptionsMenu(true);
            fetchHotels();
        }

        return parentView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void displayEmptyMessage(String string) {
        txtResult.setVisibility(View.VISIBLE);
        listHotels.setVisibility(View.GONE);
        txtResult.setText(string);
        menuFilter.getItem(0).setVisible(false);
    }

    public void displayList() {
        txtResult.setVisibility(View.GONE);
        listHotels.setVisibility(View.VISIBLE);
        // menuFilter.getItem(0).setVisible(true);
    }


    private void fetchHotels() {
        pd = ProgressDialog.show(getActivity(), "Loading.", "Fetching Restaurants..", false);

        IceNet.connect()
                .createRequest()
                .get()
                .pathUrl(AppConstants.GET_HOTELS + areaID)
                .fromJsonObject()
                .mappingInto(ResponseHotel.class)
                .execute("RequestLocations", new RequestCallback() {

                    @Override
                    public void onRequestSuccess(Object o) {
                        pd.dismiss();

                        try {
                            responseHotel = (ResponseHotel) o;

                            if (responseHotel == null) {
                                Functions.snack(parentView, "Server time out");

                            } else {
                                if (responseHotel.responseCode == 1) {
                                    setHasOptionsMenu(true);
                                    displayList();
                                    displayHotels();

                                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                                    complexPreferences.putObject("hotels", responseHotel);
                                    complexPreferences.commit();

                                    ArrayList<String> cuisines = new ArrayList<String>();
                                    for (int i = 0; i < responseHotel.lstCuisine.size(); i++) {
                                        cuisines.add(responseHotel.lstCuisine.get(i).CuisineName);
                                    }
                                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, cuisines);
                                    listCuisine.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                                    listCuisine.setAdapter(adapter1);

                                } else if (responseHotel.responseCode == 4) {
                                    displayEmptyMessage(responseHotel.responseMessage);
                                    setHasOptionsMenu(false);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onRequestError(RequestError requestError) {
                        pd.dismiss();
                        //Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void displayHotels() {

        hotels = responseHotel.HotelList;

        adapterHotel = new AdapterHotel(getActivity(), hotels);
        listHotels.setLayoutManager(new LinearLayoutManager(getActivity()));
        listHotels.setAdapter(adapterHotel);
        listHotels.setVisibility(View.VISIBLE);
        listHotels.addItemDecoration(new SpacesItemDecoration(16));
        adapterHotel.setAlpha();

        if (hotels.size() == 0) {
            txtResult.setText("No Restaurant");
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menuFilter = menu;
        inflater.inflate(R.menu.menu_hotel_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter) {

            if (drawer_layout.isDrawerOpen(Gravity.RIGHT)) {
                drawer_layout.closeDrawer(Gravity.RIGHT);
            } else {
                drawer_layout.openDrawer(Gravity.RIGHT);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
