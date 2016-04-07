package lb.cn.so.bean;

import java.util.Date;

/**
 * Creater :ReeseLin
 * Email:172053362@qq.com
 * Date:2016/3/29
 * Des：聊天室信息
 */
public class ChatroomMessage {
    private String chatroomid;
    private String message;
    private String senderid;
    private Date createtime;
    private String sendername;
    private String isread;

    public ChatroomMessage(String chatroomid, String message, String senderid, Date createtime, String sendername, String isread) {
        this.chatroomid = chatroomid;
        this.message = message;
        this.senderid = senderid;
        this.createtime = createtime;
        this.sendername = sendername;
        this.isread = isread;
    }

    public void setChatroomid(String chatroomid) {
        this.chatroomid = chatroomid;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public String getChatroomid() {
        return chatroomid;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderid() {
        return senderid;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public String getSendername() {
        return sendername;
    }

    public String getIsread() {
        return isread;
    }
}
