package david.itime_calendar;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by David Liu on 21/10/17.
 * NowBoarding Ltd
 * lyhmelbourne@gmail.com
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        changeLocale();
    }

    private void changeLocale(){
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        config.locale = Locale.CHINESE;
        resources.updateConfiguration(config, dm);
    }
}
