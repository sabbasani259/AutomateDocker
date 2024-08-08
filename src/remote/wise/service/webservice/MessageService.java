/**
 * 
 */
package remote.wise.service.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

import remote.wise.exception.CustomFault;
import remote.wise.service.implementation.MessageServiceImpl;


/**
 * @author kprabhu5
 * Class to handle unsent pending messages
 */
@WebService(name = "MessageService")
public class MessageService {
	@WebMethod(operationName = "sendPendingMessages", action = "sendPendingMessages")
	public String sendPendingMessages() throws CustomFault{
		String status = null;
		status = new MessageServiceImpl().sendPendingMessage();
		return status;
	}

}
