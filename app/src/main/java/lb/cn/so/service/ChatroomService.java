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
 * Date:2016/3/29
 * Des：对于数据库ChatRoom表的操作查询
 */
public class ChatroomService {


    private DBOpenHelper dbOpenHelper;

    public ChatroomService(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }

    //添加聊天室
    public void saveChatRoom(Chatroom chatroom) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into chat_room(chatroomid, chatroomname, isagree) values(?,?,?)",
                new Object[]{chatroom.getChatroomid(), chatroom.getChatroomname(), chatroom.getIsagree()});
    }

    /**
     * 查询聊天室的数量
     *
     * @return
     */
    public Long getChatRoomCount() {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select count(*) from chat_room where isagree=1", null);
        cursor.moveToFirst();
        long result = cursor.getLong(0);
        cursor.close();
        return result;
    }

    //查询聊天室

    /**
     * 分页获取记录
     *
     * @param offset    跳过前面多少条记录
     * @param maxResult 每页获取多少条记录
     * @return
     */
    public List<Chatroom> getScrollData(int offset, int maxResult) {
        List<Chatroom> chatrooms = new ArrayList<Chatroom>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from chat_room where isagree = 1 order by chatroomid asc limit ?,?",
                new String[]{String.valueOf(offset), String.valueOf(maxResult)});
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int chatroomid = cursor.getInt(cursor.getColumnIndex("chatroomid"));
            String chatroomname = cursor.getString(cursor.getColumnIndex("chatroomname"));
            String isagree = cursor.getString(cursor.getColumnIndex("isagree"));
            chatrooms.add(new Chatroom(Integer.toString(chatroomid), chatroomname, isagree));
        }
        cursor.close();
        return chatrooms;
    }


    public void updateIsAgree(String chatroomid){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("update chat_room set isagree = 1 where chatroomid=?",
                new Object[]{chatroomid});
    }
}
