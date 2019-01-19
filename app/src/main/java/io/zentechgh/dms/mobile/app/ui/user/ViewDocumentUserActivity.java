package io.zentechgh.dms.mobile.app.ui.user;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import io.zentechgh.dms.mobile.app.R;

public class ViewDocumentUserActivity extends AppCompatActivity {

    // creating variable of type Toolbar
    Toolbar toolbar;

    TextView toolbar_title;

    // creating variables of types ImageView and TextView
    ImageView document;
    TextView title;
    TextView tag;
    TextView comment;

    ProgressBar progressBar;

    // variables to getStringExtra(strings passed from recyclerViewManage)
    String documentUrl,documentTitle,documentTag, documentComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // sets fullscreen for activity
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_view_document_user);


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
        document = findViewById(R.id.document_file);
        title =  findViewById(R.id.tv_title);
        tag = findViewById(R.id.tv_tag);
        comment = findViewById(R.id.tv_comment);

        progressBar = findViewById(R.id.progressBar);

        documentUrl = getIntent().getStringExtra("file");
        documentTitle = getIntent().getStringExtra("title");
        documentTag = getIntent().getStringExtra("tag");
        documentComment = getIntent().getStringExtra("comment");

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
                    Glide.with(ViewDocumentUserActivity.this).load(documentUrl).into(document);

                    // setting text to the textViews
                    title.setText(" Title : " + documentTitle);

                    tag.setText(" Tag : " + documentTag);

                    comment.setText(" Comment : " + documentComment);


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