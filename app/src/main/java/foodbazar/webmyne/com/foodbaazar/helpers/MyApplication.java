package foodbazar.webmyne.com.foodbaazar.helpers;

import android.app.Application;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.aspsine.multithreaddownload.DownloadConfiguration;
import com.aspsine.multithreaddownload.DownloadManager;

import foodbazar.webmyne.com.foodbaazar.icenet.IceNet;
import foodbazar.webmyne.com.foodbaazar.icenet.IceNetConfig;


/**
 * Created by dhruvil on 04-09-2015.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    /**
     * Log or request TAG
     */
    public static final String TAG = "VolleyPatterns";

    /**
     * Global request queue for Volley
     */
    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

       IceNetConfig config = new IceNetConfig.Builder()
                .setBaseUrl("http://ws.foodfad.in/")
                .setContext(getApplicationContext())
                .build();

     /*   IceNetConfig config = new IceNetConfig.Builder()
                .setBaseUrl("http://ws-srv-net.in.webmyne.com/applications/FoodBaazarWS_v2/")
                .setContext(getApplicationContext())
                .build();*/

        IceNet.init(config);

        DatabaseHandler handler = new DatabaseHandler(getApplicationContext());
        try {
            handler.createDataBase();
        } catch (Exception e) {

        }

        DownloadConfiguration configuration = new DownloadConfiguration();
        configuration.setMaxThreadNum(10);
        configuration.setThreadNum(3);
        DownloadManager.getInstance().init(getApplicationContext(), configuration);
    }

    public static synchronized MyApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);

    }

    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }
}
