package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Android on 27-04-2015.
 */
public class City {

    @SerializedName("cityId")
    public int cityId;
    @SerializedName("stateId")
    public  int stateId;
    @SerializedName("cityName")
    public String cityName;
    @SerializedName("lstAre")
    public ArrayList<Area> areaListArrayList;
}
