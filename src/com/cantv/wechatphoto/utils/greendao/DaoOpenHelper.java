package com.cantv.wechatphoto.utils.greendao;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cantv.wechatphoto.utils.greendao.DaoMaster.DevOpenHelper;

public class DaoOpenHelper extends DevOpenHelper {
	/**
	 * @author long 数据库操作页 DaoMaster类中添加数据库Version
	 */
	public static final String TAG = "GreenDaoDBHelper";
	private static final String DB_NAME = "cibntv.sqlite";
	private static DaoOpenHelper instance = null;
	private DaoMaster mDaoMaster;
	private DaoSession mDaoSession;
	private WechatPhotoInfoDao mWechatPhotoInfoDao;

	private DaoOpenHelper(Context ctx) {
		super(ctx, DB_NAME, null);
		mDaoMaster = getDaoMaster();
		mDaoSession = getDaoSession();
		mWechatPhotoInfoDao = mDaoSession.getWechatPhotoInfoDao();
	}

	public static DaoOpenHelper getInstance(Context ctx) {
		if (ctx == null) {
			return null;
		}

		if (instance == null) {
			synchronized (DaoOpenHelper.class) {
				if (instance == null) {
					instance = new DaoOpenHelper(ctx.getApplicationContext());
				}
			}

		}
		return instance;
	}

	private DaoMaster getDaoMaster() {
		if (mDaoMaster == null) {
			mDaoMaster = new DaoMaster(getWritableDatabase());
		}
		return mDaoMaster;
	}

	private DaoSession getDaoSession() {
		if (mDaoSession == null) {
			mDaoSession = getDaoMaster().newSession();
		}
		return mDaoSession;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
	}
	
	public WechatPhotoInfoDao getPhotoInfoDao() {
		return mWechatPhotoInfoDao;
	}

	/**
	 * 执行新增
	 **/
	public long execInsertPhoto(PhotoBean info) {
		return mWechatPhotoInfoDao.insert(info);
	}

	/**
	 * 使用事务批量插入数据
	 */
	public void photoBatchInsertWithTransaction(List<PhotoBean> infos) {
		mWechatPhotoInfoDao.insertInTx(infos);
	}

	/**
	 * 执行删除(单条)
	 * 
	 * @param
	 */
	public void removePhoto(PhotoBean info) {
		mWechatPhotoInfoDao.delete(info);
	}

	/**
	 * 执行删除(多条)
	 * 
	 * @param userid
	 */
	public void removePhotoById(String userid) {
		mWechatPhotoInfoDao.queryBuilder().where(WechatPhotoInfoDao.Properties.Userid.eq(userid)).buildDelete()
				.executeDeleteWithoutDetachingEntities();

	}

	/**
	 * 执行删除(全部)
	 * 
	 */
	public void removePhotos(List<PhotoBean> infos) {
		mWechatPhotoInfoDao.deleteInTx(infos);

	}

	/**
	 * 执行更新(单条)
	 **/
	public void updatePhoto(PhotoBean info) {
		mWechatPhotoInfoDao.update(info);
	}

	/**
	 * 执行更新(多条)
	 **/
	public void photoExecUpdate(List<PhotoBean> info) {
		mWechatPhotoInfoDao.updateInTx(info);
	}

	/**
	 * 按用户名查询
	 */
	public List<PhotoBean> photoQueryUserBy(String UserId) {
		return mWechatPhotoInfoDao.queryBuilder()
				.where(WechatPhotoInfoDao.Properties.Userid.eq(UserId))
				.orderDesc(WechatPhotoInfoDao.Properties.Id).list();
	}

	/**
	 * 按Id查询
	 */
	public PhotoBean queryUserById(long id) { return mWechatPhotoInfoDao.load(id); }
	/**
	 * 查询所有用户
	 */
	public List<PhotoBean> PhotoQueryAllUser() {
		return mWechatPhotoInfoDao.queryBuilder()
				.where(WechatPhotoInfoDao.Properties.Back1.isNull())
				.orderDesc(WechatPhotoInfoDao.Properties.Id)
				.list();
	}
	/**
	 * 增量查询
	 */	
	public List<PhotoBean> queryNewPhoto(long number) {
		return mWechatPhotoInfoDao.queryBuilder()
				.where(WechatPhotoInfoDao.Properties.Id.gt(number))
				.orderDesc(WechatPhotoInfoDao.Properties.Id)
				.list();
	}
	
	/**
	 * 查询照片总个数
	 * @return
     */
	public long queryAllUserCount() {
		return mWechatPhotoInfoDao.count();
	}
	/**
	 * 查询剩余照片个数
	 * @return
     */
	public long queryExpiredUserCount() { 
		return mWechatPhotoInfoDao.queryBuilder()
               .where(WechatPhotoInfoDao.Properties.Back1.isNull())
               .count(); 
		}
	
	/**
	 * 查询特定用户的照片个数
	 * @param userName
	 * @return
     */
	public long queryEveryUserPhotoCount(String userName) {
		return mWechatPhotoInfoDao.queryBuilder()
				                  .where(WechatPhotoInfoDao.Properties.Userid.eq(userName))
				                  .buildCount()
				                  .count();
	}

	/**
	 * 分页加载
	 * @param limit
	 * @return
     */
	public List<PhotoBean> queryUserPhotoByPage(int offset ,int limit){
		return mWechatPhotoInfoDao.queryBuilder().offset(offset)
								  .limit(limit)
								  .orderDesc(WechatPhotoInfoDao.Properties.Id)
								  .list();
	}

}
