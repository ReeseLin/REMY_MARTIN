package lb.cn.so.Thread;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lb.cn.so.MainConfig.MainUser;
import lb.cn.so.MainConfig.SettingFile;
import lb.cn.so.Utils.HttpUtils;
import lb.cn.so.bean.QueryMsg;
import lb.cn.so.service.ChatroomService;
import lb.cn.so.service.RequestJoinRoomService;
/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/4/9
 *  Des：坚持是否被允许加入聊天室的线程，允许在Service中
 */
public class ChackBeAgreeThread implements Runnable {

    //操作数据库的Service
    private RequestJoinRoomService requestJoinRoomService;
    private ChatroomService chatroomService;

    public ChackBeAgreeThread(Context context) {
        this.requestJoinRoomService = new RequestJoinRoomService(context);
        this.chatroomService = new ChatroomService(context);
    }

    @Override
    public void run() {
        List<String> chatrooms = requestJoinRoomService.getRequestRoomID();
        if (chatrooms.size() > 0) {
            try {
                check(chatrooms);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求指定的聊天室，查看是否被允许
     * @param chatrooms
     * @throws Exception
     */
    private void check(List<String> chatrooms) throws Exception {
        for (String chatroomid : chatrooms) {
            QueryMsg queryMsg = new QueryMsg();
            queryMsg.setMethodName("CheckBeAgree");
            queryMsg.iniDateTable();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userid", MainUser.userid);
            map.put("chatroomid", chatroomid);
            queryMsg.getDataTable().add(map);
            QueryMsg qm = HttpUtils.postQueryMsgAndGetQueryMsg(SettingFile.postUrl, queryMsg);
            Map<String, Object> datatable  = qm.getDataTable().get(0);
            String look  = (String)datatable.get("isagree");
            if("1".equals(datatable.get("isagree"))){
                requestJoinRoomService.delete(chatroomid);
                chatroomService.updateIsAgree(chatroomid);
            }
        }
    }
}
