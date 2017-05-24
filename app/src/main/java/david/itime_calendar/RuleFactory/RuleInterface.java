package david.itime_calendar.RuleFactory;

/**
 * Created by yuhaoliu on 25/11/16.
 */
public interface RuleInterface {
    long getStartTime();
    long getEndTime();
    String[] getRecurrence();
}
