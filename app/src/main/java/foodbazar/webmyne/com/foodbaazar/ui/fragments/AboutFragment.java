package foodbazar.webmyne.com.foodbaazar.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.ui.activity.HomeScreen;


public class AboutFragment extends Fragment implements View.OnClickListener {

    View parentView;
    TextView call1, call2;

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    public AboutFragment() {
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
        parentView = inflater.inflate(R.layout.fragment_about, container, false);
        init();
        return parentView;
    }

    private void init() {
        HomeScreen homeScreen = ((HomeScreen) getActivity());
        homeScreen.setTitle("About Us");
        homeScreen.setSubTitle("");

        call1 = (TextView) parentView.findViewById(R.id.call1);
        call2 = (TextView) parentView.findViewById(R.id.call2);

        call1.setOnClickListener(this);
        call2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        TextView tv = (TextView) v;
        Intent dialIntent = new Intent();
        dialIntent.setAction(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + tv.getText().toString()));
        startActivity(dialIntent);
    }
}
