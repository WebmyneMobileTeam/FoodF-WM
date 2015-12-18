package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.lacronicus.easydatastorelib.DatastoreBuilder;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.custom.AskDialog;
import foodbazar.webmyne.com.foodbaazar.helpers.ApplicationPrefs;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.helpers.ToolHelper;
import foodbazar.webmyne.com.foodbaazar.model.UserProfile;
import foodbazar.webmyne.com.foodbaazar.ui.fragments.AboutFragment;
import foodbazar.webmyne.com.foodbaazar.ui.fragments.ContactFragment;
import foodbazar.webmyne.com.foodbaazar.ui.fragments.HotelFragment;
import foodbazar.webmyne.com.foodbaazar.ui.fragments.LocationFragment;
import foodbazar.webmyne.com.foodbaazar.ui.fragments.LoginFragment;
import foodbazar.webmyne.com.foodbaazar.ui.fragments.OrdersFragment;

public class HomeScreen extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView view;
    Toolbar toolbar;
    private ApplicationPrefs applicationPrefs;
    View parentView;
    SharedPreferences pref;
    private ImageView imgCartMenu;
    String value;
    ToolHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        helper = new ToolHelper(HomeScreen.this, toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        view = (NavigationView) findViewById(R.id.navigation_view);
        parentView = findViewById(android.R.id.content);
        imgCartMenu = (ImageView) findViewById(R.id.imgCartMenu);

        applicationPrefs = new DatastoreBuilder(PreferenceManager.getDefaultSharedPreferences(this))
                .create(ApplicationPrefs.class);

        if (toolbar != null) {
            toolbar.setTitle("Food Baazar");
            setSupportActionBar(toolbar);
        }

        initDrawer();

        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                setDrawerClick(menuItem.getItemId());

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });

        imgCartMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(HomeScreen.this, CartActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        value = getIntent().getStringExtra("value");

        if (value == null || !value.equals("order")) {
            displayLocations();
        } else {
            displayOrders();

            MenuItem item = view.getMenu().findItem(R.id.drawer_orders);
            item.setChecked(true);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Functions.isConnecting(this)) {
            Functions.toast(this, "No Internet Connection.");
            //  finish();
        }
        helper.displayBadge();
        setLogoutVisibility();
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }

    public void setSubTitle(String subTitle) {

        toolbar.setSubtitle(subTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);

    }

    private void setDrawerClick(int itemId) {

        switch (itemId) {
            case R.id.drawer_location:
                displayLocations();
                break;

            case R.id.drawer_hotels:
                displayHotels();
                break;

            case R.id.drawer_orders:
                pref = getSharedPreferences("login", Context.MODE_PRIVATE);
                if (pref.contains("isUserLogin")) {
                    displayOrders();

                } else {
                    MenuItem item = view.getMenu().findItem(R.id.drawer_accounts);
                    item.setChecked(true);

                    displayAccount();
                }
                break;

            case R.id.drawer_accounts:
                displayAccount();
                break;

            case R.id.drawer_aboutus:
                displayAboutUs();
                break;

            case R.id.drawer_contactus:
                displayContact();
                break;

            case R.id.drawer_logout:
                AskDialog askDialog = new AskDialog(HomeScreen.this, "Are you sure want to logout?");
                askDialog.setOnButtonsEventListener(new AskDialog.OnButtonEventListener() {
                    @Override
                    public void clickYes() {
                        closeSession();
                    }

                    @Override
                    public void clickNo() {

                    }
                });
                askDialog.show();
                break;

        }
    }

    private void displayContact() {
        MenuItem item1 = view.getMenu().findItem(R.id.drawer_contactus);
        item1.setChecked(true);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, ContactFragment.newInstance(), "contact");
        ft.commit();
    }

    private void closeSession() {
        ComplexPreferences complexPreferences;
        complexPreferences = ComplexPreferences.getComplexPreferences(HomeScreen.this, "user_pref", 0);
        UserProfile blankUser = new UserProfile();
        complexPreferences.putObject("current-user", blankUser);
        complexPreferences.commit();

        SharedPreferences preferences2 = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences2.edit();
        editor2.remove("isUserLogin");
        editor2.commit();

        setLogoutVisibility();

        displayAccount();
    }

    private void displayAboutUs() {
        MenuItem item1 = view.getMenu().findItem(R.id.drawer_aboutus);
        item1.setChecked(true);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, AboutFragment.newInstance(), "about_us");
        ft.commit();
    }

    private void displayAccount() {
        MenuItem item1 = view.getMenu().findItem(R.id.drawer_accounts);
        item1.setChecked(true);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, LoginFragment.newInstance(), "account");
        ft.commit();
    }

    public void displayOrders() {
        MenuItem item1 = view.getMenu().findItem(R.id.drawer_orders);
        item1.setChecked(true);

        value = "location";
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, OrdersFragment.newInstance(), "orders");
        ft.commit();

    }

    public void displayLocations() {
        MenuItem item1 = view.getMenu().findItem(R.id.drawer_location);
        item1.setChecked(true);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, LocationFragment.newInstance(), "location");
        ft.commit();

    }

    public void displayHotels() {
        MenuItem item1 = view.getMenu().findItem(R.id.drawer_hotels);
        item1.setChecked(true);

        view.getMenu().getItem(1).setChecked(true);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.content, HotelFragment.newInstance(), "restaurants");
        ft.commit();
    }

    public void setLogoutVisibility() {
        pref = getSharedPreferences("login", Context.MODE_PRIVATE);
        if (pref.contains("isUserLogin")) {
            MenuItem item = view.getMenu().findItem(R.id.drawer_logout);
            item.setVisible(true);

            MenuItem item1 = view.getMenu().findItem(R.id.drawer_orders);
            item1.setVisible(true);

        } else {
            MenuItem item = view.getMenu().findItem(R.id.drawer_logout);
            item.setVisible(false);

            MenuItem item1 = view.getMenu().findItem(R.id.drawer_orders);
            item1.setVisible(false);

        }
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment instanceof HotelFragment) {
            displayLocations();

        } else {
            final AskDialog askDialog = new AskDialog(this, "Are you sure want to close app?");
            askDialog.setOnButtonsEventListener(new AskDialog.OnButtonEventListener() {
                @Override
                public void clickYes() {
                    askDialog.dismiss();
                    finish();
                }

                @Override
                public void clickNo() {

                }
            });
            askDialog.show();
        }
    }
}
