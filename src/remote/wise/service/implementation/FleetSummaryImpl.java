package remote.wise.service.implementation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.ContactEntity;
import remote.wise.businessentity.GroupUserMapping;
import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.FleetSummaryReqContract;
import remote.wise.service.datacontract.FleetSummaryRespContract;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class FleetSummaryImpl {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("FleetSummaryImpl:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("FleetSummaryImpl:","fatalError");*/

	private double totalIdleHours;
	private double totalWorkingHours;
//	Keerthi : 19/12/2013 : ID: 1828 
	private double totalOffHours;
	//DF20190308:Abhishek:To display date in FleetUtilization chart in UI
	private String resp_Date;
		
	public String getResp_Date() {
			return resp_Date;
		}
	
	public void setResp_Date(String resp_Date) {
			this.resp_Date = resp_Date;
		}
	

	public double getTotalIdleHours() {
		return totalIdleHours;
	}



	public void setTotalIdleHours(double totalIdleHours) {
		this.totalIdleHours = totalIdleHours;
	}



	public double getTotalWorkingHours() {
		return totalWorkingHours;
	}



	public void setTotalWorkingHours(double totalWorkingHours) {
		this.totalWorkingHours = totalWorkingHours;
	}

	
	public double getTotalOffHours() {
		return totalOffHours;
	}



	public void setTotalOffHours(double totalOffHours) {
		this.totalOffHours = totalOffHours;
	}



	public List<FleetSummaryRespContract> getFleetSummaryService(FleetSummaryReqContract request) throws CustomFault
	{
		//Logger businessError = Logger.getLogger("businessErrorLogger");
				//Changes Done by Juhi On 6 May 2013
				Logger bLogger = BusinessErrorLoggerClass.logger;
				Logger fLogger = FatalLoggerClass.logger;
				try
				{
					if(request.getPeriod()==null)
					{
						throw new CustomFault("Period is not specified");
					}
					if(request.getContactId()==null)
					{
						throw new CustomFault("Contact is not specified");
					}
					if(request.getTenancyIdList()==null)
					{
						throw new CustomFault("Tenancy Id List should be specified");
					}
					
					
					if(request.getPeriod()!=null)
		            {
		                  if( !(request.getPeriod().equalsIgnoreCase("Week") || request.getPeriod().equalsIgnoreCase("Month") ||
		                		  request.getPeriod().equalsIgnoreCase("Quarter") || request.getPeriod().equalsIgnoreCase("Year") ||
		                		  request.getPeriod().equalsIgnoreCase("Last Week") ||      request.getPeriod().equalsIgnoreCase("Last Month") ||
		                		  request.getPeriod().equalsIgnoreCase("Last Quarter") || request.getPeriod().equalsIgnoreCase("Last Year") 
		                		 //DefectID: DF20140115 - Rajani Nagaraju - Was throwing Custom Fault for active alerts
		                		  || request.getPeriod().equalsIgnoreCase("Today")) )
		                  {
		                        throw new CustomFault("Invalid Time Period");
		                        
		                  }
		      }

				}
				
				catch(CustomFault e)
				{
					bLogger.error("Custom Fault: "+ e.getFaultInfo());
				}
				
				List<FleetSummaryRespContract> listFleetSummaryRespContract=new LinkedList<FleetSummaryRespContract>();
				FleetSummaryRespContract fleetSummaryRespContract=new FleetSummaryRespContract();
				AssetDetailsBO assetDetailsBO=new AssetDetailsBO();
				List<FleetSummaryImpl> fleetSummaryImpl = new LinkedList<FleetSummaryImpl>();
				DomainServiceImpl domainService = new DomainServiceImpl();
				ContactEntity contact = domainService.getContactDetails(request.getContactId());
				if(contact==null || contact.getContact_id()==null)
				{
					throw new CustomFault("Invalid Contactid");
				}
				
				//Changes Done by Juhi On 6 May 2013
				//If the user is tenancyAdmin
				if(contact.getIs_tenancy_admin()==1){
				fleetSummaryImpl=assetDetailsBO.getFleetSummaryService(request.getPeriod(),request.getContactId(),request.getTenancyIdList(),request.getAssetGroupIdList(),request.getAssetTypeIdList(),request.getCustomAssetGroupIdList(),request.getLandmarkIdList(),request.getAlertSeverity(),request.getAlertTypeIdList(),request.getMachineGroupId(),request.getNotificationDimensionID(),request.isOwnStock());
				}
				//If the user is not tenancyAdmin
				else{

					List<Integer> customMachineGroupIdList = new LinkedList<Integer>();
					if((request.getCustomAssetGroupIdList()==null || request.getCustomAssetGroupIdList().isEmpty()) )
					{
						 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
					     session.beginTransaction();
					     
					     try
					     {
					    	 Query query = session.createQuery("from GroupUserMapping where contact_id='"+contact.getContact_id()+"'");
					    	 Iterator itr = query.list().iterator();
					    	 while(itr.hasNext())
					    	 {
					    		 GroupUserMapping groupUser = (GroupUserMapping) itr.next();
					    		 customMachineGroupIdList.add(groupUser.getGroup_id().getGroup_id());
					    	 }
					     }
					     
					      
					    catch(Exception e)
						{
							fLogger.fatal("Exception :"+e);
						}
				            
				        finally
				        {
				             if(session.getTransaction().isActive())
				             {
				                  session.getTransaction().commit();
				             }
				                  
				             if(session.isOpen())
				             {
				                  session.flush();
				                  session.close();
				             }
				                  
				         }
					}
					
					else
					{
						if(request.getCustomAssetGroupIdList()!=null)
							customMachineGroupIdList.addAll(request.getMachineGroupId());
					}
					
					fleetSummaryImpl=assetDetailsBO.getFleetSummaryService(request.getPeriod(),request.getContactId(),request.getTenancyIdList(),request.getAssetGroupIdList(),request.getAssetTypeIdList(),customMachineGroupIdList,request.getLandmarkIdList(),request.getAlertSeverity(),request.getAlertTypeIdList(),request.getMachineGroupId(),request.getNotificationDimensionID(),request.isOwnStock());
					
				}
				for(int i=0;i<fleetSummaryImpl.size();i++)
				{//Changes Done by Juhi On 6 May 2013
					if(fleetSummaryImpl.get(i).getTotalOffHours()<0)
					{
						fleetSummaryRespContract.setTotalOffHours(0);

					}
					else{
						fleetSummaryRespContract.setTotalOffHours(fleetSummaryImpl.get(i).getTotalOffHours());
					}
					if(fleetSummaryImpl.get(i).getTotalIdleHours()<0)
					{
						fleetSummaryRespContract.setTotalIdleHours(0);

					}
					else{
						fleetSummaryRespContract.setTotalIdleHours(fleetSummaryImpl.get(i).getTotalIdleHours());
					}
					if(fleetSummaryImpl.get(i).getTotalWorkingHours()<0)
					{
						fleetSummaryRespContract.setTotalWorkingHours(0);
					}
					else{
						fleetSummaryRespContract.setTotalWorkingHours(fleetSummaryImpl.get(i).getTotalWorkingHours());	
					}
					
					//Changes Done by Juhi On 6 May 2013
					
					//DF20190308:Abhishek:To display date in FleetUtilization chart in UI
					fleetSummaryRespContract.setResp_Date(fleetSummaryImpl.get(i).getResp_Date());
					
				
					listFleetSummaryRespContract.add(fleetSummaryRespContract);
				}
				return listFleetSummaryRespContract;
			}
}
