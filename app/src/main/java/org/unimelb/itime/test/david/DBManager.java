package org.unimelb.itime.test.david;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import org.greenrobot.greendao.query.QueryBuilder;
import org.unimelb.itime.test.bean.DaoMaster;
import org.unimelb.itime.test.bean.DaoSession;
import org.unimelb.itime.test.bean.Event;
import org.unimelb.itime.test.bean.EventDao;

import java.util.List;

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

    public void insertEventList(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDaoDao = daoSession.getEventDao();
        eventDaoDao.insertInTx(events);
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

    public void clearDB(){
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        EventDao eventDaoDao = daoSession.getEventDao();
        eventDaoDao.deleteAll();
    }

    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }
}
