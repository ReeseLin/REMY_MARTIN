package lb.cn.so;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Lin on 2016/3/31.
 */
public class ChatActivity extends Activity implements View.OnClickListener {

    private TextView chatroomidTV;
    private TextView chatroomnameTV;
    private EditText sendMessageET;
    private Button commitBut;

    private String chatroomname;
    private String chatroomid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatroomidTV = (TextView) findViewById(R.id.chatroomid);
        chatroomnameTV = (TextView) findViewById(R.id.chatroomname);
        sendMessageET = (EditText) findViewById(R.id.contentEditText);
        commitBut = (Button) findViewById(R.id.commitButton);

        Intent intent = getIntent();//获取用于激活它的意图对象

        Bundle bundle = intent.getExtras();

        chatroomname = bundle.getString("chatroomid");
        chatroomid = bundle.getString("chatroomname");

//        commitBut.requestFocus();

        commitBut.setOnClickListener(this);

        chatroomidTV.setText("ID:" + chatroomname);
        chatroomnameTV.setText("" + chatroomid);

        show();
    }

    private void show() {
        //显示 listView内容
    }

    //自定义handler，只是一个定时的功能去调用show()方法

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commitButton:
                //执行 SendChatRoomMsg操作
                break;
        }
    }
}
