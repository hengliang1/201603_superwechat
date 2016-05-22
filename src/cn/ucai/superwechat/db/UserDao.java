package cn.ucai.superwechat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.bean.User;

/**
 * 使用SQLiteOpenHelper方法创建数据库
 * SQLiteOpenHelper是一个抽象的数据库操作类，首先执行的是OnCreate
 * Created by sks on 2016/5/19.
 */
public class UserDao extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "user";

    public UserDao(Context context) {
        super(context, "user.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = " DROP TABLE IF EXISTS " + I.User.TABLE_NAME + "" +
                " CREATE TABLE " + I.User.TABLE_NAME +
                I.User.USER_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                I.User.USER_NAME + " TEXT NOT NULL," +
                I.User.PASSWORD + " TEXT NOT NULL," +
                I.User.NICK + "  TEXT NOT NULL," +
                I.User.UN_READ_MSG_COUNT + " INTEGER DEFAULT 0"+");";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * SQLiteOpenHelper封装了一个insert方法可以方便我们执行插入行为
     * @param user
     * @return
     */
    public boolean addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(I.User.USER_ID, user.getMUserId());
        values.put(I.User.USER_NAME, user.getMUserName());
        values.put(I.User.PASSWORD, user.getMUserPassword());
        values.put(I.User.NICK, user.getMUserNick());
        values.put(I.User.UN_READ_MSG_COUNT, user.getMUserUnreadMsgCount());
        //调用getWritableDatabase方法真正创建一个数据库
        SQLiteDatabase db = getWritableDatabase();
        long insert = db.insert(I.User.TABLE_NAME, null, values);
        return insert > 0;
    }

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    public User findUserByName(String username) {
        //创建数据库
        SQLiteDatabase db = getReadableDatabase();
        //获取sql语句
        String sql = "select * from " + TABLE_NAME + " where " + I.User.USER_NAME + "=?";
        //利用光标Cursor，Cursor指向当前的数据记录，然后可以从光标中获取相应的数据,执行本地sql语句查询
        Cursor c = db.rawQuery(sql, new String[]{username});
        if (c.moveToNext()) {
            int uid = c.getInt(c.getColumnIndex(I.User.USER_ID));
            String nick = c.getString(c.getColumnIndex(I.User.NICK));
            String password = c.getString(c.getColumnIndex(I.User.PASSWORD));
            int unReaderMsgCount = c.getInt(c.getColumnIndex(I.User.UN_READ_MSG_COUNT));
            return new User(uid, username, password, nick, unReaderMsgCount);
        }
        c.close();
        return null;
    }
    /**
     * 更新用户
     * @param user
     * @return
     */
    public boolean updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(I.User.USER_ID, user.getMUserId());
        values.put(I.User.NICK, user.getMUserNick());
        values.put(I.User.PASSWORD, user.getMUserPassword());
        values.put(I.User.UN_READ_MSG_COUNT, user.getMUserUnreadMsgCount());
        SQLiteDatabase db = getWritableDatabase();
        long insert = db.update(I.User.TABLE_NAME, values, "where " + I.User.USER_NAME + "=?", new String[]{user.getMUserName()});
        return insert > 0;
    }


}
