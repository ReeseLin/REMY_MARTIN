package lb.cn.so.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import lb.cn.so.R;
import lb.cn.so.bean.ApplyJoinUser;
import lb.cn.so.bean.ChatroomMessage;

public class ChatMessageAdapter extends BaseAdapter {
    private List<ChatroomMessage> messages;    //在绑定的数据
    private int resource;                //绑定的条目界面
    private LayoutInflater inflater;

    public ChatMessageAdapter(Context context, List<ChatroomMessage> messages, int resource) {
        this.messages = messages;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView sendername = null;
        TextView chatmessage = null;

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);//生成条目界面对象

            sendername = (TextView) convertView.findViewById(R.id.sendernameTV);
            chatmessage = (TextView) convertView.findViewById(R.id.chatMessageTV);

            ViewCache cache = new ViewCache();
            cache.sendername = sendername;
            cache.chatmessage = chatmessage;
            convertView.setTag(cache);
        } else {
            ViewCache cache = (ViewCache) convertView.getTag();
            sendername = cache.sendername;
            chatmessage = cache.chatmessage;
        }

        ChatroomMessage message = messages.get(position);

        //下面代码实现数据绑定
        sendername.setText(message.getSendername());
        chatmessage.setText(message.getMessage());

        return convertView;

    }

    public void refresh(List<ChatroomMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    private final class ViewCache {
        public TextView sendername;
        public TextView chatmessage;
    }
}
