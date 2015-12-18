package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 03-12-2015.
 */
public class HotelModel {

    @SerializedName("HotelName")
    public String HotelName;

    @SerializedName("IsDelivery")
    public boolean IsDelivery;

    @SerializedName("IsPickUp")
    public boolean IsPickUp;

    @SerializedName("Timings")
    public ArrayList<Timing> Timings;

    @SerializedName("StreetAddress")
    public String StreetAddress;

    @SerializedName("ServiceFees")
    public double ServiceFees;

    @SerializedName("lstRR")
    public ArrayList<RatingModel> lstRR;

    @SerializedName("VegNonveg")
    public int VegNonveg;
}
