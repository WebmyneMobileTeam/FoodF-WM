package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class UserOrder {
    @SerializedName("CustomerFirstName")
    public String CustomerFirstName;

    @SerializedName("CustomerLastName")
    public String CustomerLastName;

    @SerializedName("DeliveryArea")
    public String DeliveryArea;

    @SerializedName("DeliveryCity")
    public int DeliveryCityId;

    @SerializedName("DeliveryCityName")
    public String DeliveryCityName;

    @SerializedName("DeliveryCountry")
    public int DeliveryCountry;

    @SerializedName("DeliveryCountryName")
    public String DeliveryCountryName;

    @SerializedName("DeliveryState")
    public int DeliveryState;

    @SerializedName("DeliveryStateName")
    public String DeliveryStateName;

    @SerializedName("PostCode")
    public int PostCode;

    @SerializedName("Userid")
    public int Userid;

    @SerializedName("CreatedOn")
    public String CreatedOn;

    @SerializedName("CreatedOnString")
    public String CreatedOnString;

    @SerializedName("DelivaryAmount")
    public float DelivaryAmount;

    @SerializedName("DelivaryType")
    public boolean DelivaryType;

    @SerializedName("DiscountPercent")
    public float DiscountPercent;

    @SerializedName("DiscountPrice")
    public float DiscountPrice;

    @SerializedName("HotelId")
    public int HotelId;

    @SerializedName("HotelName")
    public String HotelName;

    @SerializedName("OrderBy")
    public int OrderBy;

    @SerializedName("OrderDesc")
    public String OrderDesc;

    @SerializedName("OrderId")
    public String OrderId;

    @SerializedName("OrderStatus")
    public int OrderStatus;

    @SerializedName("PaymentTypeId")
    public int PaymentTypeId;

    @SerializedName("PriceToPay")
    public float PriceToPay;

    @SerializedName("Tax")
    public float Tax;

    @SerializedName("TotalPrice")
    public float TotalPrice;

    @SerializedName("TotalQty")
    public int TotalQty;

    @SerializedName("UpdatedOn")
    public String UpdatedOn;

    @SerializedName("lstMenuItem")
    public ArrayList<OrderMenuItem> lstMenuItem;

    @SerializedName("lstOrderItem")
    public ArrayList<OrderItem> lstOrderItem;

    @SerializedName("preorder")
    public String preorder;

    @SerializedName("Rating")
    public String Rating;
}
