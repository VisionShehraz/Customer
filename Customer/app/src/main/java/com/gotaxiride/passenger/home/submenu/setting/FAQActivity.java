package com.gotaxiride.passenger.home.submenu.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ServiceGenerator;

public class FAQActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar mProgressBar;
    private static final String url = ServiceGenerator.FAQ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);



        webView = (WebView) findViewById(R.id.webView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        // Tiga baris di bawah ini agar laman yang dimuat dapat melakukan zoom.
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl(url);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //ProgressBar akan Terlihat saat Halaman Dimuat
                mProgressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);

            }
        });
        webView.setWebViewClient(new WebViewClient(){

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }


            public void onPageFinished(WebView view, String url) {
                //ProgressBar akan Menghilang setelah Halaman Selesai Dimuat
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onBackPressed () {

        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    //@Override
    //public void onBackPressed(){
    //    Intent home = new Intent(Tiketpergi.this, MainActivity.class);
    //     startActivity(home);

    //}
}