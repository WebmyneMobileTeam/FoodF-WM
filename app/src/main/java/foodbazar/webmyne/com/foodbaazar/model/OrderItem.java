package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class OrderItem {
    @SerializedName("ItemName")
    public String ItemName;

    @SerializedName("OrderItemId")
    public int OrderItemId;

    @SerializedName("Price")
    public int Price;

    @SerializedName("Quantity")
    public int Quantity;

    @SerializedName("TagLine")
    public String TagLine;

    @SerializedName("Typename")
    public String Typename;

    @SerializedName("VegNonVeg")
    public int VegNonVeg;

    @SerializedName("isSpicy")
    public int isSpicy;

    @SerializedName("Extras")
    public ArrayList<Extra> Extras;

}
