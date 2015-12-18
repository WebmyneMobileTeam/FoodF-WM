package foodbazar.webmyne.com.foodbaazar.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Hotel {

    @SerializedName("AreaId")
    public int AreaId;
    @SerializedName("AreaName")
    public String AreaName;
    @SerializedName("City")
    public int City;
    @SerializedName("CityName")
    public String CityName;
    @SerializedName("ClosingTime")
    public String ClosingTime;
    @SerializedName("ClosingTimeStr")
    public String ClosingTimeStr;
    @SerializedName("Days")
    public String Days;
    @SerializedName("DeliveryFee")
    public String DeliveryFee;
    @SerializedName("Description")
    public String Description;
    @SerializedName("EmailId")
    public String EmailId;
    @SerializedName("FacebookLink")
    public String FacebookLink;
    @SerializedName("GoogleLink")
    public String GoogleLink;
    @SerializedName("HotelId")
    public int HotelId;
    @SerializedName("HotelName")
    public String HotelName;
    @SerializedName("IsDeleted")
    public boolean IsDeleted;
    @SerializedName("IsDelivery")
    public boolean IsDelivery;
    @SerializedName("IsPickUp")
    public boolean IsPickUp;
    @SerializedName("Logo")
    public String Logo;
    @SerializedName("LogoImagePath")
    public String LogoPath;
    @SerializedName("LogoTagLine")
    public String LogoTagLine;
    @SerializedName("MinDeliveryAmount")
    public String MinDeliveryAmount;
    @SerializedName("NearByLocation")
    public String NearByLocation;
    @SerializedName("OpeningTime")
    public String OpeningTime;
    @SerializedName("OpeningTimeStr")
    public String OpeningTimeStr;
    @SerializedName("OtherTax")
    public String OtherTax;
    @SerializedName("OwnerId")
    public int OwnerId;
    @SerializedName("PhoneNumber")
    public String PhoneNumber;
    @SerializedName("ServiceFees")
    public String ServiceFees;
    @SerializedName("ShareOfAdmin")
    public String ShareOfAdmin;
    @SerializedName("ShareOfCustomer")
    public String ShareOfCustomer;
    @SerializedName("State")
    public String State;
    @SerializedName("StateId")
    public int StateId;
    @SerializedName("StreetAddress")
    public String StreetAddress;
    @SerializedName("TermAndConditions")
    public String TermAndConditions;
    @SerializedName("TwitterLink")
    public String TwitterLink;
    @SerializedName("VatTax")
    public String VatTax;
    @SerializedName("VegNonveg")
    public int VegNonveg;
    @SerializedName("WebsiteLink")
    public String WebsiteLink;
    @SerializedName("lstCuisine")
    public ArrayList<Cuisine> lstCuisine;

    public ArrayList<Integer> getCuisinesArray() {
        ArrayList<Integer> arr = new ArrayList<>();

        for (int i = 0; i < lstCuisine.size(); i++) {
            arr.add(lstCuisine.get(i).CuisineID);
        }

        return arr;
    }

    @SerializedName("lstRR")
    public ArrayList<RatingModel> lstRR;

}
