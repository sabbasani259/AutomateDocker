package remote.wise.service.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.MachineCommReportRespContract;
import remote.wise.service.implementation.MachineCommReportImpl;
//import remote.wise.util.WiseLogger;

@WebService(name = "MachineCommReportService")
public class MachineCommReportService 
{
	@WebMethod(operationName = "getMachineCommReport", action = "getMachineCommReport")
	public List<MachineCommReportRespContract> getMachineCommReport()
	{
		//WiseLogger infoLogger = WiseLogger.getLogger("MachineCommReportService:","info");
		Logger iLogger = InfoLoggerClass.logger;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String startDate = sdf.format(cal.getTime());
		iLogger.info("Current Startdate: "+startDate);
		long startTime = System.currentTimeMillis();
		
		List<MachineCommReportRespContract> response = new MachineCommReportImpl().getMachineCommReport();
		
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
		String endDate = sdf1.format(cal1.getTime());
		iLogger.info("Current Enddate: "+endDate);
		long endTime=System.currentTimeMillis();
		iLogger.info("serviceName:MachineCommReportService~executionTime:"+(endTime-startTime)+"~"+""+"~");
		
		/*for(int i=0; i<response.size(); i++)
		{
			infoLogger.info(response.get(i).getSerialNumber()+"|"+response.get(i).getDealerAccountId()+"|"+response.get(i).getDealerCode()+"|" +
					""+response.get(i).getDealerName()+"|"+response.get(i).getCity()+"|"+response.get(i).getState()+"|"+
					response.get(i).getSerialNumber()+"|"+response.get(i).getOwnerName()+"|"+response.get(i).getMachineHours()+"|"+
					response.get(i).getFwVersion()+"|"+response.get(i).getPktCreatedTimestamp()+"|"+response.get(i).getPktReceivedTimestamp()+"|"+
					response.get(i).getRolledOffDate()+"|"+response.get(i).getPktReceivedDate());
		}
		*/
		
		
		return response;
		
	}
}
