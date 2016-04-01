package lb.cn.so.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/3/29
 *  Des：偏好设置参数
 *  1，现在用于存储使用用户的Username和Userid
 */
public class PreferencesService {

    private Context context;


    public PreferencesService(Context context) {
        this.context = context;
    }

    public void save(String name, String id) {
        SharedPreferences preferences = context.getSharedPreferences("so", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", name);
        editor.putString("id", id);
        editor.commit();
    }



    public Map<String, String> getPreferences() {
        Map<String, String> params = new HashMap<String, String>();
        SharedPreferences preferences = context.getSharedPreferences("so", Context.MODE_PRIVATE);
        params.put("name", preferences.getString("name", ""));
        params.put("id", preferences.getString("id", ""));
        return params;
    }
}
