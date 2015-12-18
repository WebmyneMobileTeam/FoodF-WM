package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class ResponseHotelMenuNew {

    @SerializedName("ResponseMsg")
    public String responseMessage;

    @SerializedName("ResponseId")
    public int responseId;

    @SerializedName("Menus")
    public ArrayList<MenuItemNew> Menus;

    @SerializedName("HotelDetail")
    public ArrayList<HotelModel> HotelDetail;

}
