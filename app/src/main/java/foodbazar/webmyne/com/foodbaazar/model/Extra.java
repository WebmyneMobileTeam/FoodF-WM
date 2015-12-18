package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 17-11-2015.
 */
public class Extra {
    @SerializedName("ExtraID")
    public int ExtraID;

    @SerializedName("ExtraItemID")
    public int ExtraItemID;

    @SerializedName("ItemID")
    public int ItemID;

    @SerializedName("ExtraName")
    public String ExtraName;

    @SerializedName("Price")
    public int Price;
}
