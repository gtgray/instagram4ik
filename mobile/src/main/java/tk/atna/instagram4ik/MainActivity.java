package tk.atna.instagram4ik;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    ContentManager contentManager = ContentManager.get();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDefaultDisplayHomeAsUpEnabled(true);
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setHomeButtonEnabled(true);




        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View shadow = findViewById(R.id.shadow_prelollipop);
            shadow.setVisibility(View.GONE);
            toolbar.setElevation(8);
        }

        if (savedInstanceState == null) {
            Utils.parkFragment(getSupportFragmentManager(),
                               R.id.content,
                               FeedFragment.class,
                               true, false);
        }

        Intent intent = getIntent();
        String token = intent.getStringExtra(MainActivity.class.getName());
        contentManager.rememberToken(token);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(contentManager.getToken() == null)
            shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // user decided to log out
        if (item.getItemId() == R.id.action_logout) {
            shutdown();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {



        return super.onSupportNavigateUp();
    }

    private void shutdown() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.class.getName(), contentManager.getToken());
        startActivity(intent);
        finish();
    }
}
