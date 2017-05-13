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
    public static final String EXTRA_AUTHOR = "by.minskfood.author";
    public static final String EXTRA_TEXT = "by.miskfood.text";

    @BindView(R.id.text_author) EditText authorView;
    @BindView(R.id.text_review) EditText reviewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.review, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.bar_add_review);
        myActionMenuItem.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_AUTHOR, String.valueOf(authorView.getText()));
            intent.putExtra(EXTRA_TEXT, String.valueOf(reviewView.getText()));
            setResult(RESULT_OK, intent);
            finish();
            return false;
        });

        return true;
    }
}
