package com.dadn.sgardens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

import static com.dadn.sgardens.component.ProductGridFragment.EXTRA_SUBTITLE;
import static com.dadn.sgardens.component.ProductGridFragment.EXTRA_TITLE;
import static com.dadn.sgardens.component.ProductGridFragment.EXTRA_URL;


public class DetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String imageUrl;
    private String productName;
    private String subTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        initActivity();

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(productName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initActivity() {
        Intent intent = getIntent();
        imageUrl = intent.getStringExtra(EXTRA_URL);
        productName = intent.getStringExtra(EXTRA_TITLE);
        subTitle = intent.getStringExtra(EXTRA_SUBTITLE);

        ImageView imageView = findViewById(R.id.image_view_detail);
        TextView productNameView = findViewById(R.id.text_view_creator_detail);
        TextView textViewLikes = findViewById(R.id.text_view_like_detail);

        Picasso.with(this).load(imageUrl).fit().centerInside().into(imageView);
        productNameView.setText(productName);
        textViewLikes.setText(subTitle);
    }
}
