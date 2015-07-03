package com.code.immersivemode;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.code.immersivemode.Record;
import com.code.immersivemode.User;
import com.code.immersivemode.Location;

import com.code.immersivemode.RecordDao;
import com.code.immersivemode.UserDao;
import com.code.immersivemode.LocationDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig recordDaoConfig;
    private final DaoConfig userDaoConfig;
    private final DaoConfig locationDaoConfig;

    private final RecordDao recordDao;
    private final UserDao userDao;
    private final LocationDao locationDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        recordDaoConfig = daoConfigMap.get(RecordDao.class).clone();
        recordDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        locationDaoConfig = daoConfigMap.get(LocationDao.class).clone();
        locationDaoConfig.initIdentityScope(type);

        recordDao = new RecordDao(recordDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        locationDao = new LocationDao(locationDaoConfig, this);

        registerDao(Record.class, recordDao);
        registerDao(User.class, userDao);
        registerDao(Location.class, locationDao);
    }
    
    public void clear() {
        recordDaoConfig.getIdentityScope().clear();
        userDaoConfig.getIdentityScope().clear();
        locationDaoConfig.getIdentityScope().clear();
    }

    public RecordDao getRecordDao() {
        return recordDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public LocationDao getLocationDao() {
        return locationDao;
    }

}
