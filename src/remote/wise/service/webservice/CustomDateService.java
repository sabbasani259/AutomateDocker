package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import remote.wise.exception.CustomFault;

@WebService(name = "CustomDateService")
public class CustomDateService {
	@WebMethod(operationName = "display", action = "display")
	public void display(@WebParam(name="fromDate") String fromDate,@WebParam(name="toDate") String toDate) throws CustomFault
	{
		
		//CustomDatesBO customDatesBO =new CustomDatesBO();
		//customDatesBO.display(fromDate,toDate);
		
	}
}
