package io.zentechgh.dms.mobile.app.ui.user;

import android.annotation.SuppressLint;
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
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // getting reference to views
        document_viewer = findViewById(R.id.document_viewer);


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
    @SuppressLint("SetJavaScriptEnabled")
    private void displayDocumentDetails(){

        // checks if imageUrl is not null
        if(documentUrl != null) {

            // sets visibility to visible
            progressBar.setVisibility(View.VISIBLE);

            //String url = Uri.encode(documentUrl);
            // displaying document in webview
            String url = Uri.encode(documentUrl);

            //document_viewer.setWebViewClient(new AppWebViewClients());
            document_viewer.getSettings().setJavaScriptEnabled(true);
            document_viewer.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
            document_viewer.getSettings().setBuiltInZoomControls(true);
            document_viewer.getSettings().setUseWideViewPort(true);
            //document_viewer.getSettings().setPluginState(WebSettings.PluginState.ON);
            // loads documentUrl into webView
            document_viewer.loadUrl("http://docs.google.com/gview?embedded=true&url="+url);

            document_viewer.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                    super.onPageStarted(view, url, favicon);
                    // sets visibility to visible
                    progressBar.setVisibility(View.VISIBLE);

                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    // do your stuff here
                    // sets visibility of progressBar to gone
                    progressBar.setVisibility(View.GONE);

                    // sets visibility of webView to visible
                    document_viewer.setVisibility(View.VISIBLE);

                    // setting the details of document on text Views
                    title.setText(" Title : " + documentTitle);
                    tag.setText(" Tag : " + documentTag);
                    type.setText(" Type : " + documentType);
                    comment.setText(" Comment : " + documentComment);
                    distributee.setText(" Distributee : "  + documentDistributee);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    view.loadUrl("about:blank");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }

            });


        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // finish
        finish();
    }
}
