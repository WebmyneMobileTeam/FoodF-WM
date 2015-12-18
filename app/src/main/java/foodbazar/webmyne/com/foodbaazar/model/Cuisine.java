package foodbazar.webmyne.com.foodbaazar.model;


import com.google.gson.annotations.SerializedName;

public class Cuisine {

    @SerializedName("CuisineID")
    public int CuisineID;

    @SerializedName("CuisineName")
    public String CuisineName;

    @SerializedName("IsActive")
    public boolean IsActive;

    @SerializedName("IsDelete")
    public boolean IsDelete;

}
