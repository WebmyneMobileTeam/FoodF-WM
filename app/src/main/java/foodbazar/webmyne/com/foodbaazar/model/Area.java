package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 27-04-2015.
 */
public class Area {


    @SerializedName("AreaActive")
    public boolean AreaActive;
    @SerializedName("AreaId")
    public int AreaId;
    @SerializedName("AreaName")
    public String AreaName;
    @SerializedName("cityId")
    public int cityId;
}
