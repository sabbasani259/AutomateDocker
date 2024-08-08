/**
 * 
 */
package remote.wise.service.implementation;

import remote.wise.businessobject.EventDetailsBO;
import remote.wise.exception.CustomFault;

/**
 * @author kprabhu5
 *
 */
public class MessageServiceImpl {

	public String sendPendingMessage() throws CustomFault{
		String status = null;
		EventDetailsBO eventDetailsBo = new EventDetailsBO();	
		try{
			 status = eventDetailsBo.sendPendingMessage();	
		}
		catch(CustomFault e){
			throw e;
		}
	   	
		return status;
	}
}
