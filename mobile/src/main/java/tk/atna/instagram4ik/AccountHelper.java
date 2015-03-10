package tk.atna.instagram4ik;


import android.net.Uri;

import java.util.Set;

public class AccountHelper {

    public static final String NETWORK_DOWN = "Network is unavailable at the moment";


    public static String getAuthUrl(String clientId) {
        return ServerApi.FULL_AUTH_URL + clientId;
    }

    public static String getBlank() {
        return ServerApi.BLANK_URL;
    }

    public static String getLogoutUrl() {
        return ServerApi.FULL_LOGOUT_URL;
    }

    public static Uri hitTargetRedirect(String url) {
        Uri uri = Uri.parse(url);
        // if needed host
        if((ServerApi.DEFAULT_REDIRECT_URI.equals(uri.getHost())))
            return uri;

        return null;
    }

    public static String parseToken(Uri uri) {
        String raw = uri.getFragment();
        if (raw != null) {
            int index = (ServerApi.ACCESS_TOKEN + "=").length();
            if (index > 0)
                return raw.substring(index);
        }

        return null;
    }

    public static Boolean isUserDenied(Uri uri) {
        Set<String> names = uri.getQueryParameterNames();
        for(String name : names) {
            if(ServerApi.ERROR_REASON.equals(name)) {
                return ServerApi.USER_DENIED.equals(uri.getQueryParameter(name));
            }
        }
        return null;
    }

}

