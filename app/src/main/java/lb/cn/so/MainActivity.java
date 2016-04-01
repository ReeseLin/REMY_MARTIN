package lb.cn.so;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lb.cn.so.MainConfig.MainUser;
import lb.cn.so.MainConfig.SettingFile;
import lb.cn.so.Thread.SubmitQueryMsgThread;
import lb.cn.so.Utils.ThreadPool;
import lb.cn.so.bean.QueryMsg;
import lb.cn.so.service.PreferencesService;

/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/3/29
 *  Des：用户第一次访问的界面，用于生成一个用户
 */
public class MainActivity extends AppCompatActivity {

    //UI控件
    private EditText usernameET;
    private Button submitButton;
    private String username;

    //交互handler
    private MyHandler myHandler;

    //在请求等待的对话框
    private ProgressDialog mProgressDialog;

    //Preferences属性保存，保存全局的Username和Userid
    private PreferencesService service = new PreferencesService(this);

    //线程请求时传过去的what，来区别handler中的创建用户信息
    public final static int request_createNewUser_what = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameET = (EditText) this.findViewById(R.id.usernameEditText);
        submitButton = (Button) this.findViewById(R.id.button);
        myHandler = new MyHandler();

        Map<String, String> map = service.getPreferences();

        String name = map.get("name");
        String id = map.get("id");

        //开启service
        Intent startIntent = new Intent(this, MainService.class);
        startService(startIntent);

        if ("".equals(name) && "".equals(id)) {
            submitButton.setOnClickListener(new ButtoonClickListener());
        } else {
            MainUser.username = name;
            MainUser.userid = id;
            Intent intent = new Intent(MainActivity.this, CenterActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 监听按键类，用于在用户点击按钮后提交用户的username到服务器，再从服务器拿到唯一ID
     */
    private final class ButtoonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //组装请求信息
            QueryMsg queryMsg = assembleQuery();
            //创建线程对象
            SubmitQueryMsgThread submitQueryMsgThread = new SubmitQueryMsgThread(myHandler, SettingFile.postUrl, request_createNewUser_what);
            //传入请求内容
            submitQueryMsgThread.addQueryMsg(queryMsg);
            //线程执行
            ThreadPool.timeThreadPool.scheduleWithFixedDelay(submitQueryMsgThread, 0, 3, TimeUnit.SECONDS);
            //等待画面
            mProgressDialog = ProgressDialog.show(MainActivity.this, null, "正在登录......", true);

        }

        /**
         * 组装请求对象，请求服务器的CreateNewUser方法
         * @return
         */
        @NonNull
        private QueryMsg assembleQuery() {
            username = usernameET.getText().toString();
            QueryMsg queryMsg = new QueryMsg();
            queryMsg.setMethodName("CreateNewUser");
            queryMsg.iniDateTable();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("username", username);
            MainUser.username = username;
            queryMsg.getDataTable().add(map);
            return queryMsg;
        }
    }

    /**
     * 界面的handler，用于接收Http线程请求后回传的数据
     */
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case request_createNewUser_what:
                    getUserAndRecordAndToCenterActivity(msg);
                    break;
            }
            super.handleMessage(msg);
        }

        /**
         * 得到请求线程回传数据后，进行指定处理
         * @param msg
         */
        private void getUserAndRecordAndToCenterActivity(Message msg) {
            Bundle bata = msg.getData();
            //获得数据
            QueryMsg qm = (QueryMsg) bata.getSerializable(SubmitQueryMsgThread.RESPONSE_MSG);
            //记录全局User
            if (qm != null) {
                List<Map<String, Object>> datatable = qm.getDataTable();
                Map<String, Object> dataMap = datatable.get(0);
                MainUser.userid = String.valueOf(dataMap.get("userid"));
            }
            //判断是否请求成功，成功则跳转到详情界面
            if (MainUser.userid != null && MainUser.username != null && qm.getResponseCode() == 200) {
                mProgressDialog.dismiss();
                service.save(MainUser.username, MainUser.userid);
                Intent intent = new Intent(MainActivity.this, CenterActivity.class);
                startActivity(intent);
            }
        }
    }
}
