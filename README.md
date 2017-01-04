#CalendarView
-------------

> Welcome 
  WeChat：[poplyh]()  
---
##Catalog

* [DayView](#dayview)
    * Demo
    * Usage
    * Listener
    * Methods

* [WeekView](#weekview)
  * Demo
  * Usage
  * Listener
  * Methods

* [AgendaView](#agendaview)
  * Demo
  * Usage
  * Listener
  * Methods

DayView
-------------

####Demo

Create  | Overlap
--------- | --------
![image](https://github.com/DavidFancy/CalendarView/blob/master/demo/gif_dayview_create.gif)  | ![image](https://github.com/DavidFancy/CalendarView/blob/master/demo/gif_dayview_overlap.gif)

  
Drag in day  | Drag between days
--------- | --------
![image](https://github.com/DavidFancy/CalendarView/blob/master/demo/gif_dayview_drag.gif)   | ![image](https://github.com/DavidFancy/CalendarView/blob/master/demo/gif_dayview_dragDay.gif) 

Header  |
---------
![image](https://github.com/DavidFancy/CalendarView/blob/master/demo/gif_dayview_other.gif)|
  
####Usage
Step1: Create view
```Java
  <org.unimelb.itime.vendor.dayview.MonthDayView
          android:id="@+id/month_day_view"
          android:layout_width="match_parent"
          android:layout_height="match_parent" />
```
Step2: Set data source
```Java
    //Set the data source with format of ITimeEventPackageInterface
    //ITimeEventPackageInterface is composed by two parts:
    //  1: regular events. 2: repeated events.
    monthDayView.setDayEventMap(eventManager.getEventsPackage());
    //If creating instance of event is needed, set the class.
    monthDayView.setEventClassName(Event.class);
```
####Listener

-----------
OnHeaderListener
```Java
    //Detecting selected date changed
    public interface OnHeaderListener{
            void onMonthChanged(MyCalendar calendar);
    }
```

-----------
OnBodyListener
```Java
    /**
     * DayDraggableEventView contains data source and all information about new status
     */
    public interface OnBodyListener {
        //If current event view is draggable
        boolean isDraggable(DayDraggableEventView eventView);
        //While creating event view
        void onEventCreate(DayDraggableEventView eventView);
        //While clicking event
        void onEventClick(DayDraggableEventView eventView);
        //When start dragging
        void onEventDragStart(DayDraggableEventView eventView);
        //On dragging
        void onEventDragging(DayDraggableEventView eventView, int x, int y);
        //When dragging ended
        void onEventDragDrop(DayDraggableEventView eventView);
    }
```
####Methods
```Java
    //set data source
    public void setDayEventMap(ITimeEventPackageInterface eventPackage);
    //refresh all event views base on data source
    public void reloadEvents();
    //scrollTo certain DATE.day
    public void scrollTo(final Calendar calendar);
    //scrollTo certain DATE.day && DAY.time
    public void scrollToWithOffset(final long time);
    //scrollTo DATE.today
    public void backToToday();
    //Show alpha animation on background of DraggableView (from 255 - 125).
    public void showEventAnim(ITimeEventInterface... events);
    //Remove all listener, shown as static page, for preview.
    public void removeAllOptListener()
    
    public void setOnHeaderListener(OnHeaderListener onHeaderListener);
    public void setOnBodyListener(OnBodyListener onBodyListener);
```

WeekView
------
Description: <br>
The body part of weekview is same as dayview's. Both of them are composed of FlexibleLenViewBody.
In the FlexibleLenViewBody, attribute 'displayLen' controls the number of day to be shown within single body.
If you want to customzie how many day to show, just edite attribute 'displayLen'.
####Demo
Create && Drag  | Swipe between week
--------- | --------
![image](https://github.com/DavidFancy/CalendarView/blob/master/demo/gif_week_create%26drag.gif)  | ![image](https://github.com/DavidFancy/CalendarView/blob/master/demo/gif_week_swipe.gif)

####Usage
Same as DayView<br>
  
######Addition:
  Recommend Time Block: create customized duration time block as recommendation or other usage.
```Java
  //Enable function of creating time block
  weekView.enableTimeSlot();
```
####Listener
Same as DayView<br>
  
######Addition:
OnTimeSlotListener
```Java
    /**
     * TimeSlotView contains data source(ITimeTimeSlotInterface)
     * and all information about new status
     */
    public interface OnTimeSlotListener {
        //While creating time block
        void onTimeSlotCreate(TimeSlotView timeSlotView);
        //While clicking existed time block
        void onTimeSlotClick(TimeSlotView timeSlotView);
        //When start dragging
        void onTimeSlotDragStart(TimeSlotView timeSlotView);

        /**
         * On dragging
         * @param timeSlotView : The view on dragging
         * @param x : current X position of View
         * @param y : current Y position of View
         */
        void onTimeSlotDragging(TimeSlotView timeSlotView, int x, int y);

        /**
         * When dragging ended
         * @param timeSlotView : The view on drop
         * @param startTime : dropped X position of View
         * @param endTime : dropped Y position of View
         */
        void onTimeSlotDragDrop(TimeSlotView timeSlotView, long startTime, long endTime);
    }
```
####Methods
Same as DayView<br>

######Addition:
```Java
  //Enable funciton of time block.
  public void enableTimeSlot();
  //Show alpha animation on background of TimeSlotView (from 255 - 125).
  public <T extends ITimeTimeSlotInterface>void showTimeslotAnim(final T ... timeslots);
```

AgendaView
------

####Demo

Agenda Overall  | Float window
--------- | --------
![image](https://github.com/DavidFancy/CalendarView/blob/master/demo/gif_agenda_all.gif)  | ![image](https://github.com/DavidFancy/CalendarView/blob/master/demo/gif_agenda_floatWindow.gif)  

####Usage
Step1: Create view
```Java
  <org.unimelb.itime.vendor.agendaview.MonthAgendaView
        android:id="@+id/month_agenda_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
Step2: Set data source
```Java
    //Set the data source with format of ITimeEventPackageInterface
    //ITimeEventPackageInterface is composed by two parts:
    //  1: regular events. 2: repeated events.
    monthAgendaView.setDayEventMap(eventManager.getEventsPackage());
```
####Listener
-----------
OnHeaderListener
```Java
    public interface OnHeaderListener {
        void onMonthChanged(MyCalendar var1);
    }
```
-----------
OnEventClickListener
```Java
    public interface OnEventClickListener {
        void onEventClick(ITimeEventInterface var1);
    }
```
####Methods
```Java
  //set data source
  public void setDayEventMap(ITimeEventPackageInterface eventPackage);
  //scrollTo certain DATE.day
  public void scrollTo(final Calendar calendar);
  
  public void setOnHeaderListener(OnHeaderListener onHeaderListener);
  public void setOnEventClickListener(OnEventClickListener onEventClickListener);
```