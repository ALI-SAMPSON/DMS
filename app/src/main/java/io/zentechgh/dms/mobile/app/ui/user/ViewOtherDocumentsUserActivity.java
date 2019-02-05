package io.zentechgh.dms.mobile.app.ui.user;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import io.zentechgh.dms.mobile.app.R;
import io.zentechgh.dms.mobile.app.helper.AppWebViewClients;

public class ViewOtherDocumentsUserActivity extends AppCompatActivity {

    // creating variable of type Toolbar
    Toolbar toolbar;

    TextView toolbar_title;

    // creating variables of types ImageView and TextView
    WebView document_viewer;
    TextView title;
    TextView tag;
    TextView type;
    TextView comment;
    TextView distributee;

    ProgressBar progressBar;

    // variables to getStringExtra(strings passed from recyclerViewManage)
    String documentUrl,documentTitle,documentTag, documentType, documentComment, documentDistributee, documentSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other_documents_user);

        // getting reference to views
        toolbar = findViewById(R.id.toolbar);
        toolbar_title =  findViewById(R.id.toolbar_title);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // getting reference to views
        document_viewer = findViewById(R.id.file_viewer);


        title =  findViewById(R.id.tv_title);
        tag = findViewById(R.id.tv_tag);
        type = findViewById(R.id.tv_type);
        comment = findViewById(R.id.tv_comment);
        distributee = findViewById(R.id.tv_distributee);

        progressBar = findViewById(R.id.progressBar);

        documentUrl = getIntent().getStringExtra("document_url");
        documentTitle = getIntent().getStringExtra("document_title");
        documentTag = getIntent().getStringExtra("document_tag");
        documentType = getIntent().getStringExtra("document_type");
        documentComment = getIntent().getStringExtra("document_comment");
        documentDistributee = getIntent().getStringExtra("document_distributee");
        documentSender = getIntent().getStringExtra("document_sender");

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

                    // setting the details of document on text Views
                    title.setText(" Title : " + documentTitle);
                    tag.setText(" Tag : " + documentTag);
                    type.setText(" Type : " + documentType);
                    comment.setText(" Comment : " + documentComment);
                    distributee.setText(" Distributee : "  + documentDistributee);

                    //String url = Uri.encode(documentUrl);

                    String url = Uri.encode(documentUrl);
                    String finalUrl = "http://docs.google.com/viewer?url=" + url + "&embedded=true";

                    document_viewer.setWebViewClient(new Callback());
                    document_viewer.getSettings().setJavaScriptEnabled(true);
                    document_viewer.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
                    document_viewer.getSettings().setBuiltInZoomControls(true);
                    //file_viewer.getSettings().setLoadWithOverviewMode(true);
                    //file_viewer.getSettings().setUseWideViewPort(true);
                    document_viewer.getSettings().setPluginState(WebSettings.PluginState.ON);
                    // loads documentUrl into webView
                    document_viewer.loadUrl("http://docs.google.com/gview?embedded=true&url=" + finalUrl);


                }
            }, 3000);

        }

    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return(false);
        }
    }

    private void webClient(){
        document_viewer.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings().setLoadsImagesAutomatically(true);
                document_viewer.setVisibility(View.VISIBLE);
                //progressView.setVisibility(View.VISIBLE);

                if (progressBar != null && progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }

                Log.v("after load", view.getUrl());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
                Log.e("error", description);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // finish
        finish();
    }
}
