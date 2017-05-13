package minskfood.by.foodapp.models.place;

import io.realm.RealmObject;


public class Location extends RealmObject {
    private String address;
    private String district;

    public String getAddress() {
        return address;
    }

    public String getDistrict() {
        return district;
    }
}
