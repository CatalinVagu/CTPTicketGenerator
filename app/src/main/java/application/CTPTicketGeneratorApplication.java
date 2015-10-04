package application;

import android.app.Application;

/**
 * Created by andrei on 19/09/15.
 */
public class CTPTicketGeneratorApplication extends Application {

    private static CTPTicketGeneratorApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static CTPTicketGeneratorApplication getInstance() {
        return instance;
    }
}
