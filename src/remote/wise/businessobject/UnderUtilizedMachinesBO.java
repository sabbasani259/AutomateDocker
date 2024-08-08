package remote.wise.businessobject;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import remote.wise.businessentity.AssetMonitoringFactDataEntity;
import remote.wise.businessentity.GroupUserMapping;
import remote.wise.businessentity.LandmarkAssetEntity;
import remote.wise.businessentity.LandmarkEntity;
import remote.wise.businessentity.MachineGroupDimensionEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.implementation.LandmarkAssetImpl;
import remote.wise.service.implementation.UnderUtilizedMachinesImpl;

import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;
//import remote.wise.util.WiseLogger;

public class UnderUtilizedMachinesBO {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("UnderUtilizedMachinesBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("UnderUtilizedMachinesBO:","fatalError");*/
	
	private String Serial_no;
	private double Engine_Off_Hours_Perct;
	private long Working_Time;
	private double WorkingTimePercentage;
	

	public String getSerial_no() {
		return Serial_no;
	}

	public void setSerial_no(String serial_no) {
		Serial_no = serial_no;
	}

	
public double getEngine_Off_Hours_Perct() {
		return Engine_Off_Hours_Perct;
	}

	public void setEngine_Off_Hours_Perct(double engine_Off_Hours_Perct) {
		Engine_Off_Hours_Perct = engine_Off_Hours_Perct;
	}

	public long getWorking_Time() {
		return Working_Time;
	}

	public void setWorking_Time(long working_Time) {
		Working_Time = working_Time;
	}

	public double getWorkingTimePercentage() {
		return WorkingTimePercentage;
	}

	public void setWorkingTimePercentage(double workingTimePercentage) {
		WorkingTimePercentage = workingTimePercentage;
	}

	//*********************************Get UnderUtilizedMachines Object List*******************************
	public List<UnderUtilizedMachinesImpl>  getUnderUtilizedMachinesObj(List<Integer> Tenancy_ID,String Period,List<Integer> MachineProfile_ID,List<Integer> Model_ID,List<Integer> MachineGroupType_ID,List<Integer> MachineGroup_ID) throws CustomFault 
	{
		List<UnderUtilizedMachinesImpl> underUtilizedList = new LinkedList<UnderUtilizedMachinesImpl>();
		//get the period and hence select the corresponding table
		//Period can be 'Today','Week','Month', 'Quarter', 'Year'
		String basicFromQuery = null;
		String basicSelectQuery = null;
		String basicWhereQuery = null;
		String finalQuery = null;
		
		if(Period==null)
			throw new CustomFault("Please pass a Period");
		if(Tenancy_ID==null)
			throw new CustomFault("Please pass a Tenancy_ID");
		
		//Logger businessError = Logger.getLogger("businessErrorLogger");
        //Logger fatalError = Logger.getLogger("fatalErrorLogger");
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

try{
		ListToStringConversion conversionObj = new ListToStringConversion();
		String tenancyIdStringList = conversionObj.getIntegerListString(Tenancy_ID).toString();
		
		basicSelectQuery = "select a.serialNumber, a.workingTime ,a.engineOffPercentage , a.workingTimePercentage";

		basicWhereQuery = " where a.tenancyId = b.tenancyId and b.tenancyId in (" + tenancyIdStringList + " )" ;

		if(Period.equalsIgnoreCase("Today"))
		{
		basicFromQuery = " from AssetMonitoringFactDataDayAgg_json a ";
		
		}
		else if(Period.equalsIgnoreCase("Week"))
		{
		basicFromQuery = " from AssetMonitoringFactDataWeekAgg a ";
		
		}
		else if(Period.equalsIgnoreCase("Month"))
		{
		basicFromQuery = " from AssetMonitoringFactDataMonthAgg a ";
		
		}
		else if(Period.equalsIgnoreCase("Quarter"))
		{
		basicFromQuery = " from AssetMonitoringFactDataQuarterAgg a ";
	
		}
		else if(Period.equalsIgnoreCase("Year"))
		{
		basicFromQuery = " from AssetMonitoringFactDataYearAgg a ";
		
		}

		basicFromQuery = basicFromQuery +" JOIN a.tenancyId b JOIN a.machineGroupId c JOIN a.assetClassDimensionId d";
		
		
		if(! (MachineGroup_ID==null || MachineGroup_ID.isEmpty()) )
		{
		String MachineGroup_IDStringList = conversionObj.getIntegerListString(MachineGroup_ID).toString();
		basicWhereQuery = basicWhereQuery + " and c.machineGroupId in ( " + MachineGroup_IDStringList +" )"; 

		}
		int flag=0;
		if(! (MachineGroupType_ID==null || MachineGroupType_ID.isEmpty()) )
		{
			
			List<Integer>  MachineGroupType_IDList= new LinkedList<Integer>();
			for(int i=0;i<MachineGroupType_ID.size();i++)
			{
				
				Query q = session.createQuery("from MachineGroupDimensionEntity where parentId ="+MachineGroupType_ID.get(i));
				Iterator itr = q.list().iterator();
				while(itr.hasNext())
				{
					MachineGroupDimensionEntity machineGroupDimensionEntity = (MachineGroupDimensionEntity)itr.next();
					MachineGroupType_IDList.add(machineGroupDimensionEntity.getMachineGroupId());
					flag=1;
				}
				
			}
			if(flag==1)
			{
			String	MachineGroupType_IDStringList = conversionObj.getIntegerListString(MachineGroupType_IDList).toString();
			basicWhereQuery = basicWhereQuery + " and c.machineGroupId in ( " + MachineGroupType_IDStringList +" )";
			}
		}

		basicWhereQuery=basicWhereQuery + " order by a.workingTimePercentage asc"; 
		
		
		finalQuery = basicSelectQuery + basicFromQuery + basicWhereQuery;
		
		Query query = session.createQuery(finalQuery);
		Object[] result=null;
		Iterator itr=query.list().iterator();
		while(itr.hasNext())
		{ 			
			result = (Object[]) itr.next();
			UnderUtilizedMachinesImpl underUtilizedMachinesImpl=new UnderUtilizedMachinesImpl();
			underUtilizedMachinesImpl.setEngine_Off_Hours_Perct((Long)result[2]);
			underUtilizedMachinesImpl.setWorkingTimePercentage((Double)result[3]);
			underUtilizedMachinesImpl.setWorking_Time((Long)result[1]);
			underUtilizedMachinesImpl.setSerial_no(result[0].toString());
			
		
			underUtilizedList.add(underUtilizedMachinesImpl);
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
		
		return underUtilizedList ;
	}

}
