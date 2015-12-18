package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 04-12-2015.
 */
public class RatingResponse {

    @SerializedName("ResponseId")
    public String ResponseId;

    @SerializedName("ResponseMsg")
    public String ResponseMsg;

    @SerializedName("UserID")
    public int UserID;

}
