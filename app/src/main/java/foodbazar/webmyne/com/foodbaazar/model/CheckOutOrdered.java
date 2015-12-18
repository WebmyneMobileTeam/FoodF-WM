package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class CheckOutOrdered {
    @SerializedName("OrderConfirmId")
    public int OrderConfirmId;

    @SerializedName("ResponseCode")
    public int ResponseCode;

    @SerializedName("ResponseMsg")
    public String ResponseMsg;
}
