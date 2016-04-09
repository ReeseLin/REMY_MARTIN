package lb.cn.so;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import lb.cn.so.MainConfig.MainUser;
import lb.cn.so.MainConfig.SettingFile;
import lb.cn.so.Thread.SubmitQueryMsgThread;
import lb.cn.so.Utils.ThreadPool;
import lb.cn.so.adapter.ChatMessageAdapter;
import lb.cn.so.bean.ChatroomMessage;
import lb.cn.so.bean.QueryMsg;
import lb.cn.so.service.MessageService;

/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/4/9
 *  Des：聊天界面
 */
public class ChatActivity extends Activity implements View.OnClickListener {

    private String chatroomname;
    private String chatroomid;

    //获取几个垃圾小控件
    private TextView chatroomidTV;
    private TextView chatroomnameTV;
    private EditText sendMessageET;
    private Button commitBut;

    //聊天界面专属Handler
    private MyHandler myhandler;

    //界面的listView几个参数
    private ListView chatMessageListView;
    private ChatMessageAdapter chatMessageAdapter;
    private List<ChatroomMessage> messages = new ArrayList<ChatroomMessage>();

    //Handler识别的what值
    public static final int sendMessage_what = 7;
    public static final int chat_showTime_what = 8;

    //定时任务，用于更新界面的ListView内容
    MyTimeTask myTimeTask;

    //获取聊天室消息的Service
    private MessageService messageService = new MessageService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatroomidTV = (TextView) findViewById(R.id.chatroomid);
        chatroomnameTV = (TextView) findViewById(R.id.chatroomname);
        sendMessageET = (EditText) findViewById(R.id.contentEditText);
        commitBut = (Button) findViewById(R.id.commitButton);
        chatMessageListView = (ListView) findViewById(R.id.chatMessageListView);

        myhandler = new MyHandler();

        //这步是获取从上一个Activity跳转过来后所携带的消息
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        chatroomname = bundle.getString("chatroomname");
        chatroomid = bundle.getString("chatroomid");

        commitBut.setOnClickListener(this);

        chatroomidTV.setText("ID:" + chatroomid);
        chatroomnameTV.setText("" + chatroomname);

        myTimeTask = new MyTimeTask(myhandler);
        ThreadPool.timeThreadPool.scheduleWithFixedDelay(myTimeTask, 0, 3, TimeUnit.SECONDS);

        chatMessageAdapter = new ChatMessageAdapter(this,messages,R.layout.chatmessage_item);
        chatMessageListView.setAdapter(chatMessageAdapter);
        show();
    }

    /**
     * 刷新ListView调用的
     */
    private void show() {
        //TODO 有空的话要做一个查询数量的方法，不用每次都查询整个消息内容
        int chatroomidint = Integer.parseInt(chatroomid);
        messages = messageService.getScrollMessageData(chatroomidint,0,100);
        chatMessageAdapter.refresh(messages);

    }

    /**
     * 定时发送空消息去触发Handler执行刷新动作
     */
    private class MyTimeTask extends TimerTask {
        Handler handler;
        public MyTimeTask(Handler handler) {
            this.handler = handler;
        }
        @Override
        public void run() {
            handler.sendEmptyMessage(chat_showTime_what);
        }
    }

    /**
     * 本页面的专属Handler
     */
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case sendMessage_what:
                    checkSendResult(msg);
                    show();
                    break;
                case chat_showTime_what:
                    show();
                    break;
            }
            super.handleMessage(msg);
        }

        /**
         *获取到发送消息的返回结果，如果成功记录到数据库中
         * @param msg
         */
        private void checkSendResult(Message msg) {
            Bundle bata = msg.getData();
            //获得数据
            QueryMsg qm = (QueryMsg) bata.getSerializable(SubmitQueryMsgThread.RESPONSE_MSG);
            if ("0".equals(qm.getResult())) {
                //保存到数据库
                List<Map<String, Object>> datatable = qm.getDataTable();
                Map<String, Object> dataMap = datatable.get(0);
                String chatroomid = (String) dataMap.get("chatroomid");
                String message = (String) dataMap.get("message");
                String time = (String) dataMap.get("createtime");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date createtime = null;
                try {
                    createtime = format.parse(time);
                } catch (ParseException e) {
                    //TODO 如果可以需要想个办法做一个记录的类
                }
                ChatroomMessage cm = new ChatroomMessage(chatroomid,
                        message, MainUser.userid, createtime, MainUser.username, "1");
                messageService.saveChatroomMessage(cm);
                Toast.makeText(ChatActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commitButton:
                sendMessage();
                break;
        }
    }

    /**
     * 发送消息
     */
    private void sendMessage() {
        QueryMsg queryMsg = assembleQueryMsg();
        SubmitQueryMsgThread submitQueryMsgThread = new SubmitQueryMsgThread(myhandler, SettingFile.postUrl, sendMessage_what);
        submitQueryMsgThread.addQueryMsg(queryMsg);
        ThreadPool.timeThreadPool.scheduleWithFixedDelay(submitQueryMsgThread, 0, 3, TimeUnit.SECONDS);
    }

    @NonNull
    private QueryMsg assembleQueryMsg() {
        String message = sendMessageET.getText().toString();
        sendMessageET.setText("");
        QueryMsg queryMsg = new QueryMsg();
        queryMsg.setMethodName("SendChatRoomMsg");
        queryMsg.iniDateTable();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("chatroomid", chatroomid);
        map.put("username", MainUser.username);
        map.put("userid", MainUser.userid);
        map.put("message", message);
        queryMsg.getDataTable().add(map);
        return queryMsg;
    }
}
