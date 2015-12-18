package foodbazar.webmyne.com.foodbaazar.helpers;

import com.lacronicus.easydatastorelib.BooleanEntry;
import com.lacronicus.easydatastorelib.IntEntry;
import com.lacronicus.easydatastorelib.ObjectEntry;
import com.lacronicus.easydatastorelib.Preference;
import com.lacronicus.easydatastorelib.StringEntry;

import foodbazar.webmyne.com.foodbaazar.model.Hotel;


public interface ApplicationPrefs {

    @Preference("isLoggedIn")
    BooleanEntry isLoggedIn();

    @Preference("isLocationSet")
    BooleanEntry isLocationSet();

    @Preference("selectedAreaName")
    StringEntry selectedAreaName();

    @Preference("selectedAreaId")
    StringEntry selectedAreaId();

    @Preference("selectedAreaPosition")
    IntEntry selectedAreaPosition();

    @Preference("currentHotel")
    ObjectEntry<Hotel> currentHotel();


}
