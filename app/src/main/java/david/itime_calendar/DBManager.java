package david.itime_calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import david.itime_calendar.bean.Contact;
import david.itime_calendar.bean.ContactDao;
import david.itime_calendar.bean.DaoMaster;
import david.itime_calendar.bean.DaoSession;
import david.itime_calendar.bean.Event;
import david.itime_calendar.bean.EventDao;
import david.itime_calendar.bean.Invitee;
import david.itime_calendar.bean.InviteeDao;

/**
 * Created by yuhaoliu on 28/08/16.
 */
public class DBManager {
    private final static String dbName = "test_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }


    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class)
        {
            if (mInstance == null)
                {
                    mInstance = new DBManager(context);
                }
        }
        }
            return mInstance;
    }


    public void insertEvent(Event event) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDaoDao = daoSession.getEventDao();
        eventDaoDao.insert(event);
    }

    public void insertInvitee(Invitee invitee) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        InviteeDao inviteeDao = daoSession.getInviteeDao();
        inviteeDao.insert(invitee);
    }

    public void insertContact(Contact contact) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ContactDao contactDao = daoSession.getContactDao();
        contactDao.insert(contact);
    }

    public void insertEventList(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDaoDao = daoSession.getEventDao();
        eventDaoDao.insertInTx(events);
    }

    public void insertInviteeList(List<Invitee> invitees) {
        if (invitees == null || invitees.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        InviteeDao inviteeDao = daoSession.getInviteeDao();
        inviteeDao.insertInTx(invitees);
    }

    public List<Event> queryEventList(long startTime, long endTime) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDao = daoSession.getEventDao();
        QueryBuilder<Event> qb = eventDao.queryBuilder();
        qb.where(qb.and(EventDao.Properties.StartTime.gt(startTime - 1), EventDao.Properties.StartTime.le(endTime)));
        List<Event> list = qb.list();
        return list;
    }

    public List<Event> getAllEvents() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDao = daoSession.getEventDao();
        QueryBuilder<Event> qb = eventDao.queryBuilder();
        List<Event> list = qb.list();
        return list;
    }

    public List<Invitee> getAllInvitee(){
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        InviteeDao inviteeDao = daoSession.getInviteeDao();
        QueryBuilder<Invitee> qb = inviteeDao.queryBuilder();
        List<Invitee> list = qb.list();
        return list;
    }

    public void clearDB(){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDao = daoSession.getEventDao();
        ContactDao contactDao = daoSession.getContactDao();
        InviteeDao inviteeDao = daoSession.getInviteeDao();
        eventDao.deleteAll();
        contactDao.deleteAll();
        inviteeDao.deleteAll();
    }

    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    public SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }
}
