package remote.wise.service.implementation;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

////import org.apache.log4j.Logger;

import remote.wise.businessobject.ReportDetailsBO;

import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.LandmarkActivityReportReqContract;
import remote.wise.service.datacontract.LandmarkActivityReportRespContract;
//import remote.wise.util.WiseLogger;


/** Implementation Class to get the Machine Activity Details at a Landmark
 * @author jgupta41
 *
 */
public class LandmarkActivityReportImpl 
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//	public static WiseLogger businessError = WiseLogger.getLogger("LandmarkActivityReportImpl:","businessError");
	private int landmarkId;
	private String landMarkName;
	private int landmarkCategoryId;
	private String landMarkCategoryName;
	private String serialNumber;
	private String nickname;
	private int numberOfArrivals;
	private int numberOfdepartures;
	private long totalDurationAtLandmarkInMinutes;
	private long longestDurationAtLandmarkInMinutes;
	//added by smitha on Aug 27th 2013
	private int machineGroupId;
	private String machineGroupName;	
	private String dealerName;
	
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public int getMachineGroupId() {
		return machineGroupId;
	}
	public void setMachineGroupId(int machineGroupId) {
		this.machineGroupId = machineGroupId;
	}
	public String getMachineGroupName() {
		return machineGroupName;
	}
	public void setMachineGroupName(String machineGroupName) {
		this.machineGroupName = machineGroupName;
	}
	//ended on Aug 27th 2013
	
	public int getLandmarkId() {
		return landmarkId;
	}


	public void setLandmarkId(int landmarkId) {
		this.landmarkId = landmarkId;
	}


	public String getLandMarkName() {
		return landMarkName;
	}


	public void setLandMarkName(String landMarkName) {
		this.landMarkName = landMarkName;
	}


	public int getLandmarkCategoryId() {
		return landmarkCategoryId;
	}


	public void setLandmarkCategoryId(int landmarkCategoryId) {
		this.landmarkCategoryId = landmarkCategoryId;
	}


	public String getLandMarkCategoryName() {
		return landMarkCategoryName;
	}


	public void setLandMarkCategoryName(String landMarkCategoryName) {
		this.landMarkCategoryName = landMarkCategoryName;
	}


	public String getSerialNumber() {
		return serialNumber;
	}


	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	public String getNickname() {
		return nickname;
	}


	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	public int getNumberOfArrivals() {
		return numberOfArrivals;
	}


	public void setNumberOfArrivals(int numberOfArrivals) {
		this.numberOfArrivals = numberOfArrivals;
	}


	public int getNumberOfdepartures() {
		return numberOfdepartures;
	}


	public void setNumberOfdepartures(int numberOfdepartures) {
		this.numberOfdepartures = numberOfdepartures;
	}


	public long getTotalDurationAtLandmarkInMinutes() {
		return totalDurationAtLandmarkInMinutes;
	}


	public void setTotalDurationAtLandmarkInMinutes(
			long totalDurationAtLandmarkInMinutes) {
		this.totalDurationAtLandmarkInMinutes = totalDurationAtLandmarkInMinutes;
	}


	public long getLongestDurationAtLandmarkInMinutes() {
		return longestDurationAtLandmarkInMinutes;
	}


	public void setLongestDurationAtLandmarkInMinutes(
			long longestDurationAtLandmarkInMinutes) {
		this.longestDurationAtLandmarkInMinutes = longestDurationAtLandmarkInMinutes;
	}


	/** This method returns the Machine Activity in the specified Landmarks
	 * @param landmarkActivityReq ReqObj specifies the Landmark/Landmark Category for which the MachineActivity details to be reported
	 * @return Returns the MachineActivity details under a landmark
	 */
	public List<LandmarkActivityReportRespContract> getLandmarkActivityReport(LandmarkActivityReportReqContract reqObj)
	{
		List<LandmarkActivityReportRespContract> respList = new LinkedList<LandmarkActivityReportRespContract>();
	//	Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		//Check for Mandatory Parameters
		try
		{
			if(reqObj.getLoginId()==null)
			{
				throw new CustomFault("LoginId is NULL");
			}
			
			if(reqObj.getLoginTenancyId()==0)
			{
				throw new CustomFault("TenancyId not specified");
			}
						

			//Custom Dates (fromDate,toDate) added by Juhi on 19-August-2013 
			if(((reqObj.getPeriod()==null))&&((reqObj.getFromDate()==null)&& (reqObj.getToDate()==null)))
			{
				
				bLogger.error("Please pass either Period or custom dates");
				throw new CustomFault("Either Period or custom dates are not specified");
			}
			
					
			//Check for the valid String input for period
			if(reqObj.getPeriod()!=null)
            {
                  if( !(reqObj.getPeriod().equalsIgnoreCase("Week") || reqObj.getPeriod().equalsIgnoreCase("Month") ||
                              reqObj.getPeriod().equalsIgnoreCase("Quarter") || reqObj.getPeriod().equalsIgnoreCase("Year") ||
                              reqObj.getPeriod().equalsIgnoreCase("Last Week") ||      reqObj.getPeriod().equalsIgnoreCase("Last Month") ||
                              reqObj.getPeriod().equalsIgnoreCase("Last Quarter") || reqObj.getPeriod().equalsIgnoreCase("Last Year") ) )
                  {
                        throw new CustomFault("Invalid Time Period");
                        
                  }
            }

		}

		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		
		
		//Call the BO to get the details
		// Custom Dates (fromDate,toDate) added by Juhi on 21-August-2013 
		List<LandmarkActivityReportImpl> landmarkActivityReportImpl = new LinkedList<LandmarkActivityReportImpl>();
		landmarkActivityReportImpl= new ReportDetailsBO().getLandmarkActivityDetails(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getPeriod(),reqObj.getLandmarkIdList(),
															reqObj.getLandmarkCategoryIDList(), reqObj.getLoginId(), reqObj.getLoginTenancyId(),reqObj.getMachineGroupIdList());
				
	
		for(int i=0;i<landmarkActivityReportImpl.size();i++)
		{	
			
			LandmarkActivityReportRespContract  respContractObj = new LandmarkActivityReportRespContract();
			
			respContractObj.setLandmarkId(landmarkActivityReportImpl.get(i).getLandmarkId());
			respContractObj.setLandMarkName(landmarkActivityReportImpl.get(i).getLandMarkName());
			
			respContractObj.setLandmarkCategoryId(landmarkActivityReportImpl.get(i).getLandmarkCategoryId());
			respContractObj.setLandMarkCategoryName(landmarkActivityReportImpl.get(i).getLandMarkCategoryName());
			
			respContractObj.setSerialNumber(landmarkActivityReportImpl.get(i).getSerialNumber());
			respContractObj.setNickname(landmarkActivityReportImpl.get(i).getNickname());
			
			if(!(landmarkActivityReportImpl.get(i).getNumberOfArrivals()<0))
				respContractObj.setNumberOfArrivals(landmarkActivityReportImpl.get(i).getNumberOfArrivals());
			if(!(landmarkActivityReportImpl.get(i).getNumberOfdepartures()<0))
				respContractObj.setNumberOfdepartures(landmarkActivityReportImpl.get(i).getNumberOfdepartures());
			
			if(!(landmarkActivityReportImpl.get(i).getLongestDurationAtLandmarkInMinutes()<0))
				respContractObj.setTotalDurationAtLandmarkInMinutes(landmarkActivityReportImpl.get(i).getTotalDurationAtLandmarkInMinutes());
			if(!(landmarkActivityReportImpl.get(i).getLongestDurationAtLandmarkInMinutes()<0))
				respContractObj.setLongestDurationAtLandmarkInMinutes(landmarkActivityReportImpl.get(i).getLongestDurationAtLandmarkInMinutes());
			
			respContractObj.setMachineGroupId(landmarkActivityReportImpl.get(i).getMachineGroupId());
			respContractObj.setMachineGroupName(landmarkActivityReportImpl.get(i).getMachineGroupName());
			//DefectId:20150225  @Suprava DealerName Added As a new parameter
			respContractObj.setDealerName(landmarkActivityReportImpl.get(i).getDealerName());
			respList.add(respContractObj);
		}
		
		
		return respList;
	}
	
}
