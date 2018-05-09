package david.itime_calendar.rulefactory;

/**
 * Created by David Liu on 25/11/16.
 * lyhmelbourne@gmail.com
 */

public interface RuleInterface {
    long getStartTime();
    long getEndTime();
    String[] getRecurrence();
}
