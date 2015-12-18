package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ResponseHotelMenu {

    @SerializedName("ResponseMsg")
    public String responseMessage;

    @SerializedName("ResponseCode")
    public int responseCode;

    @SerializedName("lstMenuItem")
    public ArrayList<MenuItem> lstItem;

}
