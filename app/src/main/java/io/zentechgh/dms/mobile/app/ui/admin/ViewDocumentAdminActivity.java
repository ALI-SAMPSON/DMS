package io.zentechgh.dms.mobile.app.ui.admin;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.ui.user.ViewDocumentUserActivity;

public class ViewDocumentAdminActivity extends AppCompatActivity {

    // creating variable of type Toolbar
    Toolbar toolbar;

    TextView toolbar_title;

    // creating variables of types ImageView and TextView
    ImageView document_image;

    ProgressBar progressBar;

    // variables to getStringExtra(strings passed from recyclerViewManage)
    String documentUrl,documentTitle,documentTag, documentComment, documentDistributee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document_admin);

        // getting reference to views
        toolbar = findViewById(R.id.toolbar);
        toolbar_title =  findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // getting reference to views
        document_image = findViewById(R.id.document_file);

        progressBar = findViewById(R.id.progressBar);

        documentUrl = getIntent().getStringExtra("document_url");
        documentTitle = getIntent().getStringExtra("document_title");
        documentTag = getIntent().getStringExtra("document_tag");
        documentComment = getIntent().getStringExtra("document_comment");
        documentDistributee = getIntent().getStringExtra("document_distributee");

        // method call to display document details
        displayDocumentDetails();

    }


    // method to display document Details
    private void displayDocumentDetails(){

        // checks if imageUrl is not null
        if(documentUrl != null) {

            // sets visibility to visible
            progressBar.setVisibility(View.VISIBLE);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    // sets visibility to gone
                    progressBar.setVisibility(View.GONE);

                    // loads documentUrl into imageView
                    Glide.with(ViewDocumentAdminActivity.this).load(documentUrl).into(document_image);


                }
            }, 3000);

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // finish
        finish();
    }

}
