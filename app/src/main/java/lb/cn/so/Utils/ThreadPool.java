package lb.cn.so.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/3/29
 *  Des：线程池，用来执行线程任务
 */
public class ThreadPool {
    //这个池现在是给用在Main和Center这两个界面对于http请求的线程方法中
    public static ScheduledExecutorService timeThreadPool = Executors.newScheduledThreadPool(10);

    //Service操作的线程池
    public static ScheduledExecutorService serviceThreadPool = Executors.newScheduledThreadPool(10);
}
