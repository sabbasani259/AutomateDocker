package remote.wise.exception;

import javax.jws.WebParam;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebFault;

@WebFault(name = "CustomFault")
public class CustomFault extends Exception 
{
	private static String faultInfo;
	
	public String getFaultInfo() {
		return faultInfo;
	}

	public void setFaultInfo(String faultInfo) {
		this.faultInfo = faultInfo;
		//System.out.println(faultInfo);
	}

	public CustomFault(String error)
	{
		faultInfo=error;
		//System.out.println(faultInfo);
	}
	public CustomFault(@WebParam(name = "fault") SOAPFault fault)
	{
		//super(faultInfo);
		this.faultInfo = faultInfo;
	}
}
