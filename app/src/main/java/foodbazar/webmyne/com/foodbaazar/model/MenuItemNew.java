package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class MenuItemNew {

    @SerializedName("CateGoryName")
    public String CateGoryName;

    @SerializedName("CategoryID")
    public int CategoryID;

    @SerializedName("CreatedOn")
    public String CreatedOn;

    @SerializedName("Cuisine")
    public String Cuisine;

    @SerializedName("CuisineID")
    public int CuisineID;

    @SerializedName("IsActive")
    public boolean IsActive;

    @SerializedName("IsCombo")
    public boolean IsCombo;

    @SerializedName("IsSpicy")
    public int IsSpicy;

    @SerializedName("ItemID")
    public int ItemId;

    @SerializedName("ItemName")
    public String ItemName;

    @SerializedName("MenuIconFolderPath")
    public String MenuIconFolderPath;

    @SerializedName("MenuIcon")
    public String MenuIcon;

    @SerializedName("RestaurantID")
    public int RestaurantID;

    @SerializedName("RestaurantName")
    public String RestaurantName;

    @SerializedName("TagLine")
    public String TagLine;

    @SerializedName("OtherTax")
    public double Tax;

    @SerializedName("UpdatedOn")
    public String UpdatedOn;

    @SerializedName("VegNonveg")
    public int VegNonveg;

    @SerializedName("lstMenuTypeItemstrans")
    public ArrayList<MenuTypeItemstrans> lstMenuTypeItemstrans;

}
