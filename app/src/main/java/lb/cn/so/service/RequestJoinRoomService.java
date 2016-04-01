package lb.cn.so.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lb.cn.so.Utils.DBOpenHelper;
import lb.cn.so.bean.Chatroom;

/**
 * Creater :ReeseLin
 * Email:172053362@qq.com
 * Date:2016/3/31
 * Des：查询是否有请求加入聊天室
 */
public class RequestJoinRoomService {


    private DBOpenHelper dbOpenHelper;

    public RequestJoinRoomService(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }

    public void saveJoinRoom(String requestRoomID) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into request_join_room(requestroomid) values(?)",
                new Object[]{requestRoomID});
    }

    public List<String> getRequestRoomID(){
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        List<String> rooms = new ArrayList<String>();
        Cursor cursor = db.rawQuery("select * from request_join_room",null);
        while (cursor.moveToNext()){
            String chatroomid = cursor.getString(cursor.getColumnIndex("requestroomid"));
            rooms.add(chatroomid);
        }
        cursor.close();
        return rooms;
    }

    public void delete(String requestRoomID){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from request_join_room where requestroomid=?", new Object[]{requestRoomID});
    }

}
