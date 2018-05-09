package david.itime_calendar.rulefactory;

public enum WeekDayEnum {
    MO(2),
    TU(3),
    WE(4),
    TH(5),
    FR(6),
    SA(7),
    SU(1);

    /**
     * Created by David Liu on 20/11/16.
     * lyhmelbourne@gmail.com
     */
    private int index;

    WeekDayEnum(){};

    WeekDayEnum(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
