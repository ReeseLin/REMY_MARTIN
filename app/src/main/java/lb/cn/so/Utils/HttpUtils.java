package lb.cn.so.Utils;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lb.cn.so.bean.QueryMsg;

/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/3/29
 *  Des：http请求类，接受特定的请求对象，返回特定的请求对象，主要作用是用于服务器之间的交互
 */
public class HttpUtils {

    /**
     * Post数据到指定的Url，并接受返回数据
     * @param url
     * @param msg
     * @return
     * @throws Exception
     */
    public static String postMsg(String url, String msg) throws Exception {
        URL positionURL = new URL(url);
        URLConnection connection = positionURL.openConnection();
        HttpURLConnection conn = (HttpURLConnection) connection;

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept-Charset", "utf-8");

        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.writeBytes(msg);
        dos.flush();

        StringBuffer sb = new StringBuffer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));

        String readLine = null;

        do {
            readLine = reader.readLine();
            if (readLine != null) {
                sb.append(readLine);
            }
        } while (readLine != null);

        reader.close();
        dos.close();
        conn.disconnect();
        return sb.toString();
    }

    /**
     * 把封装好的请求对象Post到指定的Url中，并返回String数据
     * @param url
     * @param queryMsg
     * @return
     * @throws Exception
     */
    public static String postQueryMsg(String url, QueryMsg queryMsg) throws Exception {
        URL positionURL = new URL(url);
        URLConnection connection = positionURL.openConnection();
        HttpURLConnection conn = (HttpURLConnection) connection;

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept-Charset", "utf-8");

        OutputStream out = conn.getOutputStream();

        wirteQueryMsg(queryMsg, out);

        StringBuffer sb = new StringBuffer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));

        String readLine = null;

        do {
            readLine = reader.readLine();
            if (readLine != null) {
                sb.append(readLine);
            }
        } while (readLine != null);

        reader.close();
        conn.disconnect();
        return sb.toString();
    }

    /**
     * 把封装好的请求数据post到指定的Url，并接收数据组合成一个对象返回
     * 涉及到的知识点重要的是，对于xml文件的解析方法
     * @param url
     * @param queryMsg
     * @return
     * @throws Exception
     */
    public static QueryMsg postQueryMsgAndGetQueryMsg(String url, QueryMsg queryMsg) throws Exception {
        URL positionURL = new URL(url);
        URLConnection connection = positionURL.openConnection();
        HttpURLConnection conn = (HttpURLConnection) connection;

        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept-Charset", "utf-8");

        OutputStream out = conn.getOutputStream();

        wirteQueryMsg(queryMsg, out);

        InputStream in = conn.getInputStream();

        QueryMsg responseQM = getResponseQueryMsg(in);

        responseQM.setResponseCode(conn.getResponseCode());

        conn.disconnect();

        return responseQM;
    }

    private static QueryMsg getResponseQueryMsg(InputStream in) throws Exception {

        QueryMsg queryMsg = null;
        Map<String, Object> map = null;

        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(in, "UTF-8");//为Pull解析器设置要解析的XML数据

        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {

            switch (event) {
                case XmlPullParser.START_TAG:
                    if ("DocumentElement".equals(pullParser.getName())) {
                        queryMsg = new QueryMsg();
                    } else if ("Result".equals(pullParser.getName())) {
                        String result = pullParser.nextText();
                        queryMsg.setResult(result);
                    } else if ("Error".equals(pullParser.getName())) {
                        String error = pullParser.nextText();
                        queryMsg.setError(error);
                    } else if ("DataTable".equals(pullParser.getName())) {
                        queryMsg.iniDateTable();
                        map = new HashMap<String, Object>();
                    } else {
                        map.put(pullParser.getName(), pullParser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("DataTable".equals(pullParser.getName())) {
                        queryMsg.getDataTable().add(map);
                    }
                    break;
            }
            event = pullParser.next();
        }

        return queryMsg;
    }


    private static void wirteQueryMsg(QueryMsg queryMsg, OutputStream out) throws Exception {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(out, "UTF-8");
        serializer.startDocument("UTF-8", true);
        serializer.startTag(null, "DocumentElement");

        serializer.startTag(null, "MethodName");
        serializer.text(queryMsg.getMethodName());
        serializer.endTag(null, "MethodName");

        List<Map<String, Object>> datetable = queryMsg.getDataTable();
        for (int i = 0; i < datetable.size(); i++) {
            serializer.startTag(null, "DataTable");
            Map<String, Object> map = datetable.get(i);
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                serializer.startTag(null, entry.getKey());
                serializer.text(entry.getValue() + "");
                serializer.endTag(null, entry.getKey());
            }
            serializer.endTag(null, "DataTable");
        }
        serializer.endTag(null, "DocumentElement");
        serializer.endDocument();
        out.flush();
        out.close();
    }

}
