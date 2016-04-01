package lb.cn.so.Thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.HashSet;
import java.util.Set;

import lb.cn.so.Utils.HttpUtils;
import lb.cn.so.bean.QueryMsg;

/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/3/29
 *  Des：Http请求线程，由于主线程是无法执行耗时的http请求任务
 *  所以封装了一个实现Runnable的类，用于专门去执行请求任务
 */
public class SubmitQueryMsgThread implements Runnable {
    //TODO 这里应该可以优化下  不用每次都建线程
    public static String RESPONSE_MSG = "RESPONSE_MSG";

    private String requestURL;
    private Handler handler;
    private int what;

    public SubmitQueryMsgThread(Handler hander, String requestURL,int what) {
        this.requestURL = requestURL;
        this.handler = hander;
        this.what=what;
    }

    public static final Set<QueryMsg> submitSet = new HashSet<QueryMsg>();

    public void addQueryMsg(QueryMsg queryMsg) {
        submitSet.add(queryMsg);
    }

    public void addQueryMsgs(Set<QueryMsg> queryMsgs) {
        submitSet.addAll(queryMsgs);
    }

    public static final Set<QueryMsg> deleteSet = new HashSet<QueryMsg>();

    @Override
    public void run() {
        if (submitSet.size() > 0) {
            execute();
        }
    }

    private void execute() {
        for (QueryMsg queryMsg : submitSet) {
            try {
                QueryMsg qm = HttpUtils.postQueryMsgAndGetQueryMsg(requestURL, queryMsg);
                Message msg = new Message();
                msg.what=what;
                Bundle b = new Bundle();
                b.putSerializable(RESPONSE_MSG, qm);
                msg.setData(b);
                handler.sendMessage(msg);
                deleteSet.add(queryMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        submitSet.removeAll(deleteSet);
        deleteSet.clear();
    }
}
