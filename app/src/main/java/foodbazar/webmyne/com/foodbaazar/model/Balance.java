package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class Balance {
    @SerializedName("UserBal")
    public float UserBal;

    @SerializedName("Userid")
    public int Userid;
}