package foodbazar.webmyne.com.foodbaazar.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.ui.activity.HomeScreen;


public class SettingFragment extends Fragment {

    View parentView;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();

        return fragment;
    }

    public SettingFragment() {
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
        parentView = inflater.inflate(R.layout.fragment_setting, container, false);
        init();
        return parentView;
    }

    private void init() {
        HomeScreen homeScreen = ((HomeScreen) getActivity());
        homeScreen.setTitle("Settings");
        homeScreen.setSubTitle("");
    }

}
