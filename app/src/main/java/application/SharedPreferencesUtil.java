package application;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SharedPreferencesUtil {

    private static final String KEY_LINES = "key_lines";

    private final SharedPreferences prefs;
    private static SharedPreferencesUtil instance = new SharedPreferencesUtil();

    private SharedPreferencesUtil() {
        Context context = CTPTicketGeneratorApplication.getInstance();
        this.prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance() {
        return instance;
    }

    public List<String> getLines() {
        return new ArrayList<>(prefs.getStringSet(KEY_LINES, new HashSet<String>()));
    }

    public void setLines(List<String> lines) {
        prefs.edit().putStringSet(KEY_LINES, new HashSet<String>(lines)).apply();
    }
}
