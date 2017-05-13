package minskfood.by.foodapp.models.place;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Place extends RealmObject {
    @PrimaryKey private String _id;

    private String description;
    private String name;
    private String prices;
    private String type;

    private Location location;
    private WorkTime workTime;

    private RealmList<Image> images;
    private RealmList<Review> reviews;
    private RealmList<Tag> tags;


    public String getId() {
        return _id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getPrices() {
        return prices;
    }

    public String getType() {
        return type;
    }

    public Location getLocation() {
        return location;
    }

    public WorkTime getWorkTime() {
        return workTime;
    }

    public RealmList<Image> getImages() {
        return images;
    }

    public RealmList<Review> getReviews() {
        return reviews;
    }

    public String getTags() {
        StringBuilder result = new StringBuilder("");
        for (Tag tag : tags) {
            result.append("#").append(tag.getTag()).append(" ");
        }
        return result.toString();
    }

    public void addReview(Review review) {
        reviews.add(review);
    }
}
