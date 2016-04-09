package lb.cn.so.Thread;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lb.cn.so.MainConfig.MainUser;
import lb.cn.so.MainConfig.SettingFile;
import lb.cn.so.Utils.HttpUtils;
import lb.cn.so.bean.ChatroomMessage;
import lb.cn.so.bean.QueryMsg;
import lb.cn.so.service.MessageService;
/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/4/9
 *  Des：获取用户被允许加入的聊天室的消息
 */
public class GetMessageThread implements Runnable {

    private MessageService messageService;

    public GetMessageThread(Context context) {
        this.messageService = new MessageService(context);
    }

    @Override
    public void run() {
        try {
            getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取消息，用非本用户最后一次时间为界限获取
     * @throws Exception
     */
    private void getMessage() throws Exception {
        String lasttime = messageService.getLastTimeString();
        if(lasttime==null){
            //软件第一次执行成功的时间，有些意义
            lasttime="2016-04-09 15:15:15";
        }
        QueryMsg queryMsg = new QueryMsg();
        queryMsg.setMethodName("GetChatRoomMsg");
        queryMsg.iniDateTable();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userid", MainUser.userid);
        map.put("lasttime", lasttime);
        queryMsg.getDataTable().add(map);
        QueryMsg qm = HttpUtils.postQueryMsgAndGetQueryMsg(SettingFile.postUrl, queryMsg);
        if("0".equals(qm.getResult())&&qm.getDataTable()!=null){
            List<Map<String, Object>> messages =  qm.getDataTable();
            for(Map<String, Object> message:messages){
                String chatroomid = (String)message.get("chatroomid");
                String content = (String)message.get("message");
                String senderid = (String)message.get("senderid");
                String time = (String)message.get("createtime");
                String sendername = (String)message.get("sendername");
                String isread = "1";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date createtime = format.parse(time);
                ChatroomMessage cm = new ChatroomMessage(chatroomid,content,senderid,createtime,sendername,isread);
                messageService.saveChatroomMessage(cm);
            }
        }
    }
}
