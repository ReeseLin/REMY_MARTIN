package lb.cn.so.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/3/29
 *  Des：数据库操作的类
 */
public class DBOpenHelper extends SQLiteOpenHelper{

    public final static int VERSION = 1;
    public static final String DATABASE_NAME = "SODatabase.db";

    public DBOpenHelper(Context contex) {
        super(new DatabaseContext(contex), DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      //  db.execSQL("CREATE TABLE chat_room (id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,chatroomid integer NOT NULL UNIQUE,chatroomname varchar DEFAULT 聊天室,isagree varchar NOT NULL DEFAULT 0)");
        db.execSQL("CREATE TABLE chat_room (id integer PRIMARY KEY AUTOINCREMENT,chatroomid integer,chatroomname varchar DEFAULT 聊天室,isagree varchar DEFAULT 0,hasMessage integer DEFAULT 0)");
        db.execSQL("CREATE TABLE chat_room_message (id integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,chatroomid integer NOT NULL,message text,createdate date,sender varchar,senderid integer,isread varchar DEFAULT 0)");
        db.execSQL("CREATE TABLE request_join_room (requestroomid integer)");
        db.execSQL("CREATE TABLE request_join_user (requesterid integer,requestroomid integer,requestername varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
