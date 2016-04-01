package lb.cn.so.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  Creater :ReeseLin
 *  Email:172053362@qq.com
 *  Date:2016/3/29
 *  Des： post请求数据包装类 What to do: 与QueryUtils结合使用可以把指定格式的xml装换为QueryMsg
 */
public class QueryMsg implements Serializable{

	// 是否是响应数据   默认不是
	private boolean isResponse = false;
	private String MethodName;
	private String Result;
	private String Error;
	private Date createtime;
	private String AccessKey;
	private List<Map<String, Object>> DataTable;
	private String encoding;
	private int responseCode;

	public QueryMsg() {
		super();
	}

	public boolean isResponse() {
		return isResponse;
	}

	public void setResponse(boolean isResponse) {
		this.isResponse = isResponse;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getMethodName() {
		return MethodName;
	}

	public void setMethodName(String methodName) {
		MethodName = methodName;
	}

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public String getError() {
		return Error;
	}

	public void setError(String error) {
		Error = error;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getAccessKey() {
		return AccessKey;
	}

	public void setAccessKey(String accessKey) {
		AccessKey = accessKey;
	}

	public void iniDateTable(){
		if(DataTable==null){
			DataTable = new ArrayList<Map<String, Object>>();
		}
	}
	
	public List<Map<String, Object>> getDataTable() {
		return DataTable;
	}

	public void setDataTable(List<Map<String, Object>> dataTable) {
		DataTable = dataTable;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
}
