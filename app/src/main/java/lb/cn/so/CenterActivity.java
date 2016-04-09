package lb.cn.so;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import lb.cn.so.MainConfig.MainUser;
import lb.cn.so.MainConfig.SettingFile;
import lb.cn.so.Thread.SubmitQueryMsgThread;
import lb.cn.so.Utils.ThreadPool;
import lb.cn.so.adapter.ChatroomAdapter;
import lb.cn.so.bean.ApplyJoinUser;
import lb.cn.so.bean.Chatroom;
import lb.cn.so.bean.QueryMsg;
import lb.cn.so.service.ChatroomService;
import lb.cn.so.service.RequestJoinRoomService;
import lb.cn.so.service.RequestJoinUserService;

/**
 * Creater :ReeseLin
 * Email:172053362@qq.com
 * Date:2016/3/29
 * Des：中心界面，在自动登录或注册好用户后的主要界面
 */
public class CenterActivity extends Activity implements View.OnClickListener {

    //几个小小的UI控件
    private TextView usernameTextView;
    private Button createRoomButtom;
    private Button applyRoomButtom;
    private Button messageButtom;
    private ListView chatroomsListView;


    //等待的对话框
    private ProgressDialog centerProgressDialog;

    //创建和申请的dialog
    private AlertDialog centerDialog;
    private EditText createRoomName;

    //线程交互的handler
    private MyHandler myHandler;

    private String chatroomid;
    private String chatroomname;


    //ListView使用的一些
    private List<Chatroom> chatrooms = new ArrayList<Chatroom>();
    private List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
    public static Long dataCount;
    private ChatroomAdapter chatroomAdapter;

    //与数据库交互的service
    private ChatroomService chatroomService = new ChatroomService(this);
    private RequestJoinRoomService requestJoinRoomService = new RequestJoinRoomService(this);
    private RequestJoinUserService requestJoinUserService = new RequestJoinUserService(this);

    //标志的what
    public static final int createRoom_what = 2;
    public static final int center_showTime_what = 3;
    public static final int applyRoom_what = 4;

    //定时的一个类，用来主要功能是刷新ListView，TODO 后续应该要改掉
    MyTimeTask myTimeTask;

    private class MyTimeTask extends TimerTask {
        Handler handler;

        public MyTimeTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendEmptyMessage(center_showTime_what);
            handler.sendEmptyMessage(center_showTime_what);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);
        //获取控件
        usernameTextView = (TextView) findViewById(R.id.showUserNameTextView);
        createRoomButtom = (Button) findViewById(R.id.createRoomButtom);
        applyRoomButtom = (Button) findViewById(R.id.applyRoomButtom);
        chatroomsListView = (ListView) findViewById(R.id.chatRoomListView);
        messageButtom = (Button) findViewById(R.id.messageButtom);
        //显示Username
        usernameTextView.setText(MainUser.username);

        myHandler = new MyHandler();

        //创建两个按键的监听事件
        createRoomButtom.setOnClickListener(this);
        applyRoomButtom.setOnClickListener(this);
        messageButtom.setOnClickListener(this);

        //开线程刷新ListView
        myTimeTask = new MyTimeTask(myHandler);
        ThreadPool.timeThreadPool.scheduleWithFixedDelay(myTimeTask, 0, 15, TimeUnit.SECONDS);

        //分配的一个适配器给ListView TODO 这个必须改为自己的适配器

        chatroomAdapter = new ChatroomAdapter(this, chatrooms, R.layout.chatroomitem);
        chatroomsListView.setOnItemClickListener(new ItemClickListener());
        chatroomsListView.setAdapter(chatroomAdapter);
    }


    private final class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lView = (ListView) parent;
            /* 自定义适配器*/
            Chatroom chatroom = (Chatroom) lView.getItemAtPosition(position);
            Intent intent = new Intent(CenterActivity.this, ChatActivity.class);//激活组件,显示意图:明确指定了组件名称的意图叫显示意图
            Bundle bundle = new Bundle();
            bundle.putString("chatroomname", chatroom.getChatroomname());
            bundle.putString("chatroomid", chatroom.getChatroomid());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    private void show() {
        if (dataCount != chatroomService.getChatRoomCount()) {
            dataCount = chatroomService.getChatRoomCount();
            chatrooms = chatroomService.getScrollData(0, 20);
            chatroomAdapter.refresh(chatrooms);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createRoomButtom:
                createRoomName = new EditText(this);
                centerDialog = new AlertDialog.Builder(this).setTitle("设置聊天室名字：").setView(createRoomName).
                        setPositiveButton("确定", new createRoomListen()).
                        setNegativeButton("取消", null).show();
                break;
            case R.id.applyRoomButtom:
                createRoomName = new EditText(this);
                centerDialog = new AlertDialog.Builder(this).setTitle("输入申请聊天室ID：").setView(createRoomName).
                        setPositiveButton("确定", new applyRoomListen()).
                        setNegativeButton("取消", null).show();
                break;
            case R.id.messageButtom:
                Intent intent = new Intent(CenterActivity.this, MessageActivity.class);//激活组件,显示意图:明确指定了组件名称的意图叫显示意图
                startActivity(intent);
                break;
        }
    }

    /**
     * 创建聊天室的监听类
     */
    class createRoomListen implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            QueryMsg queryMsg = assembleCreateRoomQuery();
            SubmitQueryMsgThread submitQueryMsgThread = new SubmitQueryMsgThread(myHandler, SettingFile.postUrl, createRoom_what);
            submitQueryMsgThread.addQueryMsg(queryMsg);
            ThreadPool.timeThreadPool.scheduleWithFixedDelay(submitQueryMsgThread, 0, 3, TimeUnit.SECONDS);
            centerProgressDialog = ProgressDialog.show(CenterActivity.this, null, "正在创建......", true);
        }

        private QueryMsg assembleCreateRoomQuery() {
            chatroomname = createRoomName.getText().toString();
            QueryMsg queryMsg = new QueryMsg();
            queryMsg.setMethodName("CreateChatRoom");
            queryMsg.iniDateTable();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userid", MainUser.userid);
            map.put("username", MainUser.username);
            map.put("chatroomname", chatroomname);
            queryMsg.getDataTable().add(map);
            return queryMsg;
        }
    }

    /**
     * 申请加入聊天室的类
     */
    class applyRoomListen implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            QueryMsg queryMsg = assembleApplyRoomQuery();

            SubmitQueryMsgThread submitQueryMsgThread = new SubmitQueryMsgThread(myHandler, SettingFile.postUrl, applyRoom_what);
            submitQueryMsgThread.addQueryMsg(queryMsg);
            ThreadPool.timeThreadPool.scheduleWithFixedDelay(submitQueryMsgThread, 0, 3, TimeUnit.SECONDS);
            centerProgressDialog = ProgressDialog.show(CenterActivity.this, null, "正在申请......", true);
        }

        private QueryMsg assembleApplyRoomQuery() {
            chatroomid = createRoomName.getText().toString();
            QueryMsg queryMsg = new QueryMsg();
            queryMsg.setMethodName("ApplyJoinChatRoom");
            queryMsg.iniDateTable();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userid", MainUser.userid);
            map.put("chatroomid", chatroomid);
            queryMsg.getDataTable().add(map);
            return queryMsg;
        }
    }

    /**
     * CenterActivity的Handler
     */
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case createRoom_what:
                    createRoom(msg);
                    show();
                    break;
                case center_showTime_what:
                    show();
                    showMessage();
                    break;
                case applyRoom_what:
                    applyRoom(msg);
                    break;
            }
            super.handleMessage(msg);
        }

        private void showMessage() {
            List<ApplyJoinUser> applyJoinUsers = requestJoinUserService.getJoinUsers();
            int applyCount = applyJoinUsers.size();
            if (applyCount > 0) {
                messageButtom.setVisibility(View.VISIBLE);
            }else{
                messageButtom.setVisibility(View.GONE);
            }
        }

        /**
         * 当接收到创建聊天室线程返回信息后的操作
         *
         * @param msg
         */
        private void createRoom(Message msg) {
            Bundle bata = msg.getData();
            //获得数据
            QueryMsg qm = (QueryMsg) bata.getSerializable(SubmitQueryMsgThread.RESPONSE_MSG);
            if ("0".equals(qm.getResult())) {
                List<Map<String, Object>> datatable = qm.getDataTable();
                Map<String, Object> dataMap = datatable.get(0);
                chatroomid = String.valueOf(dataMap.get("chatroomid"));
                centerProgressDialog.dismiss();
                chatroomService.saveChatRoom(new Chatroom(chatroomid, chatroomname, "1"));
                new AlertDialog.Builder(CenterActivity.this).setTitle("创建成功").
                        setMessage("聊天室名称：" + chatroomname + "\n" + "聊天室id为：" + chatroomid).setPositiveButton("确定", null).show();
            }
        }

        /**
         * 当接收到申请聊天室线程返回信息后的操作
         *
         * @param msg
         */
        private void applyRoom(Message msg) {
            Bundle bata = msg.getData();
            //获得数据
            QueryMsg qm = (QueryMsg) bata.getSerializable(SubmitQueryMsgThread.RESPONSE_MSG);

            if ("0".equals(qm.getResult())) {
                List<Map<String, Object>> returnMsg = qm.getDataTable();
                Map<String, Object> returnMap = returnMsg.get(0);
                String returnUserName = (String) returnMap.get("applyroomname");
                String chatroomid = (String) returnMap.get("applyroomid");

                centerProgressDialog.dismiss();
                //TODO 这里缺少了聊天室的名字
                chatroomService.saveChatRoom(new Chatroom(chatroomid, returnUserName, "0"));
                requestJoinRoomService.saveJoinRoom(chatroomid);
            }
        }
    }


    /**
     * 当用户点击返回键直接跳转到手机界面，别关闭了
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        CenterActivity.this.startActivity(intent);
    }
}
