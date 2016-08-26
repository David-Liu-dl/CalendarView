package org.unimelb.itime.vendor.helper;

import java.util.ArrayList;

/**
 * Created by Paul on 26/08/2016.
 */
public class MessageTimeSlot {
    public final ArrayList<Long> timeSlotArraylist;
    public final int duration;

    public MessageTimeSlot(ArrayList<Long> timeSlotArraylist, int duration) {
        this.timeSlotArraylist = timeSlotArraylist;
        this.duration= duration;
    }
}
