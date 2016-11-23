package org.unimelb.itime.test.RuleFactory;

/**
 * Created by yuhaoliu on 20/11/16.
 */
public enum FrequencyEnum {
    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    MONTHLY("MONTHLY"),
    YEARLY("YEARLY");

    private String value;

    FrequencyEnum(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
