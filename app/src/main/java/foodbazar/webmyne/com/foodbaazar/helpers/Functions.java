package foodbazar.webmyne.com.foodbaazar.helpers;

/**
 * @author jatin
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import foodbazar.webmyne.com.foodbaazar.R;

public class Functions {

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static void toast(Context ctx, String msg) {
        Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.parseColor("#252525"));
        v.setTextSize(18);
        v.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        toast.getView().setBackgroundColor(Color.parseColor("#C5D631"));
        toast.show();

    }

    public static void fireIntent(Activity activity, Class cls) {
        Intent i = new Intent(activity, cls);
        activity.startActivity(i);

    }

    public static boolean emailValidation(String email) {
        boolean validEmailAddress = true;
        if (email.length() == 0) {
            validEmailAddress = false;
        } else {
            if (!email.contains(".") || !email.contains("@")) {
                validEmailAddress = false;
            } else {
                int index1 = email.indexOf("@");
                String subStringType = email.substring(index1);
                int index2 = index1 + subStringType.indexOf(".");
                if (index1 == 0 || index2 == 0) {
                    validEmailAddress = false;
                } else {
                    String typeOf = email.substring(index1, index2);
                    if (typeOf.length() < 1) {
                        validEmailAddress = false;
                    }
                    String typeOf2 = email.substring(index2);
                    if (typeOf2.length() < 2) {
                        validEmailAddress = false;
                    }
                }

            }
        }

        return validEmailAddress;
    }

    public static boolean isConnecting(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String parseDate(String inputDate, String inputPattern, String outputPattern) {

        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(inputDate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void snack(View v, String msg) {
        Snackbar snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);
        TextView tv = (TextView) snackbar.getView().findViewById(R.id.snackbar_text);
        tv.setTextColor(Color.parseColor("#C5D631"));
        tv.setTextSize(16);
        tv.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        View view = snackbar.getView();
        view.setBackgroundColor(Color.parseColor("#252525"));
        snackbar.show();
    }

    public static int getLength(EditText editText) {
        return editText.getText().toString().trim().length();
    }

    public static String toStr(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static boolean isBlank(EditText editText) {
        if (editText.getText().toString().trim().length() == 0)
            return true;
        else
            return false;
    }

    public static String returnBas64Image(Bitmap thumbnail) {
        //complete code to save image on server
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 90, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap getBitmap(String path) {
        Bitmap bmp = null;
        try {
            URL url = new URL(path);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bmp;
    }

    public static String concatValues(ArrayList<Integer> unique) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < unique.size(); i++) {
            sb.append(unique.get(i));
        }
        return sb.toString();
    }

}