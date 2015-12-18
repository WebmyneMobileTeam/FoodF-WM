package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class OrderMenuItem {
    @SerializedName("CategoryID")
    public int CategoryID;

    @SerializedName("CategoryObj")
    public OrderCategory category;

    @SerializedName("CuisineId")
    public int CuisineId;

    @SerializedName("CuisineObj")
    public Cuisine cuisine;

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
    public float Price;

    @SerializedName("TagLine")
    public String TagLine;

    @SerializedName("VegNonveg")
    public int VegNonveg;

    @SerializedName("lstFoodDiate")
    public ArrayList<FoodDiate> foodDiates;

}
