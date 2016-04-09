package lb.cn.so.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lb.cn.so.MainConfig.MainUser;
import lb.cn.so.Utils.DBOpenHelper;
import lb.cn.so.bean.Chatroom;
import lb.cn.so.bean.ChatroomMessage;

/**
 * Creater :ReeseLin
 * Email:172053362@qq.com
 * Date:2016/4/7
 * Des：信息类
 */
public class MessageService {


    private DBOpenHelper dbOpenHelper;

    public MessageService(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }

    public void saveChatroomMessage(ChatroomMessage chatroomMessage) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createtime = format.format(chatroomMessage.getCreatetime());
        sqLiteDatabase.execSQL("insert into chat_room_message(chatroomid ,message ,createtime ,sendername ,senderid ,isread ) values(?,?,?,?,?,?)",
                new Object[]{chatroomMessage.getChatroomid(), chatroomMessage.getMessage(), createtime,
                        chatroomMessage.getSendername(), chatroomMessage.getSenderid(), chatroomMessage.getIsread()});
    }


    public List<ChatroomMessage> getScrollMessageData(int chatroomid, int offset, int maxResult){
        List<ChatroomMessage> chatroomMessages = new ArrayList<ChatroomMessage>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from chat_room_message where chatroomid =? order by createtime ASC limit ?,?",
                new String[]{String.valueOf(chatroomid), String.valueOf(offset), String.valueOf(maxResult)});
        while (cursor.moveToNext()) {
            int senderid = cursor.getInt(cursor.getColumnIndex("senderid"));
            String message = cursor.getString(cursor.getColumnIndex("message"));
            String time = cursor.getString(cursor.getColumnIndex("createtime"));
            String sendername = cursor.getString(cursor.getColumnIndex("sendername"));
            String isread = cursor.getString(cursor.getColumnIndex("isread"));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date createtime = null;
            try {
                createtime = format.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ChatroomMessage cm = new ChatroomMessage(chatroomid + "", message, senderid + "",
                    createtime, sendername, isread);

            chatroomMessages.add(cm);
        }
        cursor.close();
        return chatroomMessages;
    }


    public String getLastTimeString() {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT MAX(createtime) AS lasttime FROM  chat_room_message where senderid != ?;", new String[]{MainUser.userid});
        cursor.moveToFirst();
        String time = cursor.getString(0);
        cursor.close();
        return time;
    }

}
