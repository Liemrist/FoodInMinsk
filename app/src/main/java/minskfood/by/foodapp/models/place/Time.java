package minskfood.by.foodapp.models.place;

import io.realm.RealmObject;

public class Time extends RealmObject {
    private int hours;
    private int minutes;

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }
}
