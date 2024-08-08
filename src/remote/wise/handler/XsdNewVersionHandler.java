package remote.wise.handler;

import java.io.IOException;

import remote.wise.businessobject.AssetMonitoringDetailsBO;
import remote.wise.exception.CustomFault;

public class XsdNewVersionHandler 
{ 
	
	public void xsdNewVersionHandler() throws IOException, CustomFault
	{
		
		//ToDo:
		//1. Initial SetUp: set the modifiedDate of the XSD file into the Jboss server session
		//2. Get the modifiedDate of XSD file from server session and compare it with the current value
		//3. If there is a change, perform task 4
		
		
		//4. For the new XSD version set parameter types and monitoring parameters
		AssetMonitoringDetailsBO assetMonitoringBO = new AssetMonitoringDetailsBO();
		String status = assetMonitoringBO.setMonitoringParameters();
		
		//System.out.println("Status:"+status);
	}
}	
