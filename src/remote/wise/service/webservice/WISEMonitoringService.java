package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

import remote.wise.service.implementation.WISEMonitoringImpl;

@WebService(name = "WISEMonitoringService")
public class WISEMonitoringService 
{
	@WebMethod(operationName = "monitorWiseStatus", action = "monitorWiseStatus")
	public String monitorWiseStatus()
	{
		String wiseStatus="SUCCESS";
		
		wiseStatus=new WISEMonitoringImpl().getWiseStatus();
		
		return wiseStatus;
	}
	
	@WebMethod(operationName = "monitorQStatus", action = "monitorQStatus")
	public String monitorQStatus()
	{
		String hornetQStatus="SUCCESS";
		
		hornetQStatus= new WISEMonitoringImpl().getHornetQStatus();
		
		return hornetQStatus;
	}
	
}
