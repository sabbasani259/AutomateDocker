/**
 * 
 */
package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ModelCodeResponseContract;
import remote.wise.service.implementation.ModelCodeDetailsImpl;

/**
 * @author roopn5
 *
 */
@WebService(name = "ModelCodeService")
public class ModelCodeService {
	@WebMethod(operationName = "GetModelCodeMap", action = "GetModelCodeMap")
	public ModelCodeResponseContract GetModelCodeMap() throws CustomFault{

		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		iLogger.info("---- Webservice Input ------");
		ModelCodeResponseContract respObj = new ModelCodeDetailsImpl().getModelMap();
		iLogger.info("----- Webservice Output-----");
		iLogger.info("modelList:"+respObj.getModelCodeMap()+"");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:ModelCodeService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		return respObj;
	}

}
