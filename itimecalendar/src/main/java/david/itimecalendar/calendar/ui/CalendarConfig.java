package david.itimecalendar.calendar.ui;

/**
 * Created by yuhaoliu on 14/8/17.
 */

public class CalendarConfig {
    public enum Mode{
        EVENT, TIMESLOT_REVIEW, TIMESLOT_CREATE
    }

    public enum TimePattern {
        HH,HH_A
    }

    public Mode mode = Mode.EVENT;
    public TimePattern time = TimePattern.HH_A;

    public boolean unconfirmedIncluded = true;
    public boolean isHeaderVisible = true;

    public boolean isAllDayEventClickable = true;
    public boolean isAllDayEventAsBg = false;

    public boolean isEventCreatable = true;
    public boolean isEventDraggable = true;
    public boolean isEventClickable = true;
    public boolean isEventAsBgMode = false;
    public boolean isEventClickCreatable = false;

    public boolean isAllDayRcdEnable = false;

    public boolean isAllDayTimeSlotCreatable = false;
    public boolean isAllDayTimeSlotClickable = false;

    public boolean isTimeSlotCreatable = true;
    public boolean isTimeSlotDraggable = true;
    public boolean isTimeSlotClickable = true;
    public boolean isTimeSlotClickCreatable = true;

    public void disableEvent(){
        disableEventHeader();
        disableEventBody();
    }

    public void disableEventHeader(){
        this.isAllDayEventClickable = false;
        this.isAllDayEventAsBg = true;
    }

    public void disableEventBody(){
        this.isEventAsBgMode = true;
        this.isEventDraggable = false;
        this.isEventClickable = false;
        this.isEventCreatable = false;
        this.isEventClickCreatable = false;
    }

    public void enableEvent(){
        this.mode = Mode.EVENT;
        this.disableTimeslot();

        this.isAllDayEventClickable = true;
        this.isAllDayEventAsBg = false;

        this.isEventCreatable = true;
        this.isEventDraggable = true;
        this.isEventClickable = true;
        this.isEventAsBgMode = false;
        isEventClickCreatable = false;
    }

    public void disableTimeslot(){
        this.isAllDayRcdEnable = false;
        this.isAllDayTimeSlotCreatable = false;
        this.isAllDayTimeSlotClickable = false;

        this.isTimeSlotCreatable = false;
        this.isTimeSlotDraggable = false;
        this.isTimeSlotClickable = false;
    }

    public void enableViewTimeslotRegular(){
        this.mode = Mode.TIMESLOT_REVIEW;
        this.disableEvent();

        this.isAllDayRcdEnable = false;
        this.isAllDayTimeSlotCreatable = false;
        this.isAllDayTimeSlotClickable = false;

        this.isTimeSlotCreatable = false;
        this.isTimeSlotDraggable = false;
        this.isTimeSlotClickable = true;
    }

    public void enableViewTimeslotAllday(){
        this.mode = Mode.TIMESLOT_REVIEW;
        this.disableEvent();

        this.isAllDayRcdEnable = false;
        this.isAllDayTimeSlotCreatable = false;
        this.isAllDayTimeSlotClickable = true;

        this.isTimeSlotCreatable = false;
        this.isTimeSlotDraggable = false;
        this.isTimeSlotClickable = false;
    }

    public void enableCreateTimeslotRegular(){
        this.mode = Mode.TIMESLOT_CREATE;
        this.disableEvent();

        this.isAllDayRcdEnable = false;
        this.isAllDayTimeSlotCreatable = false;
        this.isAllDayTimeSlotClickable = false;

        this.isTimeSlotCreatable = true;
        this.isTimeSlotDraggable = true;
        this.isTimeSlotClickable = true;
    }

    public void enableCreateTimeslotAllday(){
        this.mode = Mode.TIMESLOT_CREATE;
        this.disableEvent();

        this.isAllDayRcdEnable = true;
        this.isAllDayTimeSlotCreatable = true;
        this.isAllDayTimeSlotClickable = true;

        this.isTimeSlotCreatable = false;
        this.isTimeSlotDraggable = false;
        this.isTimeSlotClickable = false;
    }
}
