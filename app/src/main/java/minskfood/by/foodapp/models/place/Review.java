package minskfood.by.foodapp.models.place;

import io.realm.RealmObject;

public class Review extends RealmObject {
    private String author;
    private String text;

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
