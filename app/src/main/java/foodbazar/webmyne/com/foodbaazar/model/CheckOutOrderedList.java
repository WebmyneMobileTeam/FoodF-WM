package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class CheckOutOrderedList {
    @SerializedName("ResponseId")
    public int ResponseId;

    @SerializedName("ResponseMsg")
    public String ResponseMsg;

    @SerializedName("Orders")
    public ArrayList<UserOrder> lstUSerOrderes;
}
