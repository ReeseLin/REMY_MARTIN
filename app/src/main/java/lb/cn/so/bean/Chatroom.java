package lb.cn.so.bean;

/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/3/29
 *  Des：聊天室类
 */
public class Chatroom {
    private String chatroomid;
    private String chatroomname;
    private String isagree;

    public Chatroom() {
    }

    public Chatroom(String chatroomid, String chatroomname, String isagree) {
        this.chatroomid = chatroomid;
        this.chatroomname = chatroomname;
        this.isagree = isagree;
    }

    public String getChatroomid() {
        return chatroomid;
    }

    public void setChatroomid(String chatroomid) {
        this.chatroomid = chatroomid;
    }

    public String getChatroomname() {
        return chatroomname;
    }

    public void setChatroomname(String chatroomname) {
        this.chatroomname = chatroomname;
    }

    public String getIsagree() {
        return isagree;
    }

    public void setIsagree(String isagree) {
        this.isagree = isagree;
    }
}
