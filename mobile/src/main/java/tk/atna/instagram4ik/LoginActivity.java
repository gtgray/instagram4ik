package tk.atna.instagram4ik;

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

        makeWebLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        clearWebView();
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
            return;
        }

        super.onBackPressed();
    }

    private void makeWebLogin() {

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                showProgress(newProgress);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = HttpHelper.parseRedirect(url);
                // parsed
                if(uri != null) {
                    // not error / user denied / another error
                    Boolean error = HttpHelper.isUserDenied(uri);
                    // not error
                    if(error == null) {
                        String token = HttpHelper.parseToken(uri);
                        if (token != null)
                            startMain(token);

                        return true;

                    } else if(error)
                        Toast.makeText(LoginActivity.this, "Impossible to run further.\n"
                                + "Permissions rejected", Toast.LENGTH_LONG).show();

                    // flush web view
                    refreshWebView();
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        refreshWebView();
    }

    private void refreshWebView() {
        clearWebView();
        webView.loadUrl(HttpHelper.getAuthUrl(getString(R.string.client_id)));
    }

    private void clearWebView() {
        webView.loadUrl(HttpHelper.getBlank());
        webView.clearHistory();
    }

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
        progressBar.setProgress(progress);
    }

    private void startMain(String token) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.class.getName(), token);
        startActivity(intent);
    }

}
