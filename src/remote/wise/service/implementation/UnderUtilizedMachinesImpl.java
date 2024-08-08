package remote.wise.service.implementation;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.service.datacontract.UnderUtilizedMachinesReqContract;
import remote.wise.service.datacontract.UnderUtilizedMachinesRespContract;
//import remote.wise.util.WiseLogger;
/**
 * UnderUtilizedMachinesImpl will allow to List of UnderUtilized Machines for given List of Tenancy Id and for filters if provided
 * @author jgupta41
 *
 */
public class UnderUtilizedMachinesImpl {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	//public static WiseLogger businessError = WiseLogger.getLogger("UnderUtilizedMachinesImpl:","businessError");
	
	private String Serial_no;
	private double Engine_Off_Hours_Perct;
	private double Working_Time;
	private double WorkingTimePercentage;;
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

	public double getWorking_Time() {
		return Working_Time;
	}
	public void setWorking_Time(double working_Time) {
		Working_Time = working_Time;
	}
	public double getWorkingTimePercentage() {
		return WorkingTimePercentage;
	}
	public void setWorkingTimePercentage(double workingTimePercentage) {
		WorkingTimePercentage = workingTimePercentage;
	}
	//*********************************Get UnderUtilized Machines for given Period, List of Tenancy Id and for filters if provided*******************************
	/**
	 * This method will return List of UnderUtilized Machines for given List of Tenancy and for filters if provided
	 * @param underUtilizedReq: Get UnderUtilized Machines for given Period, List of Tenancy Id
	 * @return respList: Returns List of UnderUtilized Machines
	 * @throws CustomFault :custom exception is thrown when Period,Tenancy_ID is not specified, Tenancy ID is invalid or not specified
	 */
	public List<UnderUtilizedMachinesRespContract> getUnderUtilizedMachines(UnderUtilizedMachinesReqContract underUtilizedReq)throws CustomFault
	{
		Logger bLogger = BusinessErrorLoggerClass.logger;
		if (underUtilizedReq.getLoginID() == null) {
			bLogger.error("Login ID is not provided");
			throw new CustomFault("Pleae provide Login ID");
		}
		if (underUtilizedReq.getPeriod() == null) {
			bLogger.error("Please pass a Period");
			throw new CustomFault("Please pass a Period");
		}

		if (underUtilizedReq.getTenancy_ID() == null) {
			bLogger.error("Please pass a Tenancy_ID");
			throw new CustomFault("Please pass a Tenancy_ID");
		}
		List<UnderUtilizedMachinesRespContract> respList = new LinkedList<UnderUtilizedMachinesRespContract>();
		
		
		ReportDetailsBO underUtilizedMachinesBO= new ReportDetailsBO();
		List<UnderUtilizedMachinesImpl> underUtilizedMachinesImpl = new LinkedList<UnderUtilizedMachinesImpl>();		
		underUtilizedMachinesImpl = underUtilizedMachinesBO.getUnderUtilizedMachinesObj(underUtilizedReq.getLoginID(),underUtilizedReq.getTenancy_ID(),underUtilizedReq.getPeriod(),underUtilizedReq.getMachineProfile_ID(),underUtilizedReq.getModel_ID(),underUtilizedReq.getMachineGroupType_ID(),underUtilizedReq.getCustomAssetGroup_ID());
		//Sorting based on working time perc added by Juhi 18-November-2013
		Collections.sort(underUtilizedMachinesImpl, new transactionComparator());
		for(int i=0;i<underUtilizedMachinesImpl.size();i++)
		{
		UnderUtilizedMachinesRespContract  respContractObj=new UnderUtilizedMachinesRespContract();
		respContractObj.setSerial_no(underUtilizedMachinesImpl.get(i).getSerial_no());
//		Keerthi : 15/11/13 : rounding off to 2 places for working time percentage
//		double workingTimePercent = Math.round(underUtilizedMachinesImpl.get(i).getWorkingTimePercentage()*100);
//		double workingTimePercentRounded = workingTimePercent/100;		
		respContractObj.setWorkingTimePercentage(underUtilizedMachinesImpl.get(i).getWorkingTimePercentage());
//		Keerthi : 02/03/14 : rounding off to 2 places for working time
//		workingTimePercent = Math.round(underUtilizedMachinesImpl.get(i).getWorking_Time()*100);
//		workingTimePercentRounded = workingTimePercent/100;	
		respContractObj.setWorking_Time(underUtilizedMachinesImpl.get(i).getWorking_Time());
//		workingTimePercent = Math.round(underUtilizedMachinesImpl.get(i).getEngine_Off_Hours_Perct()*100);
//		workingTimePercentRounded = workingTimePercent/100;	
		respContractObj.setEngine_Off_Hours_Perct(underUtilizedMachinesImpl.get(i).getEngine_Off_Hours_Perct());
	
		respList.add(respContractObj);
		}
		return respList;
	}
	//*********************************End of Get UnderUtilized Machines for given Period, List of Tenancy Id and for filters if provided*******************************


	class transactionComparator implements Comparator<UnderUtilizedMachinesImpl>
	{
//Added by Juhi 18-November-2013
		@Override
		
		public int compare(UnderUtilizedMachinesImpl arg0, UnderUtilizedMachinesImpl arg1) 
		{
			// TODO Auto-generated method stub
			double w1 = 0;
			double w2= 0;			
			
				 w1 = arg0.getWorkingTimePercentage();
				 w2= arg1.getWorkingTimePercentage();	
						
				 if( w1== w2)  
					 return 0;  
					 else if( w1> w2)  
					 return 1;  
					 else  
					 return -1; 
		}
		}
}
