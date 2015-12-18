package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class OrderCategory {
    @SerializedName("CategoryFolderPath")
    public String CategoryFolderPath;

    @SerializedName("CategoryIcon")
    public String CategoryIcon;

    @SerializedName("CategoryId")
    public int CategoryId;

    @SerializedName("CategoryName")
    public String CategoryName;

    @SerializedName("CreatedBy")
    public int CreatedBy;

    @SerializedName("Createdatetime")
    public String Createdatetime;

    @SerializedName("Description")
    public String Description;

    @SerializedName("HotelId")
    public int HotelId;

    @SerializedName("IsActive")
    public boolean IsActive;

    @SerializedName("ParentCategoryID")
    public int ParentCategoryID;

    @SerializedName("Restaurant")
    public String Restaurant;

    @SerializedName("UpdatedBy")
    public int UpdatedBy;
}
