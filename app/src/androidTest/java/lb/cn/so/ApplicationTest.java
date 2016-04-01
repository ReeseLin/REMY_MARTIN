package lb.cn.so;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import lb.cn.so.Utils.DBOpenHelper;
import lb.cn.so.bean.ApplyJoinUser;
import lb.cn.so.bean.Chatroom;
import lb.cn.so.service.ChatroomService;
import lb.cn.so.service.RequestJoinRoomService;
import lb.cn.so.service.RequestJoinUserService;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends AndroidTestCase {

    public void testCreateDB() throws Exception {
        boolean what = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdpath = Environment.getExternalStorageDirectory().getPath();
            File file1 = new File(sdpath, "110CCCCCC");
            File file = new File(file1, "my.db");
            if (!file1.exists()) {// 目录存在返回false
                file1.mkdirs();// 创建一个目录
            }
            if (!file.exists()) {// 文件存在返回false
                try {
                    file.createNewFile();//创建文件
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
        }
    }

    public void testCreateDB2() throws Exception {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(getContext());
        dbOpenHelper.getWritableDatabase();
    }

    public void testChatRoomService() throws Exception {
        Chatroom chatroom = new Chatroom("111222", "iTest", "1");
        ChatroomService chatroomService = new ChatroomService(getContext());
        chatroomService.saveChatRoom(chatroom);
    }

    public void testGetChatRoomS() throws Exception {
        ChatroomService chatroomService = new ChatroomService(getContext());
        List<Chatroom> chatrooms = chatroomService.getScrollData(0, 3);
        List<Chatroom> chatrooms2 = chatroomService.getScrollData(1, 3);
    }

    public void testGetChatRoomCount() throws Exception {
        ChatroomService chatroomService = new ChatroomService(getContext());
        Long count = chatroomService.getChatRoomCount();
        Long count1 = chatroomService.getChatRoomCount();
    }

    public void testRequestJoinSave() throws Exception {
        RequestJoinRoomService requestJoinRoomService = new RequestJoinRoomService(getContext());
        requestJoinRoomService.saveJoinRoom("11111111111111");
    }

    public void testRequestJoinGet() throws Exception {
        RequestJoinRoomService requestJoinRoomService = new RequestJoinRoomService(getContext());
        List<String> msg = requestJoinRoomService.getRequestRoomID();
        List<String> msg1 = requestJoinRoomService.getRequestRoomID();
        List<String> msg2 = requestJoinRoomService.getRequestRoomID();
    }

    public void testRequestJoinDelete() throws Exception {
        RequestJoinRoomService requestJoinRoomService = new RequestJoinRoomService(getContext());
        requestJoinRoomService.delete("88888886666");
    }

    public void testRequestJoinUserSave() throws Exception {
        ApplyJoinUser applyJoinUser = new ApplyJoinUser("9999999", "110", "595959");
        RequestJoinUserService requestJoinUserService = new RequestJoinUserService(getContext());
        requestJoinUserService.saveJoinUser(applyJoinUser);
    }

    public void testRequestJoinUserGet() throws Exception {
        // ApplyJoinUser applyJoinUser = new ApplyJoinUser("1231","8888","2435");
        RequestJoinUserService requestJoinUserService = new RequestJoinUserService(getContext());
        List<ApplyJoinUser> joinUsers = requestJoinUserService.getJoinUsers();
        List<ApplyJoinUser> joinUsers1 = requestJoinUserService.getJoinUsers();
        List<ApplyJoinUser> joinUsers2 = requestJoinUserService.getJoinUsers();
    }

    public void testRequestJoinUserDelete() throws Exception {
        RequestJoinUserService requestJoinUserService = new RequestJoinUserService(getContext());
        ApplyJoinUser applyJoinUser = new ApplyJoinUser("9999999", "110", "595959");
        requestJoinUserService.delete(applyJoinUser);
    }

    public void testRequestJoinUserSelect() throws Exception {
        RequestJoinUserService requestJoinUserService = new RequestJoinUserService(getContext());
        ApplyJoinUser applyJoinUser = new ApplyJoinUser("135", "44", null);
        requestJoinUserService.isAlreadyApply(applyJoinUser);
    }
}