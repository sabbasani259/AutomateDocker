package remote.wise.service.implementation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.EventDetailsBO;
import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.AlertSummaryRespContract;
import remote.wise.service.datacontract.AlertThresholdRespContract;
import remote.wise.service.datacontract.NotificationSummaryReportReqContract;
import remote.wise.service.datacontract.NotificationSummaryReportRespContract;
//import remote.wise.util.WiseLogger;

public class NotificationSummaryReportImpl {
                //Defect Id 1337 - Logger changes
   //public static WiseLogger businessError = WiseLogger.getLogger("NotificationSummaryReportImpl:","businessError");

   private Long Count;
   private int AssetGroupId;
   private String AssetGroupName;
   private int TenancyId;
   private String NotificationTypeName;
   private String NickName;
   private String serialNumber;
   private String tenancy_name;
   private String dealerName;
   /**
 * @return the dealerName
 */
public String getDealerName() {
	return dealerName;
}
/**
 * @param dealerName the dealerName to set
 */
public void setDealerName(String dealerName) {
	this.dealerName = dealerName;
}
HashMap<String,Long> nameCount;

   public HashMap<String, Long> getNameCount() {
          return nameCount;
   }
   public void setNameCount(HashMap<String, Long> nameCount) {
          this.nameCount = nameCount;
   }
   private int AssetTypeId;//Now added
   private String AssetTypeName;//Now added
   public int getAssetTypeId() {
          return AssetTypeId;
   }
   public void setAssetTypeId(int assetTypeId) {
          AssetTypeId = assetTypeId;
   }
   public String getAssetTypeName() {
          return AssetTypeName;
   }
   public void setAssetTypeName(String assetTypeName) {
          AssetTypeName = assetTypeName;
   }
   private int machineGroupId;//Now added
   private String machineGroupName;
                
   public String getSerialNumber() {
          return serialNumber;
   }
   public void setSerialNumber(String serialNumber) {
          this.serialNumber = serialNumber;
   }
   public String getTenancy_name() {
          return tenancy_name;
   }
   public void setTenancy_name(String tenancy_name) {
          this.tenancy_name = tenancy_name;
   }
   public String getMachineGroupName() {
          return machineGroupName;
   }
   public void setMachineGroupName(String machineGroupName) {
          this.machineGroupName = machineGroupName;
   }       
   public String getNickName() {
          return NickName;
   }
   public void setNickName(String nickName) {
          NickName = nickName;
   }
   public String getNotificationTypeName() {
          return NotificationTypeName;
   }
   public void setNotificationTypeName(String notificationTypeName) {
          NotificationTypeName = notificationTypeName;
   }
   public Long getCount() {
          return Count;
   }
   public void setCount(Long count) {
          Count = count;
   }
   public int getAssetGroupId() {
          return AssetGroupId;
   }
   public void setAssetGroupId(int assetGroupId) {
          AssetGroupId = assetGroupId;
   }
   public String getAssetGroupName() {
          return AssetGroupName;
   }
   public void setAssetGroupName(String assetGroupName) {
          AssetGroupName = assetGroupName;
   }
   public int getTenancyId() {
          return TenancyId;
   }
   public void setTenancyId(int tenancyId) {
          TenancyId = tenancyId;
   }
   public int getMachineGroupId() {
          return machineGroupId;
   }
   public void setMachineGroupId(int machineGroupId) {
          this.machineGroupId = machineGroupId;
   }                
   
   public List<NotificationSummaryReportRespContract> GetNotificationSummaryDetails(NotificationSummaryReportReqContract reqObj)throws CustomFault{
	   Logger bLogger = BusinessErrorLoggerClass.logger;
	   try
           {
              	//Custom Dates (fromDate,toDate) added by Juhi on 14-August-2013 
                 	 if(((reqObj.getPeriod()==null))&&((reqObj.getFromDate()==null) && (reqObj.getToDate()==null)))
                      	{
                                                                
                            	 bLogger.error("Please pass either Period or custom dates");
                                 	throw new CustomFault("Either Period or custom dates are not specified");
                         }                                                
                         if(reqObj.getTenancyIdList()==null)
                         {
                                 throw new CustomFault("Tenancy Id List should be specified");
                          }
                         
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
                                //Changes Done by Juhi On 6 May 2013
                           ReportDetailsBO reportBO=new ReportDetailsBO();
                           List<NotificationSummaryReportRespContract> respList = new LinkedList<NotificationSummaryReportRespContract>();
                           List<NotificationSummaryReportImpl>summaryImpl = new LinkedList<NotificationSummaryReportImpl>();
                           //Custom Dates (fromDate,toDate) added by Juhi on 14-August-2013 
                           summaryImpl = reportBO.GetNotificationSummaryReportDetails(reqObj.getFromDate(),reqObj.getToDate(),reqObj.getLoginId(), reqObj.getPeriod(), reqObj.getTenancyIdList(), 
                           reqObj.getMachineGroupIdList(), reqObj.getMachineProfileIdList(), reqObj.isMachineGroup(), reqObj.getModelidList(), 
                           //Defect Id:1406 Added loginTenancyIdList by Juhi on 30-October-2013
                           reqObj.isMachineProfile(), reqObj.isModel(),reqObj.getAlertTypeIdList(),reqObj.getLoginTenancyIdList());                          
                              
                           for(int i=0;i<summaryImpl.size();i++){
                                  NotificationSummaryReportRespContract respObj1 = new NotificationSummaryReportRespContract();
                                                
                                   respObj1.setAssetGroupId(summaryImpl.get(i).getAssetGroupId());
                                   respObj1.setAssetGroupName(summaryImpl.get(i).getAssetGroupName());
                                   respObj1.setCount(summaryImpl.get(i).getCount());
                                   respObj1.setTenancyId(summaryImpl.get(i).getTenancyId());
                                   respObj1.setNotificationTypeName(summaryImpl.get(i).getNotificationTypeName());
                                   respObj1.setNickName(summaryImpl.get(i).getNickName());
                                   respObj1.setSerialNumber(summaryImpl.get(i).getSerialNumber());
                                   respObj1.setTenancy_name(summaryImpl.get(i).getTenancy_name());
                                   respObj1.setMachineGroupId(summaryImpl.get(i).getMachineGroupId());
                                   respObj1.setMachineGroupName(summaryImpl.get(i).getMachineGroupName());
                                   respObj1.setAssetTypeId(summaryImpl.get(i).getAssetTypeId());
                                   respObj1.setAssetTypeName(summaryImpl.get(i).getAssetTypeName());
                                   respObj1.setNameCount(summaryImpl.get(i).getNameCount());
                                   respObj1.setDealerName(summaryImpl.get(i).getDealerName());
                                   
                                respList.add(respObj1);
                              }                                
                                return respList;                
       }    
}


