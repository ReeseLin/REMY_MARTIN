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
import lb.cn.so.bean.Chatroom;

/**
 * Creater :ReeseLin
 * Email:172053362@qq.com
 * Date:2016/3/30
 * Des：对于Center界面的ListView的适配器
 */
public class ApplyJoinAdapter extends BaseAdapter {
    private List<ApplyJoinUser> ApplyJoinUsers;    //在绑定的数据
    private int resource;                //绑定的条目界面
    private LayoutInflater inflater;

    public ApplyJoinAdapter(Context context, List<ApplyJoinUser> ApplyJoinUsers, int resource) {
        this.ApplyJoinUsers = ApplyJoinUsers;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return ApplyJoinUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return ApplyJoinUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView requestername = null;
        TextView requesterid = null;
        TextView requestroomid = null;

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);//生成条目界面对象

            requestername = (TextView) convertView.findViewById(R.id.requestername);
            requesterid = (TextView) convertView.findViewById(R.id.requesterid);
            requestroomid = (TextView) convertView.findViewById(R.id.requestroomid);

            ViewCache cache = new ViewCache();
            cache.requestername = requestername;
            cache.requesterid = requesterid;
            cache.requestroomid = requestroomid;
            convertView.setTag(cache);
        } else {
            ViewCache cache = (ViewCache) convertView.getTag();
            requestername = cache.requestername;
            requesterid = cache.requesterid;
            requestroomid = cache.requestroomid;
        }
        ApplyJoinUser applyJoinUser = ApplyJoinUsers.get(position);
        //下面代码实现数据绑定
        requestername.setText(applyJoinUser.getRequestername());
        requesterid.setText(applyJoinUser.getRequesterid());
        requestroomid.setText(applyJoinUser.getRequestroomid());

        return convertView;

    }


    public void refresh(List<ApplyJoinUser> ApplyJoinUsers) {
        this.ApplyJoinUsers = ApplyJoinUsers;
        notifyDataSetChanged();
    }


    private final class ViewCache {
        public TextView requestername;
        public TextView requesterid;
        public TextView requestroomid;
    }
}
