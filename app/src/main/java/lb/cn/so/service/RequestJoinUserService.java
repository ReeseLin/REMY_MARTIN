package lb.cn.so.service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lb.cn.so.Utils.DBOpenHelper;
import lb.cn.so.bean.ApplyJoinUser;
/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/4/9
 *  Des：记录想申请加入聊天室的用户
 */
public class RequestJoinUserService {

    private DBOpenHelper dbOpenHelper;

    public RequestJoinUserService(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
    }

    /**
     * 保存申请用户
     * @param applyJoinUser
     */
    public void saveJoinUser(ApplyJoinUser applyJoinUser) {
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getWritableDatabase();
        sqLiteDatabase.execSQL("insert into request_join_user(requesterid,requestroomid,requestername) values(?,?,?)",
                new Object[]{applyJoinUser.getRequesterid(), applyJoinUser.getRequestroomid(),applyJoinUser.getRequestername()});
    }

    /**
     * 获取申请加入聊天室用户列表
     * @return
     */
    public List<ApplyJoinUser> getJoinUsers() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        List<ApplyJoinUser> ApplyJoinUsers = new ArrayList<ApplyJoinUser>();
        Cursor cursor = db.rawQuery("select * from request_join_user", null);
        while (cursor.moveToNext()) {
            String requesterid = cursor.getString(cursor.getColumnIndex("requesterid"));
            String requestroomid = cursor.getString(cursor.getColumnIndex("requestroomid"));
            String requestername = cursor.getString(cursor.getColumnIndex("requestername"));
            ApplyJoinUser applyJoinUser = new ApplyJoinUser(requesterid,requestroomid,requestername);
            ApplyJoinUsers.add(applyJoinUser);
        }
        cursor.close();
        return ApplyJoinUsers;
    }

    /**
     * 查询改用户是否已经保存到申请加入聊天室列表，放置重复录入
     * @param applyJoinUser
     * @return
     */
    public Boolean isAlreadyApply(ApplyJoinUser applyJoinUser) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        List<ApplyJoinUser> ApplyJoinUsers = new ArrayList<ApplyJoinUser>();
        Cursor cursor = db.rawQuery("select count(*) as applytime from request_join_user where requesterid=? and requestroomid =?",
                new String[]{(String)applyJoinUser.getRequesterid(),(String)applyJoinUser.getRequestroomid()});
        cursor.moveToFirst();
        int applytime = cursor.getInt(cursor.getColumnIndex("applytime"));
        cursor.close();
        if(applytime>0){
            return false;
        }
        return true;
    }

    /**
     * 删除申请用户
     * @param applyJoinUser
     */
    public void delete(ApplyJoinUser applyJoinUser) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from request_join_user where requesterid=? and requestroomid =?",
                new Object[]{applyJoinUser.getRequesterid(),applyJoinUser.getRequestroomid()});
    }

}
