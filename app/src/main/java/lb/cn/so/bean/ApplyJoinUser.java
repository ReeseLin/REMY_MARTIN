package lb.cn.so.bean;
/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/4/9
 *  Des：申请加入聊天室用户类
 */
public class ApplyJoinUser {
    private String requesterid;
    private String requestroomid;
    private String requestername;

    public ApplyJoinUser(String requesterid, String requestroomid, String requestername) {
        this.requesterid = requesterid;
        this.requestroomid = requestroomid;
        this.requestername = requestername;
    }

    public void setRequesterid(String requesterid) {
        this.requesterid = requesterid;
    }

    public void setRequestroomid(String requestroomid) {
        this.requestroomid = requestroomid;
    }

    public void setRequestername(String requestername) {
        this.requestername = requestername;
    }

    public String getRequesterid() {
        return requesterid;
    }

    public String getRequestroomid() {
        return requestroomid;
    }

    public String getRequestername() {
        return requestername;
    }
}
