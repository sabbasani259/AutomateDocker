package remote.wise.service.webservice;
//CR335-20220624-Balaji MDA reports will send the list of Machine Group Ids as an input and return the list of Asset Ids as a response. 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MachinegroupAssetImpl;
import remote.wise.util.CommonUtil;
import remote.wise.util.ListToStringConversion;

//DF20190705 Avinash Machine Group Code Merge From SIT TO PROD
@Path("/MachineGroupAsset")
public class MachineGroupAsset {
	/*public static void main(String[] args) throws CustomFault {
		List<String> list = new ArrayList<String>();
		list.add("8");
		list.add("7");
		list.add("6");
		MachineGroupAsset impl=new MachineGroupAsset();
		System.out.println(impl.getMachineGroupAssetByMachineIds(list));
		System.out.println(impl.getMachineGroupAssetByMachineIds(list).size());
	}*/

	@GET
	@Path("getMachineGroupAsset")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getMachineGroupAsset(@QueryParam("userID") String userID) throws CustomFault{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		List<String> response = new ArrayList<String>();
		response = null;
		try {
			long startTime = System.currentTimeMillis();
			infoLogger.info("Webservice input - userID :: "+userID);
			
			CommonUtil util = new CommonUtil();
			String isValidinput=null;
			isValidinput = util.inputFieldValidation(String.valueOf(userID));
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
			MachinegroupAssetImpl implObj = new MachinegroupAssetImpl();
			response = implObj.getMachineGroupAsset(userID);
			
			infoLogger.info("----- Webservice Output-----");
			
			for(int i = 0;i<response.size();i++){
				isValidinput = util.inputFieldValidation(response.get(i));
				if(!isValidinput.equals("SUCCESS")){
					throw new CustomFault(isValidinput);
				}
				infoLogger.info("UserID: " + userID + " AssetID: " + response.get(i));
			}
			
			long endTime = System.currentTimeMillis();
			infoLogger.info("serviceName:MachineProfileService~executionTime:"+(endTime-startTime)+"~"+userID+"~");
			
		} catch (HibernateException e) {
			fLogger.fatal("Exception :"+e);
			e.printStackTrace();
		}
		
		return response;
		
	}
	
	//CR335-MachineGroupIDS-SN
	@GET
	@Path("getMachineGroupIds")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getMachineGroupAssetByMachineIds(@QueryParam("machineGroupIds") String machineGroupIds) throws CustomFault{
		Logger infoLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		List<String> response = null;
	    infoLogger.info("Req : List of machine group ids ::" + machineGroupIds);
	    System.out.println("Req : List of machine group ids ::" + machineGroupIds);
		MachinegroupAssetImpl implObj = new MachinegroupAssetImpl();
		String[] grpId=machineGroupIds.split(",");
		
	
		response = implObj.getMachineGroupIDs(machineGroupIds);
		
		infoLogger.info("Resp : List of machine ::" + Arrays.toString(response.toArray()));
		
		return response;
	}
	//CR335-MachineGroupIDS-EN
}
