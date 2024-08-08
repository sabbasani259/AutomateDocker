package remote.wise.service.webservice;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.RolledOffMachinesOnMapRespContract;
import remote.wise.service.implementation.RolledOffMachinesOnMapImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.DateUtil;
import remote.wise.util.ListToStringConversion;

@Path("/RolledOffMachinesOnMapService")
public class RolledOffMachinesOnMapService {
	
	@POST
	@Path("getRolledOffMachines")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({MediaType.APPLICATION_JSON})
	public List<RolledOffMachinesOnMapRespContract> 
	getRolledOffMachines(@Context HttpHeaders httpHeaders, @JsonProperty("reqObj")LinkedHashMap<String,Object> reqObj,@QueryParam("date") Date date)throws CustomFault
	{  
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		String csrfToken = null;
		boolean isValidCSRF=false;
		CommonUtil util = new CommonUtil();
		List<RolledOffMachinesOnMapRespContract> rolledOffMachinesData=null;


		try{
			
			long startTime = System.currentTimeMillis();
			iLogger.info("Webservice input :"+reqObj);
			if(httpHeaders.getRequestHeader("CSRFTOKEN")!=null)
			{
				csrfToken=httpHeaders.getRequestHeader("CSRFTOKEN").get(0);
			}
			iLogger.info("RolledOffMachinesOnMapService:getRolledOffMachines ::   received csrftoken :: "+csrfToken);
			if(csrfToken!=null){
				isValidCSRF=util.validateANTICSRFTOKEN((String)reqObj.get("loginId"),csrfToken);
			}
			iLogger.info("RolledOffMachinesOnMapService:getRolledOffMachines ::   csrftoken isValidCSRF :: "+isValidCSRF);
			if(!isValidCSRF)
			{
				iLogger.info("RolledOffMachinesOnMapService:getRolledOffMachines ::  Invalid request.");
				throw new CustomFault("Invalid request.");
			}
			String isValidinput=null;
			ListToStringConversion convert=new ListToStringConversion();
			String tenacnyListString="";
			String userId="";
				if(reqObj.get("tenancyIdList")!=null){
					List<Integer> tenacnyList=(List<Integer>) reqObj.get("tenancyIdList");
					tenacnyListString=convert.getIntegerListString(tenacnyList).toString();
					isValidinput = util.inputFieldValidation(tenacnyListString);
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
				}
				if(reqObj.get("loginId")!=null){
					userId=new CommonUtil().getUserId((String)reqObj.get("loginId"));
					isValidinput = util.inputFieldValidation(userId.toString());
					if(!isValidinput.equals("SUCCESS")){
						throw new CustomFault(isValidinput);
					}
					}
				List tenancyIdList = (List) reqObj.get("tenancyIdList");
				String accountIdStringList=new DateUtil().getAccountListForTheTenancy(tenancyIdList);
				
			
				rolledOffMachinesData = new RolledOffMachinesOnMapImpl().getRolledOffMachinesData(accountIdStringList,date);
				long endTime = System.currentTimeMillis();
				iLogger.info("RolledOffMachinesOnMapService:getRolledOffMachines :: time taken  "+(endTime-startTime));
		}catch (Exception e) {
			e.printStackTrace();
			fLogger.error("Exception:"+e.getMessage());
		}
		
		return rolledOffMachinesData;
}
	
}
