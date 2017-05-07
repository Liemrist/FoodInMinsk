package minskfood.by.foodapp.models.place;

import io.realm.RealmObject;

public class WorkTime extends RealmObject {
    private Time open;
    private Time close;

    public Time getOpen() {
        return open;
    }

    public Time getClose() {
        return close;
    }

    public String getTimeString() {
        String result = "";
        result += String.valueOf(open.getHours()) + ":";
        result += String.valueOf(open.getMinutes());
        result += " - ";
        result += String.valueOf(close.getHours()) + ":";
        result += String.valueOf(close.getMinutes());
        return result;
    }
}
