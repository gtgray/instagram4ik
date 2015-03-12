package tk.atna.instagram4ik.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tk.atna.instagram4ik.AccountHelper;
import tk.atna.instagram4ik.R;


public class LoginActivity extends Activity {

    @InjectView(R.id.web_login)
    WebView webView;

    @InjectView(R.id.web_progress)
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

//        String cookie = CookieManager.getInstance().getCookie("instagram.com");
//        Log.d("myLogs", "----------- cookie: " + cookie);

        Intent intent = getIntent();
        String token = intent.getStringExtra(LoginActivity.class.getName());
        if(token != null) {
            makeWebLogout();
            return;
        }
        //
        makeWebLogin();
    }

    /**
     * Shows login form  in a web view
     */
    private void makeWebLogin() {
        initWebView(webView, new WebLoginClient(), true);
        showLoginForm();
    }

    /**
     * Just makes logout instagram server api call.
     * Showns no web view.
      */
    private void makeWebLogout() {
        WebView webView = new WebView(this);
        initWebView(webView, new WebLogoutClient(), false);
        webView.loadUrl(AccountHelper.getLogoutUrl());
    }

    /**
     * Initializes web view
     *
     * @param webView web view to initialize
     * @param client web client to load pages
     * @param withJS whether use JS on pages or not
     */
    private void initWebView(WebView webView, WebViewClient client, boolean withJS) {

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                showProgress(newProgress);
            }
        });
        webView.setWebViewClient(client);
        // usefull to show captcha at password recall page
        webView.getSettings().setJavaScriptEnabled(withJS);
    }

    /**
     * Loads instagram login form into webview
      */
    private void showLoginForm() {
        clearWebView();
        webView.loadUrl(AccountHelper.getAuthUrl(getString(R.string.client_id)));
    }

    /**
     * Flushes web view
     */
    private void clearWebView() {
        webView.loadUrl(AccountHelper.getBlank());
        webView.clearHistory();
    }

    /**
     * Shows progress bar
     *
     * @param progress new progress value
     */
    private void showProgress(int progress) {
        if(progress < 0 && progress > 100) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(progress == 100)
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            }, 500);

        progressBar.setVisibility(View.VISIBLE);
//        progressBar.setProgress(progress);
    }

    /**
     * Starts main activity and finishes login activity
     *
     * @param token instagram user access token
     */
    private void startMain(String token) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.class.getName(), token);
        startActivity(intent);
        finish();
    }

    /**
     * Web client which parses redirecting urls and seeks needed.
     * According to: https://instagram.com/developer/authentication/
     * Implicit flow.
     */
    class WebLoginClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = AccountHelper.hitTargetRedirect(url);
            // if target host hit
            if(uri != null) {
                // not error / user denied / another error
                Boolean error = AccountHelper.isUserDenied(uri);
                // not error
                if(error == null) {
                    String token = AccountHelper.parseToken(uri);
                    if (token != null)
                        startMain(token);

                    return true;

                } else if(error)
                    Toast.makeText(LoginActivity.this, "It's impossible to proceed.\n"
                            + "Permissions rejected", Toast.LENGTH_LONG).show();

                // flush web view
                showLoginForm();
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    /**
     * Web client which makes a method call on any page loaded
     */
    class WebLogoutClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            makeWebLogin();
        }
    }

}
