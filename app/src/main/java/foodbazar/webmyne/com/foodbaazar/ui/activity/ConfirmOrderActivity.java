package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.custom.LoginDialog;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.CallWebService;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.DatabaseHandler;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.model.CartPojo;
import foodbazar.webmyne.com.foodbaazar.model.UserProfile;

public class ConfirmOrderActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    Toolbar toolbar;
    View parentView;
    private EditText edtFirstName, edtLastName, edtMobile, edtEmail, edtAddress, edtPincode, edtDate, edtTime;
    private RadioGroup radioGroup;
    private Button btnConfirm, btnLogin;
    private int selectedId = 0;
    private CheckBox cbPreOrder;
    private LinearLayout dateTimeLayout;
    private String date, time, uuid, dateTime;
    DatabaseHandler handler;
    List<CartPojo> cartPojoList;
    int subTotal, TotalQty;
    long total;
    private String fname, lname, pincode, email, mobile, address;
    private LinearLayout loginLayout;
    SharedPreferences pref;
    int userId = 0, fetchUserId = 0;
    ProgressDialog pd;
    private TextView txtCharge;
    RadioButton radioPick, radioHome;
    double tax = 0.00;
    boolean home, pickup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        init();

        uuid = UUID.randomUUID().toString();
        total = getIntent().getLongExtra("total", 0);
        subTotal = getIntent().getIntExtra("subTotal", 0);
        tax = getIntent().getDoubleExtra("tax", 0.0);

        fetchCartDetails();

        btnConfirm.setOnClickListener(this);
        edtDate.setOnClickListener(this);
        edtTime.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton btn = (RadioButton) radioGroup.findViewById(checkedId);
                selectedId = checkedId;

                if (selectedId == R.id.radioHome) {
                    txtCharge.setVisibility(View.VISIBLE);
                } else {
                    txtCharge.setVisibility(View.GONE);
                }
            }
        });

        cbPreOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dateTimeLayout.setVisibility(View.VISIBLE);
                } else {
                    dateTimeLayout.setVisibility(View.GONE);
                }
            }
        });

    }

    private void fetchCartDetails() {
        cartPojoList = new ArrayList<>();

        handler = new DatabaseHandler(this);
        cartPojoList = handler.getCartPojos();

        for (int i = 0; i < cartPojoList.size(); i++) {
            TotalQty += cartPojoList.get(i).getQuantity();
        }

        home = getSharedPreferences("delivery", Context.MODE_PRIVATE).getBoolean("home", false);
        pickup = getSharedPreferences("delivery", Context.MODE_PRIVATE).getBoolean("pickup", false);

        if (home)
            radioHome.setEnabled(true);
        else
            radioHome.setEnabled(false);

        if (pickup)
            radioPick.setEnabled(true);
        else
            radioPick.setEnabled(false);

    }

    private boolean isValidation() {
        boolean valid;
        if (Functions.isBlank(edtFirstName)) {
            Functions.snack(parentView, "Enter first name");
            valid = false;
        } else if (Functions.isBlank(edtLastName)) {
            Functions.snack(parentView, "Enter last name");
            valid = false;
        } else if (Functions.isBlank(edtMobile)) {
            valid = false;
            Functions.snack(parentView, "Enter mobile number");
        } else if (Functions.isBlank(edtEmail)) {
            Functions.snack(parentView, "Enter last name");
            valid = false;
        } else if (Functions.isBlank(edtAddress)) {
            Functions.snack(parentView, "Enter Address");
            valid = false;
        } else if (Functions.isBlank(edtPincode)) {
            Functions.snack(parentView, "Enter Pincode");
            valid = false;
        } else if (Functions.getLength(edtPincode) != 6) {
            Functions.snack(parentView, "Enter valid pincode");
            valid = false;
        } else if (Functions.getLength(edtMobile) != 10) {
            Functions.snack(parentView, "Enter valid mobile number");
            valid = false;
        } else if (!Functions.emailValidation(Functions.toStr(edtEmail))) {
            Functions.snack(parentView, "Enter valid email-id");
            valid = false;
        } else if (selectedId == 0) {
            Functions.snack(parentView, "Select any delivery type: Home Delivery or Pick Up");
            valid = false;
        } else if (cbPreOrder.isChecked()) {
            if (Functions.isBlank(edtDate) || Functions.isBlank(edtTime)) {
                Functions.snack(parentView, "Select Date and Time for Scheduled Pre-Order.");
                valid = false;
            } else {
                valid = true;
            }
        } else {
            valid = true;

        }
        return valid;
    }

    private void init() {
        findViewById();

        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Order Confirmation");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ImageView imgCart = (ImageView) toolbar.findViewById(R.id.imgCartMenu);
        imgCart.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoginDialog loginDialog = new LoginDialog(ConfirmOrderActivity.this);
                loginDialog.setCustomListner(new LoginDialog.changePasswordListener() {
                    @Override
                    public void onSuccess() {
                        onResume();
                    }
                });
                loginDialog.show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        pref = getSharedPreferences("login", Context.MODE_PRIVATE);
        if (pref.contains("isUserLogin")) {
            loginLayout.setVisibility(View.GONE);
            fillupDetails();

        } else {
            loginLayout.setVisibility(View.VISIBLE);
        }

    }

    private void fillupDetails() {

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ConfirmOrderActivity.this, "user_pref", 0);
        UserProfile currentUser = new UserProfile();
        currentUser = complexPreferences.getObject("current-user", UserProfile.class);
        userId = currentUser.UserId;

        edtFirstName.setText(currentUser.FirstName);
        edtLastName.setText(currentUser.LastName);
        edtMobile.setText(currentUser.MobileNo);
        edtEmail.setText(currentUser.EmailId);
        if (currentUser.EmailId != null) {
            edtEmail.setEnabled(false);
        } else {
            edtEmail.setEnabled(true);
        }
        edtAddress.setText(currentUser.Address);
        edtPincode.setText(currentUser.Zip);

    }

    private void findViewById() {
        txtCharge = (TextView) findViewById(R.id.txtCharge);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        loginLayout = (LinearLayout) findViewById(R.id.loginLayout);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtMobile = (EditText) findViewById(R.id.edtMobile);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        edtPincode = (EditText) findViewById(R.id.edtPincode);
        cbPreOrder = (CheckBox) findViewById(R.id.cbPreOrder);
        dateTimeLayout = (LinearLayout) findViewById(R.id.dateTimeLayout);
        edtDate = (EditText) findViewById(R.id.edtDate);
        edtTime = (EditText) findViewById(R.id.edtTime);

        radioHome = (RadioButton) radioGroup.findViewById(R.id.radioHome);
        radioPick = (RadioButton) radioGroup.findViewById(R.id.radioPick);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnConfirm:
                if (cbPreOrder.isChecked()) {
                    dateTime = date + " " + time;
                } else {
                    dateTime = "blank";
                }

                if (isValidation()) {
                    doPlaceOrder();
                }
                break;

            case R.id.edtDate:
                Calendar now = Calendar.getInstance();
                now.add(Calendar.DAY_OF_MONTH, 1);

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ConfirmOrderActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                Calendar nowMin = Calendar.getInstance();
                nowMin.add(Calendar.DAY_OF_MONTH, 1);
                dpd.setMinDate(nowMin);

                Calendar nowMax = Calendar.getInstance();
                nowMax.add(Calendar.DAY_OF_MONTH, 3);
                dpd.setMaxDate(nowMax);

                dpd.show(getFragmentManager(), "Datepickerdialog");

                break;

            case R.id.edtTime:
                Calendar now1 = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        ConfirmOrderActivity.this,
                        now1.get(Calendar.HOUR_OF_DAY),
                        now1.get(Calendar.MINUTE),
                        true
                );
                /*tpd.setMinTime(now1.get(Calendar.HOUR_OF_DAY),
                        now1.get(Calendar.MINUTE), now1.get(Calendar.SECOND));*/
                tpd.show(getFragmentManager(), "Timepickerdialog");
                break;
        }
    }

    private void doPlaceOrder() {
        pd = ProgressDialog.show(ConfirmOrderActivity.this, "Loading..", "Please wait..", false);
        if (cbPreOrder.isChecked()) {
            dateTime = date + " " + time;
        } else {
            dateTime = "blank";
        }

        fname = Functions.toStr(edtFirstName);
        lname = Functions.toStr(edtLastName);
        pincode = Functions.toStr(edtPincode);
        email = Functions.toStr(edtEmail);
        mobile = Functions.toStr(edtMobile);
        address = Functions.toStr(edtAddress);

        new AsyncTask<Void, Void, Void>() {

            JSONObject mainObect = new JSONObject();
            JSONArray productsArray = new JSONArray();

            @Override
            protected Void doInBackground(Void... params) {

                try {

                    mainObect.put("CustomerFirstName", fname);
                    mainObect.put("CustomerLastName", lname);
                    mainObect.put("DeliveryArea", "Test");
                    mainObect.put("DeliveryAddress", address);
                    mainObect.put("DeliveryCity", 1);
                    mainObect.put("DeliveryCityName", "Vadodara");
                    mainObect.put("DeliveryCountry", 1);
                    mainObect.put("DeliveryCountryName", "India");
                    mainObect.put("DeliveryState", 1);
                    mainObect.put("DeliveryStateName", "Gujarat");
                    mainObect.put("PostCode", pincode);
                    mainObect.put("Userid", userId);
                    mainObect.put("DeliveryAmount", 10);

                    if (selectedId == R.id.radioHome) {
                        mainObect.put("DeliveryType", true);
                    } else {
                        mainObect.put("DeliveryType", false);
                    }

                    mainObect.put("EmailId", email);
                    mainObect.put("MobileNo", mobile);
                    mainObect.put("OrderBy", userId);
                    mainObect.put("OrderId", uuid);
                    mainObect.put("OrderStatus", 1);
                    mainObect.put("PriceToPay", total);
                    mainObect.put("RestaurantID", cartPojoList.get(0).getRestaurantId());
                    mainObect.put("Tax", tax);
                    mainObect.put("TotalPrice", subTotal);
                    mainObect.put("TotalQty", TotalQty);
                    mainObect.put("preordertemp", dateTime);

                    for (int i = 0; i < cartPojoList.size(); i++) {

                        JSONObject orderObject = new JSONObject();
                        orderObject.put("ItemID", cartPojoList.get(i).getPriceId());
                        orderObject.put("MenuItem", 1);
                        orderObject.put("OrderId", uuid);
                        orderObject.put("OrderItemId", uuid);
                        orderObject.put("Quantity", cartPojoList.get(i).getQuantity());
                        orderObject.put("Price", cartPojoList.get(i).getItemPrice() * cartPojoList.get(i).getQuantity());

                        JSONArray extrasArray = new JSONArray();

                        if (cartPojoList.get(i).getExtraPojos().size() > 0) {

                            for (int j = 0; j < cartPojoList.get(i).getExtraPojos().size(); j++) {

                                JSONObject extraObject = new JSONObject();
                                extraObject.put("ExtraID", cartPojoList.get(i).getExtraPojos().get(j).getExtraId());
                                extraObject.put("OrderItemId", cartPojoList.get(i).getExtraPojos().get(j).getItemTypeId());
                                extrasArray.put(extraObject);
                            }
                            orderObject.put("lstOrderExtras", extrasArray);

                        } else {
                            orderObject.put("lstOrderExtras", extrasArray);
                        }

                        productsArray.put(orderObject);

                    }
                    mainObect.put("lstOrderItem", productsArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //Log.e("datetime", dateTime);
                Log.e("mainObect", mainObect.toString());

                callWebservice(mainObect);

            }
        }.execute();
    }

    private void callWebservice(JSONObject mainObect) {

        new CallWebService(AppConstants.PLACE_ORDER, CallWebService.TYPE_POST, mainObect) {
            @Override
            public void response(String response) {
                Log.e("order_response", response);
                pd.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("ResponseId").equals("1")) {
                        Functions.snack(parentView, "Place Order Successfully.");
                        fetchUserId = object.getInt("UserID");
                        Log.e("fetchUserId", fetchUserId + " user");

                        if (userId != fetchUserId) {
                            UserProfile profile = new UserProfile();
                            profile.Address = address;
                            profile.EmailId = email;
                            profile.FirstName = fname;
                            profile.LastName = lname;
                            profile.MobileNo = mobile;
                            profile.Zip = pincode;
                            profile.UserId = fetchUserId;

                            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ConfirmOrderActivity.this, "user_pref", 0);
                            complexPreferences.putObject("current-user", profile);
                            complexPreferences.commit();

                            pref = getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("isUserLogin", true);
                            editor.commit();
                        }

                        handler = new DatabaseHandler(ConfirmOrderActivity.this);
                        handler.deleteCart();

                        SharedPreferences pref = getSharedPreferences("delivery", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove("home");
                        editor.remove("pickup");
                        editor.commit();

                        new CountDownTimer(2000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                Intent homeIntent = new Intent(ConfirmOrderActivity.this, HomeScreen.class);
                                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                homeIntent.putExtra("value", "order");
                                startActivity(homeIntent);

                            }
                        }.start();
                    } else {
                        Functions.snack(parentView, "Error: " + object.getString("ResponseMsg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void error(String error) {
                Log.e("error", error);
                pd.dismiss();
            }
        }.call();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String month = (monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : "" + (monthOfYear + 1);
        String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;

        date = month + "/" + day + "/" + year;

        edtDate.setText(date);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;

        time = hourString + ":" + minuteString + ":" + secondString;
        edtTime.setText(time);
    }
}
