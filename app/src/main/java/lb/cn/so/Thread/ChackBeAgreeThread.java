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

public class ChackBeAgreeThread implements Runnable {


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
