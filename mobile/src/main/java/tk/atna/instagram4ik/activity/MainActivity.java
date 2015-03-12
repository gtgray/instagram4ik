package tk.atna.instagram4ik.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayDeque;

import tk.atna.instagram4ik.ContentManager;
import tk.atna.instagram4ik.R;
import tk.atna.instagram4ik.Utils;
import tk.atna.instagram4ik.fragment.BaseFragment;
import tk.atna.instagram4ik.fragment.DetailsFragment;
import tk.atna.instagram4ik.fragment.FeedFragment;


public class MainActivity extends ActionBarActivity
                          implements BaseFragment.FragmentActionCallback,
                                     FragmentManager.OnBackStackChangedListener {

    ContentManager contentManager = ContentManager.get();

    private static final String TITLE_STACK = "title";
    private static final String BACKSTACK = "backstack";

    private ArrayDeque<String> titleStack = new ArrayDeque<>();
    private boolean backstackEmpty = true;

    ActionBar actionbar;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null) {
            titleStack = (ArrayDeque<String>) savedInstanceState.getSerializable(TITLE_STACK);
            backstackEmpty = savedInstanceState.getBoolean(BACKSTACK);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionbar = getSupportActionBar();
        actionbar.setTitle(titleStack.peek());
        actionbar.setDisplayHomeAsUpEnabled(!backstackEmpty);


        // shadow under toolbar on new devices
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View shadow = findViewById(R.id.shadow_prelollipop);
            shadow.setVisibility(View.GONE);
            toolbar.setElevation(8);
        }

        if (savedInstanceState == null) {
            Utils.parkFragment(getSupportFragmentManager(),
                    R.id.content,
                    FeedFragment.class,
                    null, false);
            titleStack.push(getString(FeedFragment.TITLE));
            actionbar.setTitle(titleStack.peek());
        }

        Intent intent = getIntent();
        String token = intent.getStringExtra(MainActivity.class.getName());
        contentManager.rememberToken(token);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // user is still signed in check
        if(contentManager.getToken() == null)
            shutdown();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(TITLE_STACK, titleStack);
        outState.putBoolean(BACKSTACK, backstackEmpty);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return backstackEmpty && super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // log out action
        if (item.getItemId() == R.id.action_logout) {
            shutdown();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAction(int action, Bundle data) {
        switch (action) {
            case BaseFragment.ACTION_FINISH:
                onBackPressed();
                break;

            case BaseFragment.ACTION_MEDIA_DETAILS:
                Utils.parkFragment(getSupportFragmentManager(),
                                    R.id.content,
                                    DetailsFragment.class,
                                    data, true);
                titleStack.push(getString(DetailsFragment.TITLE));
                actionbar.setTitle(titleStack.peek());
                break;
        }
    }

    @Override
    public void onBackStackChanged() {
        backstackEmpty = getSupportFragmentManager().getBackStackEntryCount() == 0;
        // show back arrow
        actionbar.setDisplayHomeAsUpEnabled(!backstackEmpty);
        // refresh menu items
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().popBackStackImmediate()) {
            titleStack.pop();
            actionbar.setTitle(titleStack.peek());
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return super.onSupportNavigateUp();
    }

    /**
     * Finishes main activity and returns back to login activity
     */
    private void shutdown() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.class.getName(), contentManager.getToken());
        startActivity(intent);
        finish();
    }

}
