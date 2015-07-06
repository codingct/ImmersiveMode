package com.immersive.utils;

import java.util.Date;
import java.util.List;

import com.code.immersivemode.AppContext;
import com.code.immersivemode.DaoSession;
import com.code.immersivemode.Location;
import com.code.immersivemode.LocationDao;
import com.code.immersivemode.Record;
import com.code.immersivemode.RecordDao;
import com.code.immersivemode.User;
import com.code.immersivemode.UserDao;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import android.content.Context;
import android.util.Log;

public class GreenDaoUtils {
	private static Context mContext;
    private static GreenDaoUtils instance;
    private static final String TAG = "GreenDaoUtils";
                                                                                                                                                                                                                                                                                                                                   
    private UserDao userDao;
    private RecordDao recordDao;
    private LocationDao locationDao;
                                                                                                                                                                                                                                                                                                                                   
    private GreenDaoUtils() {
    	Log.d(TAG, "GreenDaoUtils Create");
    }
                                                                                                                                                                                                                                                                                                                                   
    public static GreenDaoUtils getInstance(Context context) {
        if (instance == null) {
            instance = new GreenDaoUtils();
            if (mContext == null) {
                mContext = context;
            }
                                                                                                                                                                                                                                                                                                                                           
            // 数据库对象
            DaoSession daoSession = AppContext.getDaoSession(mContext);
            instance.userDao = daoSession.getUserDao();
            instance.recordDao = daoSession.getRecordDao();
            instance.locationDao = daoSession.getLocationDao();

        }
        return instance;
    }
    
    /** 添加数据 */
    public void addToUserTable(User item) {
    	userDao.insert(item);
    }
    public void addToRecordTable(Record item) {
    	recordDao.insert(item);
    }
    public void addToLocationTable(Location item) {
    	locationDao.insert(item);
    }
    
    /** 查询 */
    public boolean isUserSaved(int id)
    {
        QueryBuilder<User> qb = userDao.queryBuilder();
        qb.where(UserDao.Properties.Id.eq(id));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }
    
    public boolean isRecordSaved(long record_id)
    {
        QueryBuilder<Record> qb = recordDao.queryBuilder();
        qb.where(RecordDao.Properties.Id.eq(record_id));
        qb.buildCount().count();
        return qb.buildCount().count() > 0 ? true : false;
    }
    public List<User> getAllUser() {
        return userDao.loadAll();
    }
    public List<Record> getAllRecord() {
    	return recordDao.loadAll();
    }
    public List<Location> getAllLocation() {
    	return locationDao.loadAll();
    }
    private List<Record> getRecordByUserId(int Id) {
        QueryBuilder<Record> qb = recordDao.queryBuilder();
        qb.where(RecordDao.Properties.User_id.eq(Id));
        qb.orderAsc(RecordDao.Properties.Record_time);
        if (qb.list().size() > 0) {
        	return qb.list();
        } else {
            return null;
        }
    }
    public long getReocrdIdbyDate(Date date)
    {
        QueryBuilder<Record> qb = recordDao.queryBuilder();
        qb.where(RecordDao.Properties.Record_time.eq(date));
        if (qb.list().size() > 0)
        {
            return qb.list().get(0).getId();
        }
        else
        {
            return -1;
        }
    }
    private List<Location> getLocationByRecordId(int Record_id) {
    	 QueryBuilder<Location> qb = locationDao.queryBuilder();
         qb.where(LocationDao.Properties.Record_id.eq(Record_id));
         qb.orderAsc(LocationDao.Properties.Id);
         if (qb.list().size() > 0) {
         	return qb.list();
         } else {
             return null;
         }
    }
    
    /** 删除 */
    private void deleteUser(int Id) {
        QueryBuilder<User> qb = userDao.queryBuilder();
        DeleteQuery<User> bd = qb.where(UserDao.Properties.Id.eq(Id)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    private void deleteRecord(int Record_id) {
    	QueryBuilder<Record> qb = recordDao.queryBuilder();
        DeleteQuery<Record> bd = qb.where(RecordDao.Properties.Id.eq(Record_id)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    private void deleteLocation(int Record_id) {
    	QueryBuilder<Location> qb = locationDao.queryBuilder();
        DeleteQuery<Location> bd = qb.where(LocationDao.Properties.Record_id.eq(Record_id)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }
    
    
    /** 工具类外部接口 */
    public List<Record> requestFindRecordByUser(int Id) {
    	return getRecordByUserId(Id);
    }
    
    public List<Location> requestFindLocationByRecord(int Record_id) {
    	return getLocationByRecordId(Record_id);
    }
    
    public void requestDeleteUser(int Id) {
    	deleteUser(Id);
    }
    
    public void requestDeleteRecord(int Record_id) {
    	deleteRecord(Record_id);
    	deleteLocation(Record_id);
    }
    
    
    
    
    

}