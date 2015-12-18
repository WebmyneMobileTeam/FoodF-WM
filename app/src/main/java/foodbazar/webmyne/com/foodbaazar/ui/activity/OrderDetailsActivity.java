package foodbazar.webmyne.com.foodbaazar.ui.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;

import java.io.File;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.AppConstants;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.helpers.ToolHelper;
import foodbazar.webmyne.com.foodbaazar.model.UserOrder;
import foodbazar.webmyne.com.foodbaazar.ui.widget.OrderDetailItem;

public class OrderDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    View parentView;
    private TextView txtOrderId, txtHotelName, txtOrderOn, txtAddress, txtTotalPrice, txtSubTotalPrice, txtTax, txtCustomerName, txtOrderStatus;
    private LinearLayout itemLayout;
    String status;
    private ImageView imgCartMenu;
    ToolHelper helper;
    TextView txtGenerate;
    String sheetURL;
    UserOrder currentOrder;
    ProgressDialog pd;
    File SDCardRoot, dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        init();

        SDCardRoot = Environment.getExternalStorageDirectory();
        dir = new File(SDCardRoot.getAbsolutePath()
                + "/FoodFAD Orders");
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    private void init() {
        imgCartMenu = (ImageView) findViewById(R.id.imgCartMenu);
        parentView = findViewById(android.R.id.content);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Order Details");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        helper = new ToolHelper(OrderDetailsActivity.this, toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById();

        imgCartMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.fireIntent(OrderDetailsActivity.this, CartActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        txtGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo PDF Generate
                pd = ProgressDialog.show(OrderDetailsActivity.this, "Generating order invoice.", "In Progress", false);
                sheetURL = AppConstants.GENERATE_PDF + currentOrder.OrderId;

                downloadPDF();
                // new DownloadSheet().execute();
            }
        });

        fetchOrderDetails();
    }

    private class DownloadSheet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = ProgressDialog.show(OrderDetailsActivity.this, "Generating order invoice.", "In Progress", false);
            sheetURL = AppConstants.GENERATE_PDF + currentOrder.OrderId;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            downloadPDF();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.dismiss();
            Functions.snack(findViewById(android.R.id.content), "Your file stored in Internal Storage -> FoodFAD Orders folder.");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            pd.setProgress(Integer.parseInt(values[0]));

        }
    }

    private void downloadPDF() {

        final DownloadRequest request = new DownloadRequest.Builder()
                .setTitle("MyOrder" + ".pdf")
                .setUri(sheetURL)
                .setFolder(dir)
                .build();

        DownloadManager.getInstance().download(request, "TAG", new CallBack() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onConnecting() {

            }

            @Override
            public void onConnected(long total, boolean isRangeSupport) {

            }

            @Override
            public void onProgress(long finished, long total, int progress) {
                pd.setProgress(progress);
            }

            @Override
            public void onCompleted() {
                pd.dismiss();
                Functions.snack(findViewById(android.R.id.content), "Your file stored in Internal Storage -> FoodFAD Orders folder.");
            }

            @Override
            public void onDownloadPaused() {

            }

            @Override
            public void onDownloadCanceled() {
                pd.dismiss();
            }

            @Override
            public void onFailed(DownloadException e) {
                pd.dismiss();
                Functions.snack(findViewById(android.R.id.content), "Error while generating invoice. Try again.!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.displayBadge();
    }

    private void fetchOrderDetails() {
        itemLayout.removeAllViews();
        itemLayout.invalidate();

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(OrderDetailsActivity.this, "user_pref", 0);
        currentOrder = new UserOrder();
        currentOrder = complexPreferences.getObject("current-order", UserOrder.class);

        txtOrderId.setText(currentOrder.OrderId);
        txtCustomerName.setText("Customer Name: " + currentOrder.CustomerFirstName + " " + currentOrder.CustomerLastName);
        txtHotelName.setText("Hotel Name: " + currentOrder.HotelName);
        txtOrderOn.setText("Order Created On " + currentOrder.CreatedOnString);
        txtAddress.setText(currentOrder.DeliveryArea + ", " + currentOrder.DeliveryCityName);

        for (int i = 0; i < currentOrder.lstOrderItem.size(); i++) {

            OrderDetailItem item = new OrderDetailItem(OrderDetailsActivity.this, currentOrder.lstOrderItem.get(i));
            itemLayout.addView(item);
        }
        txtTotalPrice.setText(getResources().getString(R.string.Rs) + " " + Math.round(currentOrder.PriceToPay));
        txtSubTotalPrice.setText(getResources().getString(R.string.Rs) + " " + currentOrder.TotalPrice);

        txtTax.setText(getResources().getString(R.string.Rs) + " " + (Math.round(currentOrder.PriceToPay) - currentOrder.TotalPrice));
        switch (currentOrder.OrderStatus) {
            case 1:
                status = "Pending";
                break;
            case 2:
                status = "Paid and Delievered";
                break;
            case 3:
                status = "Cancelled";
                break;
        }
        txtOrderStatus.setText("Order Status " + status);

    }

    private void findViewById() {
        txtGenerate = (TextView) findViewById(R.id.txtGenerate);
        txtSubTotalPrice = (TextView) findViewById(R.id.txtSubTotalPrice);
        txtOrderId = (TextView) findViewById(R.id.txtOrderId);
        txtHotelName = (TextView) findViewById(R.id.txtHotelName);
        txtOrderOn = (TextView) findViewById(R.id.txtOrderOn);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        itemLayout = (LinearLayout) findViewById(R.id.itemLayout);
        txtTotalPrice = (TextView) findViewById(R.id.txtTotalPrice);
        txtTax = (TextView) findViewById(R.id.txtTax);
        txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        txtOrderStatus = (TextView) findViewById(R.id.txtOrderStatus);
    }
}
