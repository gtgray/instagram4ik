package tk.atna.instagram4ik;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

/**
 * Class that contains instrumental methods
 */
public class Utils {


    public static final String FULL_TIMESTAMP_FORMAT = "yyyy-MM-dd kk:mm:ss";
    public static final String SIMPLE_TIMESTAMP_FORMAT = "yyyy-MM-dd";

    /**
     * First tries to find fragment by tag in fragment manager.
     * Otherwise, creates it or initializes (with data or not, optional).
     * Sets retainance. Loads into container with tag.
     *
     * @param fm link to fragment manager
     * @param container resource to park fragment to
     * @param clazz type of fragment to park
     * @param inited if fragment is needed to be initialized
     * @param backstacked if fragment is needed to be added to BackStack
     * @param <T> child of Fragment class
     */
    public static <T extends Fragment> T parkFragment(FragmentManager fm, int container,
                                                         Class<T> clazz, boolean inited,
                                                         boolean backstacked) {
        Fragment fragment = findFragment(fm, clazz);
        if (fragment == null) {
            fragment = inited ? initFragment(clazz) : createFragment(clazz);

            String tag = findTag(clazz);

            FragmentTransaction ft = fm.beginTransaction()
                                       .replace(container, fragment, tag);
            if(backstacked)
                ft.addToBackStack(tag);

            ft.commit();
        }
        return clazz.cast(fragment);
    }

    /**
     * Removes fragment of type clazz from fragment manager
     *
     * @param fm link to fragment manager
     * @param clazz type of fragment to unpark
     * @param <T> child of Fragment class
     */
    public static <T extends Fragment> void unparkFragment(FragmentManager fm, Class<T> clazz) {
        Fragment fragment = findFragment(fm, clazz);
        if(fragment == null)
            return;

        fm.beginTransaction()
          .remove(fragment)
          .commit();
    }

    /**
     * Tries to find fragment of type clazz in fragment manager
     *
     * @param fm link to fragment manager
     * @param clazz type of fragment to
     * @param <T> child of Fragment class
     * @return found fragment or null
     */
    public static <T extends Fragment> T findFragment(FragmentManager fm, Class<T> clazz) {
        return clazz.cast(fm.findFragmentByTag(findTag(clazz)));
    }

    /**
     * Shows dialog fragment of type clazz initialized with initData (or not, optional)
     *
     * @param fm link to fragment manager
     * @param clazz type of dialog fragment to show
     * @param <T> child of dialog fragment class
     */
    public static <T extends DialogFragment> void showDialog(FragmentManager fm, Class<T> clazz) {
        DialogFragment dialog = findFragment(fm, clazz);
        if (dialog == null) {
            dialog = initFragment(clazz);

        } else
            dialog.dismiss();

        dialog.show(fm, findTag(clazz));
    }

    /**
     *  Searches for static field TAG in clazz type
     *
     * @param clazz type of fragment to find tag for
     * @param <T> child of Fragment class
     * @return value of TAG field or null
     */
    private static <T extends Fragment> String findTag(Class<T> clazz) {

        final String TAG = "TAG";

        try {
            Field tag = clazz.getDeclaredField(TAG);
            return (String) tag.get(null);

        } catch (IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            Log.d("myLogs", Utils.class.getSimpleName() + "findTag: " + clazz.getSimpleName()
                                    + " class must have static final field 'TAG'");
        }
        return null;
    }

    /**
     * Creates fragment of type clazz
     *
     * @param clazz type of fragment to create
     * @param <T> child of Fragment class
     * @return created fragment or null
     */
    private static <T extends Fragment> T createFragment(Class<T> clazz) {

        try {
            return clazz.newInstance();

        } catch (ClassCastException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tries to initialize fragment of type clazz by one of three methods:
     * init(Class, Bundle), init(Bundle), init().
     *
     * @param clazz type of fragment to initialize
     * @param <T> child of Fragment class
     * @return initialized fragment or null
     */
    private static <T extends Fragment> T initFragment(Class<T> clazz) {

        final String INIT = "newInstance";

        try {
            Method init = clazz.getMethod(INIT);
            Object fragment = init.invoke(null);

            return clazz.cast(fragment);

        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkUp(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // network is not available at all
        if(cm == null)
            return false;

        NetworkInfo ni = cm.getActiveNetworkInfo();
        // default network is presented and it is able to connect through and it is connected now
        return (ni != null) && ni.isAvailable() && ni.isConnected();
    }

    public static String millisToLocalDate(long millis, String format) {
        return (DateFormat.format(format, millis)).toString();
    }

    public static String millisToUtcDate(long millis, String format) {
        Calendar calendar = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));
        calendar.setTimeInMillis(millis);
        return (DateFormat.format(format, calendar)).toString();
    }

    // shows a cursor contents
    public static void printCursor(Cursor cursor) {
        // checking if the query was succeeded
        if(cursor != null) {
            // if the result set of rows is empty
            if(cursor.moveToFirst()) {
                StringBuilder record;
                do {
                    record = new StringBuilder();
                    for(String str : cursor.getColumnNames()) {
                        record.append(str);
                        record.append(" = ");
                        record.append(cursor.getString(cursor.getColumnIndex(str)));
                        record.append("; ");

                    }
                    Log.d("myLogs", record.toString());
                } while(cursor.moveToNext());
            } else {
                // there are no rows in query result set
                Log.d("myLogs", "0 rows");
            }
            // if it is not
        } else {
            Log.d("myLogs", "Cursor is null!");
        }
    }

}
