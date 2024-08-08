/**
 * 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.StateRespContract;
import remote.wise.service.implementation.StateImpl;

/**
 * @author roopn5
 *
 */
@WebService(name = "StateService")
public class StateService {

	
	@WebMethod(operationName = "GetStates", action = "GetStates")
	public List<StateRespContract> GetStates() throws CustomFault{

		
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		List<StateRespContract> respObj = new StateImpl().getStates();
		iLogger.info("<-----Webservice Output----->");
		for(int i=0;i<respObj.size();i++){
			iLogger.info(i+"   ROW");
			iLogger.info("State Id:"+respObj.get(i).getStateId()+" , State Name:"+respObj.get(i).getStateName()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:StateService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}
}
