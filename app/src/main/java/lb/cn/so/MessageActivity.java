package lb.cn.so;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lb.cn.so.MainConfig.MainUser;
import lb.cn.so.MainConfig.SettingFile;
import lb.cn.so.Thread.SubmitQueryMsgThread;
import lb.cn.so.Utils.ThreadPool;
import lb.cn.so.adapter.ApplyJoinAdapter;
import lb.cn.so.bean.ApplyJoinUser;
import lb.cn.so.bean.QueryMsg;
import lb.cn.so.service.RequestJoinUserService;
/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/4/7
 *  Des：消息界面，确认好友加入
 */
public class MessageActivity extends Activity {

    //关于加入用户的数据库操作类
    private RequestJoinUserService requestJoinUserService = new RequestJoinUserService(this);

    //界面ListView
    private ListView showMessageListView;
    private ApplyJoinAdapter applyJoinAdapter;
    List<ApplyJoinUser> applyJoinUsers =new ArrayList<>();

    //弹窗显示Dialog
    private AlertDialog centerDialog;

    //Handler使用的what值
    public static final int agreeUserJoin_what = 6;

    private MyHandler myHandler;

    //等待的Dialog
    private ProgressDialog centerProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        myHandler = new MyHandler();
        showMessageListView = (ListView) findViewById(R.id.showMessageListView);

        //TODO 如果你觉得需要可以修改为动态刷新
        applyJoinUsers = requestJoinUserService.getJoinUsers();
        applyJoinAdapter = new ApplyJoinAdapter(this, applyJoinUsers, R.layout.message_item);
        showMessageListView.setOnItemClickListener(new ItemClickListener());
        showMessageListView.setAdapter(applyJoinAdapter);
    }

    /**
     * item点击事件
     */
    private final class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView lView = (ListView) parent;
            ApplyJoinUser applyJoinUser = (ApplyJoinUser) lView.getItemAtPosition(position);
            String showMsg = "允许：" + applyJoinUser.getRequestername() + "加入房间" + applyJoinUser.getRequestroomid() + "吗？";
            centerDialog = new AlertDialog.Builder(MessageActivity.this).setTitle(showMsg).
                    setPositiveButton("确定", new agreeUserJoin(applyJoinUser)).
                    setNegativeButton("取消", null).show();
        }
    }

    /**
     * 创建聊天室的监听类
     */
    class agreeUserJoin implements DialogInterface.OnClickListener {
        ApplyJoinUser applyJoinUser;

        public agreeUserJoin(ApplyJoinUser applyJoinUser) {
            this.applyJoinUser = applyJoinUser;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            QueryMsg queryMsg = assembleCreateRoomQuery();
            SubmitQueryMsgThread submitQueryMsgThread = new SubmitQueryMsgThread(myHandler, SettingFile.postUrl, agreeUserJoin_what);
            submitQueryMsgThread.addQueryMsg(queryMsg);
            ThreadPool.timeThreadPool.scheduleWithFixedDelay(submitQueryMsgThread, 0, 3, TimeUnit.SECONDS);
            centerProgressDialog = ProgressDialog.show(MessageActivity.this, null, "正在创建......", true);
        }

        private QueryMsg assembleCreateRoomQuery() {
            QueryMsg queryMsg = new QueryMsg();
            queryMsg.setMethodName("AgreeUserJoin");
            queryMsg.iniDateTable();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userid", MainUser.userid);
            map.put("friendid", applyJoinUser.getRequesterid());
            map.put("chatroomid", applyJoinUser.getRequestroomid());
            queryMsg.getDataTable().add(map);
            return queryMsg;
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case agreeUserJoin_what:
                    deleteApply(msg);
                    centerProgressDialog.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }

        /**
         * 当获得请求加入聊天室成功结果后，删除本地的申请消息
         * @param msg
         */
        private void deleteApply(Message msg) {
            Bundle bata = msg.getData();
            //获得数据
            QueryMsg qm = (QueryMsg) bata.getSerializable(SubmitQueryMsgThread.RESPONSE_MSG);
            if ("0".equals(qm.getResult())) {
                List<Map<String, Object>> resultList = qm.getDataTable();
                Map<String, Object> result = resultList.get(0);
                String friendid = (String) result.get("friendid");
                String chatroomid = (String) result.get("chatroomid");
                requestJoinUserService.delete(new ApplyJoinUser(friendid,chatroomid,null));
            }
        }
    }
}
