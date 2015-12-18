package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class MenuItem {
    @SerializedName("CategoryID")
    public int CategoryID;

    @SerializedName("CategoryObj")
    public OrderCategory CategoryObj;

    @SerializedName("CuisineId")
    public int CuisineId;

    @SerializedName("CuisineObj")
    public Cuisine CuisineObj;

    @SerializedName("HotelId")
    public int HotelId;

    @SerializedName("IsActive")
    public boolean IsActive;

    @SerializedName("IsDeleted")
    public boolean IsDeleted;

    @SerializedName("IsSpicy")
    public int IsSpicy;

    @SerializedName("ItemId")
    public int ItemId;

    @SerializedName("ItemaName")
    public String ItemaName;

    @SerializedName("MenuFolderPath")
    public String MenuFolderPath;

    @SerializedName("MenuIcon")
    public String MenuIcon;

    @SerializedName("Price")
    public String Price;

    @SerializedName("TagLine")
    public String TagLine;

    @SerializedName("VegNonveg")
    public int VegNonveg;

    @SerializedName("lstFoodDiate")
    public ArrayList<FoodDiate> lstFoodDiate;

}
