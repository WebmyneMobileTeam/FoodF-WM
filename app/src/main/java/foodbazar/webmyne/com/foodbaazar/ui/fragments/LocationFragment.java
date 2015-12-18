package foodbazar.webmyne.com.foodbaazar.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.lacronicus.easydatastorelib.DatastoreBuilder;

import java.util.ArrayList;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ApplicationPrefs;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestCallback;
import foodbazar.webmyne.com.foodbaazar.icenet.RequestError;
import foodbazar.webmyne.com.foodbaazar.model.Area;
import foodbazar.webmyne.com.foodbaazar.model.CityObject;
import foodbazar.webmyne.com.foodbaazar.ui.activity.HomeScreen;
import foodbazar.webmyne.com.foodbaazar.ui.activity.StreetFoodActivity;


public class LocationFragment extends Fragment implements View.OnClickListener {

    private HomeScreen homeScreen;
    private CityObject cityObject;
    private ApplicationPrefs applicationPrefs;
    private ArrayList<Area> areas;
    ActionSheetDialog dialog;
    View parentView;
    private TextView txtAreaName;
    private int selectedPosition = -1;
    private LinearLayout findRestaurant, findStreetFood;
    ProgressDialog dialogLoading;
    AutoCompleteTextView edtArea;

    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();

        return fragment;
    }

    public LocationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        homeScreen = (HomeScreen) getActivity();
        applicationPrefs = new DatastoreBuilder(PreferenceManager.getDefaultSharedPreferences(getActivity()))
                .create(ApplicationPrefs.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parentView = inflater.inflate(R.layout.fragment_location_another, container, false);
        edtArea = (AutoCompleteTextView) parentView.findViewById(R.id.edtArea);
        edtArea.setThreshold(2);

        findStreetFood = (LinearLayout) parentView.findViewById(R.id.findStreetFood);
        findStreetFood.setOnClickListener(this);

        findRestaurant = (LinearLayout) parentView.findViewById(R.id.findRestaurant);
        findRestaurant.setOnClickListener(this);

        return parentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeScreen homeScreen = ((HomeScreen) getActivity());
        homeScreen.setTitle("Vadodara");
        homeScreen.setSubTitle("");
        fetchLocations();
        String receivedArea = applicationPrefs.selectedAreaName().get();

        if (receivedArea == null || receivedArea.isEmpty()) {
            selectedPosition = -1;
        } else {
            selectedPosition = Integer.parseInt(applicationPrefs.selectedAreaId().get());
            edtArea.setText(receivedArea);
        }

        if (!Functions.isConnecting(getActivity())) {
            if (dialogLoading.isShowing()) {
                dialogLoading.dismiss();
            }
            getActivity().finish();
        }

    }

    private void fetchLocations() {

        dialogLoading = ProgressDialog.show(getActivity(), "Please wait", "Fetching Areas", false);

        IceNet.connect()
                .createRequest()
                .get()
                .pathUrl(AppConstants.GET_CITY)
                .fromJsonObject()
                .mappingInto(CityObject.class)
                .execute("RequestLocations", new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object o) {
                        dialogLoading.dismiss();
                        try {

                            cityObject = (CityObject) o;

                            areas = cityObject.cityArrayList.get(0).areaListArrayList;
                            CustomerAdapter customerAdapter = new CustomerAdapter(getActivity(), R.layout.auto_item, areas);
                            edtArea.setAdapter(customerAdapter);
                            edtArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(edtArea.getWindowToken(), 0);

                                    applicationPrefs.selectedAreaName().put(areas.get(position).AreaName);
                                    applicationPrefs.selectedAreaId().put("" + areas.get(position).AreaId);
                                    applicationPrefs.selectedAreaPosition().put(position);
                                    selectedPosition = position;
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onRequestError(RequestError requestError) {
                        dialogLoading.dismiss();
//                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.txtAreaName:
                openDialogArea();
                break;

            case R.id.findStreetFood:
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(findStreetFood.getWindowToken(), 0);
                Functions.fireIntent(getActivity(), StreetFoodActivity.class);
                break;

            case R.id.findRestaurant:
                InputMethodManager inputMethodManager1 = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager1.hideSoftInputFromWindow(findRestaurant.getWindowToken(), 0);
                if (selectedPosition == -1) {
                    Functions.snack(parentView, "Please choose your area first");

                } else {
                    homeScreen.displayHotels();
                }
                break;
        }

    }

    private void openDialogArea() {

        if (cityObject == null || cityObject.cityArrayList.isEmpty()) {
            Functions.snack(parentView, "Error while fetching areas");

        } else {

            areas = cityObject.cityArrayList.get(0).areaListArrayList;
            String[] arr = new String[areas.size()];

            for (int i = 0; i < areas.size(); i++) {
                arr[i] = areas.get(i).AreaName;
            }


            dialog = new ActionSheetDialog(getActivity(), arr, null);
            dialog.title("Select Area")
                    .titleTextSize_SP(22f)
                    .itemTextColor(getResources().getColor(R.color.accent_dark))
                    .titleTextColor(getResources().getColor(R.color.primary))
                    .show();

            dialog.setOnOperItemClickL(new OnOperItemClickL() {
                @Override
                public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    selectedPosition = position;
                    txtAreaName.setText(String.format("%s", areas.get(position).AreaName));
                    applicationPrefs.selectedAreaName().put(areas.get(position).AreaName);
                    applicationPrefs.selectedAreaId().put("" + areas.get(position).AreaId);
                    applicationPrefs.selectedAreaPosition().put(position);
                }
            });

        }

    }


    private class CustomerAdapter extends ArrayAdapter<Area> {

        Context context;
        private ArrayList<Area> items;
        private ArrayList<Area> itemsAll;
        private ArrayList<Area> suggestions;
        private int viewResourceId;
        LayoutInflater li;

        public CustomerAdapter(Context context, int viewResourceId, ArrayList<Area> items) {
            super(context, viewResourceId, items);
            this.items = items;
            this.itemsAll = (ArrayList<Area>) items.clone();
            this.suggestions = new ArrayList<Area>();
            this.viewResourceId = viewResourceId;
            li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = li.inflate(viewResourceId, null);
            }

            Area a = items.get(position);
            if (a != null) {
                TextView txtArea = (TextView) convertView.findViewById(R.id.autoTextView);
                if (txtArea != null) {
                    txtArea.setText(a.AreaName);
                }
            }
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return nameFilter;
        }

        Filter nameFilter = new Filter() {

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                String str = ((Area) (resultValue)).AreaName;
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                if (constraint != null) {
                    suggestions.clear();
                    for (Area area : itemsAll) {
                        if (area.AreaName.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            suggestions.add(area);
                        }
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<Area> filteredList = (ArrayList<Area>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (Area a : filteredList) {
                        add(a);
                    }
                    notifyDataSetChanged();
                }

            }
        };
    }
}
