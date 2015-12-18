package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 17-11-2015.
 */
public class MenuTypeItemstrans {

    @SerializedName("Extras")
    public ArrayList<Extra> Extras;

    @SerializedName("IsDeleted")
    public boolean IsDeleted;

    @SerializedName("ItemID")
    public int ItemID;

    @SerializedName("Price")
    public int Price;

    @SerializedName("PriceID")
    public int PriceID;

    @SerializedName("TypeID")
    public int TypeID;

    @SerializedName("TypeName")
    public String TypeName;
}
