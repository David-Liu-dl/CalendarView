#ITimeVendor-android
-------------

> 关于我，欢迎关注  
  微信：[poplyh]()  

####示例:  
把使用了该项目的案例放在这里。可以放APK下载链接，或者简单放几张截图。  
（示例一开始就放出来，方便浏览者一眼就看出是不是想找的东西）

![image](https://github.com/itime-team/ITimeVendor-android/blob/alpha/vendor/src/main/res/drawable/icon_timeslot_arrow.png)  

##Catalog
  * [DayView](#DayView)
    * Demo
    * Usage
    * Listener
    * Methods

  * [WeekView](#WeekView)
    * Demo
    * Usage
    * Listener
    * Methods

  * [AgendaView](#AgendaView)
    * Demo
    * Usage
    * Listener
    * Methods

DayView
------

####Demo

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
  monthDayView = (MonthDayView) root.findViewById(R.id.month_day_view);
  monthDayView.setDayEventMap(ITimeEventPackageInterface eventPackage);
  monthDayView.setEventClassName(Class<E> className);// if creating instance needed
```
####Listener

-----------
OnHeaderListener:
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
        //while creating event view
        void onEventCreate(DayDraggableEventView eventView);
        //while clicking event
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
```
WeekView
------

####Demo

####Usage

####Listener

####Methods


AgendaView
------

####Demo

####Usage

####Listener

####Methods
