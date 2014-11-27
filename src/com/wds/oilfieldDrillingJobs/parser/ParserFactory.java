package com.wds.oilfieldDrillingJobs.parser;

import android.text.TextUtils;

import com.wds.oilfieldDrillingJobs.api.ApiData;

public class ParserFactory {
	
	public static ApiParser getParser(String command, String method) {
		if (TextUtils.isEmpty(command)) {
			return null;
		} else if (ApiData.COMMAND_JOBS.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new JobListParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_EMAILS.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_POST.equalsIgnoreCase(method) ||
				ApiData.METHOD_GET.equalsIgnoreCase(method)) 
			{
				return new EmailSettingParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_SEND_EMAIL.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_POST.equalsIgnoreCase(method)) {
				return new BooleanParser();
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_SUBSCRIPTIONS.equalsIgnoreCase(command)) {
			if (ApiData.METHOD_GET.equalsIgnoreCase(method)) {
				return new SubscriptionListParser();
			} else if (ApiData.METHOD_POST.equalsIgnoreCase(method)) {
				return new SubscriptionParser(); 
			} else {
				return new SimpleParser();
			}
		} else if (ApiData.COMMAND_SUBSCRIPTIONS_DELETE.equalsIgnoreCase(command) &&
				   ApiData.METHOD_DELETE.equalsIgnoreCase(method)) 
		{
			return new SubscriptionParser();
		} else {
			return null;
		}
	}

}
