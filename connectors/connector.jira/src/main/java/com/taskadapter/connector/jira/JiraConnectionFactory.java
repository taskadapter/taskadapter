package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.RemoteIssueType;
import com.atlassian.jira.rpc.soap.client.RemotePriority;
import com.atlassian.jira.rpc.soap.client.RemoteProject;
import com.atlassian.jira_soapclient.SOAPSession;
import com.taskadapter.connector.definition.WebServerInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

public class JiraConnectionFactory {

	public static JiraConnection createConnection(WebServerInfo info) throws MalformedURLException, RemoteException {
		String baseUrl = info.getHost() + "/rpc/soap/jirasoapservice-v2";
		SOAPSession soapSession = new SOAPSession(new URL(baseUrl));
		soapSession.connect(info.getUserName(), info.getPassword());
		return new JiraConnection(soapSession.getJiraSoapService(), soapSession.getAuthenticationToken());
	}
	
	public static void main(String[] args) {
		WebServerInfo info = new WebServerInfo("http://ta-dev.dyndns.biz:8080", "admin", "zzz666");
		JiraConnection conn;
		try {
			conn = JiraConnectionFactory.createConnection(info);
		RemotePriority[] list = conn.getPriorities();
		
		for (int i = 0; i < list.length; i++) {
			System.out.println(list[i].getId() + ": " + list[i].getName());
		}

		RemoteProject[] prjList = conn.getProjects();		
		RemoteIssueType[] list2 = conn.getIssueTypeList("ZMDPRJNEW");
		
		for (int i = 0; i < list2.length; i++) {
			System.out.println(list2[i].getId() + ": " + list2[i].getName());
		}
	
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
}
