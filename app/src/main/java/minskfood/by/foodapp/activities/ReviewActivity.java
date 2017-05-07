package minskfood.by.foodapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import minskfood.by.foodapp.R;

public class ReviewActivity extends AppCompatActivity {
    public static final String EXTRA_REVIEW_DATA = "extraBirthData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(v -> {
            switch (v.getId()) {
//                case R.id.button_birth_back:
//                    setResult(RESULT_CANCELED);
//                    finish();
//                    break;
                case R.id.button:
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_REVIEW_DATA, "DATA");
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        });
    }
}

// todo: bind SOAP somewhere.
//        Button btnSoap = (Button) findViewById(R.id.button3);
//        btnSoap.setOnClickListener(v -> {
//            StrictMode.ThreadPolicy policy = new StrictMode
//                    .ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            String s = new CustomSoap().GetInteger();
//            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
//        });
//new GetPlaces(this).execute(URL_PLACES);
