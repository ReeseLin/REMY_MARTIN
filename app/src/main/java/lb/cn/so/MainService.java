package lb.cn.so;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.concurrent.TimeUnit;

import lb.cn.so.Thread.ChackBeAgreeThread;
import lb.cn.so.Thread.CheckUserJoinThread;
import lb.cn.so.Utils.ThreadPool;

/**
 * Creater :ReeseLin
 * Email:172053362@qq.com
 * Date:2016/3/31
 * Des：后台Service，用来执行 GetChatRoomMsg / CheckBeAgree / CheckUsersJoin 操作
 */
public class MainService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ChackBeAgreeThread chackBeAgreeThread = new ChackBeAgreeThread(MainService.this);

        CheckUserJoinThread checkUserJoinThread = new CheckUserJoinThread(MainService.this);

        ThreadPool.serviceThreadPool.scheduleWithFixedDelay(chackBeAgreeThread, 0, 15, TimeUnit.SECONDS);

        ThreadPool.serviceThreadPool.scheduleWithFixedDelay(checkUserJoinThread, 0, 15, TimeUnit.SECONDS);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
