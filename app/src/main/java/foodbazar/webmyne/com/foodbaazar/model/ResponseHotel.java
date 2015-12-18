package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ResponseHotel {

    @SerializedName("ResponseMsg")
    public String responseMessage;
    @SerializedName("ResponseCode")
    public int responseCode;
    @SerializedName("lstCuisine")
    public ArrayList<Cuisine> lstCuisine;
    @SerializedName("HotelList")
    public ArrayList<Hotel> HotelList;

}
