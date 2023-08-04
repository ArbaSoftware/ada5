package nl.arba.ada.client.adaclient.utils;

import java.util.ResourceBundle;

public class InternationalizationUtils {
    private static ResourceBundle bundle;
    public static ResourceBundle getResources() {
        if (bundle == null)
            bundle = ResourceBundle.getBundle("adaclient");
        return bundle;
    }

    public static String getAppTitle() {
        return getResources().getString("app.title");
    }

    public static String get(String key) {
        return getResources().getString(key);
    }
}
