package com.cantv.wechatphoto.utils.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table WECHATPHOTO_TAB.
*/
public class WechatPhotoInfoDao extends AbstractDao<PhotoBean, Long> {

    public static final String TABLENAME = "WECHATPHOTO_TAB";

    /**
     * Properties of entity WechatPhotoInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "id");
        public final static Property Userid = new Property(1, String.class, "userid", false, "userid");
        public final static Property Wxname = new Property(2, String.class, "wxname", false, "wxname");
        public final static Property Wxheadimgurl = new Property(3, String.class, "wxheadimgurl", false, "wxheadimgurl");
        public final static Property District = new Property(4, String.class, "district", false, "district");
        public final static Property Description = new Property(5, String.class, "description", false, "description");
        public final static Property Photoid = new Property(6, Integer.class, "photoid", false, "photoid");
        public final static Property Photourl = new Property(7, String.class, "photourl", false, "photourl");
        public final static Property Photocdnurl = new Property(8, String.class, "photocdnurl", false, "photocdnurl");
        public final static Property Photolocalurl = new Property(9, String.class, "photolocalurl", false, "photolocalurl");
        public final static Property Uploadtime = new Property(10, String.class, "uploadtime", false, "uploadtime");
        public final static Property Expiredtime = new Property(11, Long.class, "expiredtime", false, "expiredtime");
        public final static Property Altertime = new Property(12, Long.class, "altertime", false, "altertime");
        public final static Property Photowide = new Property(13, Integer.class, "photowide", false, "photowide");
        public final static Property Photohigh = new Property(14, Integer.class, "photohigh", false, "photohigh");
        public final static Property Direction = new Property(15, Integer.class, "direction", false, "direction");
        public final static Property Back1 = new Property(16, String.class, "back1", false, "back1");
        public final static Property Back2 = new Property(17, String.class, "back2", false, "back2");
        public final static Property Back3 = new Property(18, String.class, "back3", false, "back3");
    };


    public WechatPhotoInfoDao(DaoConfig config) {
        super(config);
    }
    
    public WechatPhotoInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'WECHATPHOTO_TAB' (" + //
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'userid' TEXT," + // 1: userid
                "'wxname' TEXT," + // 2: wxname
                "'wxheadimgurl' TEXT," + // 3: wxheadimgurl
                "'district' TEXT," + // 4: district
                "'description' TEXT," + // 5: description
                "'photoid' INTEGER," + // 6: photoid
                "'photourl' TEXT," + // 7: photourl
                "'photocdnurl' TEXT," + // 8: photocdnurl
                "'photolocalurl' TEXT," + // 9: photolocalurl
                "'uploadtime' TEXT," + // 10: uploadtime
                "'expiredtime' INTEGER," + // 11: expiredtime
                "'altertime' INTEGER," + // 12: altertime
                "'photowide' INTEGER," + // 13: photowide
                "'photohigh' INTEGER," + // 14: photohigh
                "'direction' INTEGER," + // 15: direction
                "'back1' TEXT," + // 16: back1
                "'back2' TEXT," + // 17: back2
                "'back3' TEXT);"); // 18: back3
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'WECHATPHOTO_TAB'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PhotoBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userid = entity.getUserid();
        if (userid != null) {
            stmt.bindString(2, userid);
        }
 
        String wxname = entity.getWxname();
        if (wxname != null) {
            stmt.bindString(3, wxname);
        }
 
        String wxheadimgurl = entity.getWxheadimgurl();
        if (wxheadimgurl != null) {
            stmt.bindString(4, wxheadimgurl);
        }
 
        String district = entity.getDistrict();
        if (district != null) {
            stmt.bindString(5, district);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(6, description);
        }
 
        Integer photoid = entity.getPhotoid();
        if (photoid != null) {
            stmt.bindLong(7, photoid);
        }
 
        String photourl = entity.getPhotourl();
        if (photourl != null) {
            stmt.bindString(8, photourl);
        }
 
        String photocdnurl = entity.getPhotocdnurl();
        if (photocdnurl != null) {
            stmt.bindString(9, photocdnurl);
        }
 
        String photolocalurl = entity.getPhotolocalurl();
        if (photolocalurl != null) {
            stmt.bindString(10, photolocalurl);
        }
 
        String uploadtime = entity.getUploadtime();
        if (uploadtime != null) {
            stmt.bindString(11, uploadtime);
        }
 
        Long expiredtime = entity.getExpiredtime();
        if (expiredtime != null) {
            stmt.bindLong(12, expiredtime);
        }
 
        Long altertime = entity.getAltertime();
        if (altertime != null) {
            stmt.bindLong(13, altertime);
        }
 
        Integer photowide = entity.getPhotowide();
        if (photowide != null) {
            stmt.bindLong(14, photowide);
        }
 
        Integer photohigh = entity.getPhotohigh();
        if (photohigh != null) {
            stmt.bindLong(15, photohigh);
        }
 
        Integer direction = entity.getDirection();
        if (direction != null) {
            stmt.bindLong(16, direction);
        }
 
        String back1 = entity.getBack1();
        if (back1 != null) {
            stmt.bindString(17, back1);
        }
 
        String back2 = entity.getBack2();
        if (back2 != null) {
            stmt.bindString(18, back2);
        }
 
        String back3 = entity.getBack3();
        if (back3 != null) {
            stmt.bindString(19, back3);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PhotoBean readEntity(Cursor cursor, int offset) {
    	PhotoBean entity = new PhotoBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // wxname
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // wxheadimgurl
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // district
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // description
            cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // photoid
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // photourl
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // photocdnurl
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // photolocalurl
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // uploadtime
            cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11), // expiredtime
            cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12), // altertime
            cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13), // photowide
            cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14), // photohigh
            cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15), // direction
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // back1
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // back2
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18) // back3
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PhotoBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setWxname(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setWxheadimgurl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDistrict(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDescription(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPhotoid(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setPhotourl(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPhotocdnurl(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setPhotolocalurl(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setUploadtime(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setExpiredtime(cursor.isNull(offset + 11) ? null : cursor.getLong(offset + 11));
        entity.setAltertime(cursor.isNull(offset + 12) ? null : cursor.getLong(offset + 12));
        entity.setPhotowide(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
        entity.setPhotohigh(cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14));
        entity.setDirection(cursor.isNull(offset + 15) ? null : cursor.getInt(offset + 15));
        entity.setBack1(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setBack2(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setBack3(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PhotoBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PhotoBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
