package minskfood.by.foodapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;


import butterknife.BindView;
import butterknife.ButterKnife;
import minskfood.by.foodapp.R;


public class ReviewActivity extends AppCompatActivity {
    public static final String EXTRA_REVIEW_AUTHOR = "extraReviewAuthor";
    public static final String EXTRA_REVIEW_TEXT = "extraReviewText";

    @BindView(R.id.text_author) EditText authorView;
    @BindView(R.id.text_review) EditText reviewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.review, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.bar_add_review);
        myActionMenuItem.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent();

            String author = String.valueOf(authorView.getText());
            String review = String.valueOf(reviewView.getText());

            intent.putExtra(EXTRA_REVIEW_AUTHOR, author);
            intent.putExtra(EXTRA_REVIEW_TEXT, review);

            setResult(RESULT_OK, intent);
            finish();
            return false;
        });

        return true;
    }
}
