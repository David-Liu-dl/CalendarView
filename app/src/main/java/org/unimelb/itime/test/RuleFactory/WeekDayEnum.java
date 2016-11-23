package org.unimelb.itime.test.RuleFactory;

/**
 * Created by yuhaoliu on 20/11/16.
 */
public enum WeekDayEnum {
    MO(2),
    TU(3),
    WE(4),
    TH(5),
    FR(6),
    SA(7),
    SU(1);

    private int index;

    private WeekDayEnum(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
