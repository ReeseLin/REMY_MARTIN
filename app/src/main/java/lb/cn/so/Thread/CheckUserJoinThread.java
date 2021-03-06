package lb.cn.so.Thread;

import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lb.cn.so.MainConfig.MainUser;
import lb.cn.so.MainConfig.SettingFile;
import lb.cn.so.Utils.HttpUtils;
import lb.cn.so.bean.ApplyJoinUser;
import lb.cn.so.bean.QueryMsg;
import lb.cn.so.service.RequestJoinUserService;

/**
 * Creater :ReeseLin
 * Email:172053362@qq.com
 * Date:2016/4/9
 * Des：检测是否有人申请加入本用户创建的聊天室
 */
public class CheckUserJoinThread implements Runnable {


    RequestJoinUserService requestJoinUserService;

    public CheckUserJoinThread(Context context) {
        this.requestJoinUserService = new RequestJoinUserService(context);
    }

    @Override
    public void run() {
        try {
            checkJoinUser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkJoinUser() throws Exception {
        QueryMsg queryMsg = new QueryMsg();
        queryMsg.setMethodName("CheckUsersJoin");
        queryMsg.iniDateTable();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userid", MainUser.userid);
        queryMsg.getDataTable().add(map);
        QueryMsg qm = HttpUtils.postQueryMsgAndGetQueryMsg(SettingFile.postUrl, queryMsg);
        if ("0".equals(qm.getResult()) && qm.getDataTable() != null) {
            List<Map<String, Object>> datatable = qm.getDataTable();
            for (Map<String, Object> Applyer : datatable) {
                ApplyJoinUser applyJoinUser = new ApplyJoinUser
                        ((String) Applyer.get("userid"), (String) Applyer.get("chatroomid"), (String) Applyer.get("username"));
                if (requestJoinUserService.isAlreadyApply(applyJoinUser)) {
                    requestJoinUserService.saveJoinUser(applyJoinUser);
                }
            }
        }
    }

}
