package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class FoodDiate {
    @SerializedName("DietId")
    public int DietId;

    @SerializedName("DietName")
    public String DietName;

    @SerializedName("IsActive")
    public boolean IsActive;
}
