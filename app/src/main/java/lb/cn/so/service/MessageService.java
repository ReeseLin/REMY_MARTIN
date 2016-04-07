package lb.cn.so.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        String createtime =format.format(chatroomMessage.getCreatetime());
        sqLiteDatabase.execSQL("insert into chat_room_message(chatroomid ,message ,createtime ,sendername ,senderid ,isread ) values(?,?,?,?,?,?)",
                new Object[]{chatroomMessage.getChatroomid(), chatroomMessage.getMessage(),createtime,
                        chatroomMessage.getSendername(), chatroomMessage.getSenderid(), chatroomMessage.getIsread()});
    }


    public List<ChatroomMessage> getScrollMessageData(int chatroomid ,int offset, int maxResult) throws Exception {
        List<ChatroomMessage> chatroomMessages = new ArrayList<ChatroomMessage>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from chat_room_message where chatroomid =? order by createtime ASC limit ?,?",
                new String[]{String.valueOf(chatroomid),String.valueOf(offset), String.valueOf(maxResult)});
        while (cursor.moveToNext()) {
            //int chatroomid = cursor.getInt(cursor.getColumnIndex("chatroomid"));
            int senderid = cursor.getInt(cursor.getColumnIndex("senderid"));

            String message = cursor.getString(cursor.getColumnIndex("message"));
            String time = cursor.getString(cursor.getColumnIndex("createtime"));
            String sendername = cursor.getString(cursor.getColumnIndex("sendername"));
            String isread = cursor.getString(cursor.getColumnIndex("isread"));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date createtime = format.parse(time);

            ChatroomMessage cm = new ChatroomMessage(chatroomid + "", message, senderid + "",
                    createtime, sendername, isread);

            chatroomMessages.add(cm);
        }
        cursor.close();
        return chatroomMessages;
    }


}
