package foodbazar.webmyne.com.foodbaazar.helpers;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;


/**
 * @author Made by Dhruvil Patel
 *         <p>
 *         </p>
 *         This is a custom class that represents the impmementation of Volly
 *         classes
 *         <p>
 *         <li>
 *         <h6>JsonObjectRequest</h6></li> <li>
 *         <h6>JsonArrayRequest</h6></li>
 *         <p>
 *         </p>
 *         <li>You have to give two params in this class as mentioned below</li>
 *         <li>And then write obj.call() to call webservice</li>
 *         <p>
 *         <p>
 *         </p>
 *         <p>
 *         </p>
 *         * <blockquote></blockquote>
 *         <p>
 *         <code>CallWebService obj = new CallWebService() {
 * @Override public void response(String response) { // TODO Auto-generated
 * method stub
 * <p>
 * <p>
 * }
 * @Override public void error(VolleyError error) { // TODO Auto-generated
 * method stub
 * <p>
 * <p>
 * } }; <p></p>
 * <p>
 * obj.setJsonObjectRequest(true); <p></p>
 * obj.setUrl(AppConstants.SERVICE_URL); <p></p> obj.call();</code>
 */
public abstract class CallWebService implements IService {


    public abstract void response(String response);

    public abstract void error(String error);

    private String url;
    String response = null;

    JSONObject userObject;

    public static int TYPE_GET = 100;
    public static int TYPE_POST = 200;

    public static int TYPE_JSONOBJECT = 0;
    public static int TYPE_JSONARRAY = 1;
    public static int TYPE_STRING = 2;
    public int type = 0, methodType = 0;


    public CallWebService(String url, int type) {
        super();
        this.url = url;
        this.methodType = type;
    }

    public CallWebService(String url, int methodType, JSONObject userObject) {
        super();
        this.url = url;
        this.methodType = methodType;
        this.userObject = userObject;
    }


    public synchronized final CallWebService start() {
        call();
        return this;
    }

    public void call() {

        switch (methodType) {

            // case  for requesting json object, GET type
            case 100:
                JsonObjectRequest request = new JsonObjectRequest(url, null,
                        new Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jobj) {

                                try {
                                    response(jobj.toString());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    error("Server Error");
                                }

                            }
                        }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // TODO Auto-generated method stub
                        error("Server Error");
                    }
                });
                request.setRetryPolicy(
                        new DefaultRetryPolicy(
                                0,
                                0,
                                0));
                MyApplication.getInstance().addToRequestQueue(request);

                break;

            // case for requesting json object, POST Type
            case 200:

                JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.POST, url, userObject, new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jobj) {

                        try {
                            response(jobj.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                            error("Server Error");
                        }

                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError e) {

                        error("Server Error");
                    }
                });
                request2.setRetryPolicy(
                        new DefaultRetryPolicy(
                                0,
                                0,
                                0));
                MyApplication.getInstance().addToRequestQueue(request2);

                break;

            case 2:

                break;

        }

    }


}
