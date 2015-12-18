package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 05-11-2015.
 */
public class UserProfile {
    @SerializedName("Address")
    public String Address;

    @SerializedName("City")
    public int CityId;

    @SerializedName("CityName")
    public String CityName;

    @SerializedName("Country")
    public int Country;

    @SerializedName("DOB")
    public String DOB;

    @SerializedName("DeviceId")
    public String DeviceId;

    @SerializedName("DeviceType")
    public String DeviceType;

    @SerializedName("EmailId")
    public String EmailId;

    @SerializedName("FirstName")
    public String FirstName;

    @SerializedName("IsActive")
    public boolean IsActive;

    @SerializedName("IsDeleted")
    public boolean IsDeleted;

    @SerializedName("LastName")
    public String LastName;

    @SerializedName("LoginType")
    public int LoginType;

    @SerializedName("MobileNo")
    public String MobileNo;

    @SerializedName("Password")
    public String Password;

    @SerializedName("ProfilePic")
    public String ProfilePic;

    @SerializedName("ProfilePicFolderName")
    public String ProfilePicFolderName;

    @SerializedName("ResponseId")
    public int ResponseId;

    @SerializedName("ResponseMsg")
    public String ResponseMsg;

    @SerializedName("RoleId")
    public int RoleId;

    @SerializedName("State")
    public int State;

    @SerializedName("StateName")
    public String StateName;

    @SerializedName("UpdateBy")
    public int UpdateBy;

    @SerializedName("UserId")
    public int UserId;

    @SerializedName("Zip")
    public String Zip;
}
