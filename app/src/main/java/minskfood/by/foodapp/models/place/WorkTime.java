package minskfood.by.foodapp.models.place;

import io.realm.RealmObject;


public class WorkTime extends RealmObject {
    private Time open;
    private Time close;

    public String getTime() {
        return String.valueOf(open.getHours()) + ":"
                + String.valueOf(open.getMinutes()) + " - "
                + String.valueOf(close.getHours()) + ":"
                + String.valueOf(close.getMinutes());
    }
}
