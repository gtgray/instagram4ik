package tk.atna.instagram4ik;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta);


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

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.content, StreamFragment.newInstance())
//                    .commit();
//        }


//        HttpHelper helper = new HttpHelper(this);
//        helper.authorize(getString(R.string.client_id), null);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {

            // TODO logout

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {



        return super.onSupportNavigateUp();
    }
}
