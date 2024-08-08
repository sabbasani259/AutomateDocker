package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.RoleRespContract;
import remote.wise.service.implementation.RoleImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "RoleService")
public class RoleService {
	
	/** This method gets the list of roles
	 * @return Returns the list of roles
	 */
	@WebMethod(operationName = "GetRoles", action = "GetRoles")	
	public List<RoleRespContract> getRoles(){
		
		//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
		//.WiseLogger infoLogger = WiseLogger.getLogger("RoleService:","info");
		Logger iLogger = InfoLoggerClass.logger;			
		//Logger infoLogger = Logger.getLogger("infoLogger");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();	
		iLogger.info("---- Webservice Input ------");
		List<RoleRespContract> response= new RoleImpl().getRoles();
		iLogger.info("----- Webservice Output-----");
		for(int i=0; i<response.size(); i++){
			iLogger.info("roleName:"+response.get(i).getRoleName()+","+"roleId:"+response.get(i).getRoleId()+"");
		}
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:RoleService~executionTime:"+(endTime-startTime)+"~"+""+"~");		
		return response;		
	}

}
