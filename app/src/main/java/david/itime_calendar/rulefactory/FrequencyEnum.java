package david.itime_calendar.rulefactory;

public enum FrequencyEnum {
    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY"),
    YEARLY("YEARLY");

    /**
     * Created by David Liu on 20/11/16.
     * lyhmelbourne@gmail.com
     */
    private String value;

    FrequencyEnum() {
    }

    FrequencyEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
