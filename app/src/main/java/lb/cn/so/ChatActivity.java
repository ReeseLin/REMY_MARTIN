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
 * Created by Lin on 2016/3/31.
 */
public class ChatActivity extends Activity implements View.OnClickListener {

    private TextView chatroomidTV;
    private TextView chatroomnameTV;
    private EditText sendMessageET;
    private Button commitBut;
    private ListView chatMessageListView;

    private String chatroomname;
    private String chatroomid;

    private MyHandler myhandler;

    private ChatMessageAdapter chatMessageAdapter;
    private List<ChatroomMessage> messages = new ArrayList<ChatroomMessage>();

    public static final int applyRoom_what = 7;

    public static final int chat_showTime_what = 8;

    MyTimeTask myTimeTask;

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

        Intent intent = getIntent();//获取用于激活它的意图对象

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

    private void show() {
        int chatroomidint = Integer.parseInt(chatroomid);
        messages = messageService.getScrollMessageData(chatroomidint,0,100);
        chatMessageAdapter.refresh(messages);

    }

    //自定义handler，只是一个定时的功能去调用show()方法
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case applyRoom_what:
                    checkSendResult(msg);
                    show();
                    break;
                case chat_showTime_what:
                    show();
                    break;
            }
            super.handleMessage(msg);
        }

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
                    Toast.makeText(ChatActivity.this, "解析日期出错", Toast.LENGTH_LONG);
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

    private void sendMessage() {
        QueryMsg queryMsg = assembleQueryMsg();
        SubmitQueryMsgThread submitQueryMsgThread = new SubmitQueryMsgThread(myhandler, SettingFile.postUrl, applyRoom_what);
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
