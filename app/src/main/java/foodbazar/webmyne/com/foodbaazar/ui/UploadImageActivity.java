package foodbazar.webmyne.com.foodbaazar.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.aspsine.multithreaddownload.CallBack;
import com.aspsine.multithreaddownload.DownloadException;
import com.aspsine.multithreaddownload.DownloadManager;
import com.aspsine.multithreaddownload.DownloadRequest;

import java.io.File;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;

public class UploadImageActivity extends AppCompatActivity {

    File SDCardRoot, dir;
    String type = null;
    String filename;
    boolean download = false;
    String sheetURL = "http://ws-srv-net.in.webmyne.com/Applications/FoodBaazarV2/UserDetail/DownloadPDFandroid/d44f693d-185b-476c-aac8-3e169b8899cd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        SDCardRoot = Environment.getExternalStorageDirectory();
        dir = new File(SDCardRoot.getAbsolutePath()
                + "/FoodFAD Orders");
        if (!dir.exists()) {
            dir.mkdir();
        }

        new DownloadSheet().execute();


       /* WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://ws-srv-net.in.webmyne.com/Applications/FoodBaazarV2/UserDetail/DownloadPDFandroid/d44f693d-185b-476c-aac8-3e169b8899cd");*/
    }

    private void downloadPDF() {
        final DownloadRequest request = new DownloadRequest.Builder()
                .setTitle("MyOrder" + ".pdf")
                .setUri(sheetURL)
                .setFolder(dir)
                .build();

// download:
// the tag here, you can simply use download uri as your tag;
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

            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onDownloadPaused() {

            }

            @Override
            public void onDownloadCanceled() {

            }

            @Override
            public void onFailed(DownloadException e) {

            }
        });
    }

    private class DownloadSheet extends AsyncTask<String, String, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = ProgressDialog.show(UploadImageActivity.this, "Downloading order invoce.", "In Progress", false);
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
}
