package com.wds.oilfieldDrillingJobs.api;

import java.io.Serializable;

import android.text.TextUtils;

public class ApiResponse implements Serializable {
	
	private static final long serialVersionUID = 2298834586169426687L;
	
	public static final int STATUS_SUCCESS = 0;
	public static final int STATUS_ERROR = 1;

	private int status;
	private String error;
	private String requestName;
	private String method;
	private Object data;
	
	public ApiResponse() {}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}
	
	public boolean hasError() {
		return !TextUtils.isEmpty(error);
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getRequestName() {
		return requestName;
	}
	
	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
}
