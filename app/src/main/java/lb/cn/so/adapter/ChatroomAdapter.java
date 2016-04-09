package lb.cn.so.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import lb.cn.so.R;
import lb.cn.so.bean.Chatroom;

/**
 * Creater :ReeseLin
 * Email:172053362@qq.com
 * Date:2016/3/30
 * Des：对于中心界面的ListView的适配器
 */
public class ChatroomAdapter extends BaseAdapter {
    private List<Chatroom> chatrooms;    //在绑定的数据
    private int resource;                //绑定的条目界面
    private LayoutInflater inflater;

    public ChatroomAdapter(Context context, List<Chatroom> chatrooms, int resource) {
        this.chatrooms = chatrooms;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return chatrooms.size();
    }

    @Override
    public Object getItem(int position) {
        return chatrooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView chatroomname = null;
        TextView chatroomid = null;
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);//生成条目界面对象
            chatroomname = (TextView) convertView.findViewById(R.id.chatroomname);
            chatroomid = (TextView) convertView.findViewById(R.id.chatroomid);

            ViewCache cache = new ViewCache();
            cache.chatroomid = chatroomid;
            cache.chatroomname = chatroomname;
            convertView.setTag(cache);
        } else {
            ViewCache cache = (ViewCache) convertView.getTag();
            chatroomname = cache.chatroomname;
            chatroomid = cache.chatroomid;
        }
        Chatroom chatroom = chatrooms.get(position);
        //下面代码实现数据绑定
        chatroomname.setText(chatroom.getChatroomname());
        chatroomid.setText(chatroom.getChatroomid());

        return convertView;

    }


    public void refresh(List<Chatroom> Chatrooms) {
        this.chatrooms = Chatrooms;
        notifyDataSetChanged();
    }


    private final class ViewCache {
        public TextView chatroomname;
        public TextView chatroomid;
    }
}
