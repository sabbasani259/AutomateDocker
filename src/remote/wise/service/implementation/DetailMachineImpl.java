/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.ActiveMachineListReportBO;
import remote.wise.businessobject.MachineInfoDetailsBO;
import remote.wise.businessobject.ReportDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.ActiveMachineListOutputContract;
import remote.wise.service.datacontract.DetailMachineInfoReqContract;
import remote.wise.service.datacontract.DetailMachineInfoRespContract;
import remote.wise.service.datacontract.MachineActivityReportRespContract;
//import remote.wise.util.WiseLogger;

/**
 * @author sunayak
 *
 */
public class DetailMachineImpl {
	
	//public static WiseLogger businessError = WiseLogger.getLogger("MachineActivityReportImpl:","businessError");
	private String Current_Owner;
	private String MachineHour;
	private String FW_Version_Number;
	private String SerialNumber;
	private String Last_Reported;
	/**
	 * @return the last_Reported
	 */
	public String getLast_Reported() {
		return Last_Reported;
	}

	/**
	 * @param last_Reported the last_Reported to set
	 */
	public void setLast_Reported(String last_Reported) {
		Last_Reported = last_Reported;
	}

	/**
	 * @return the rollOff_Date
	 */
	public String getRollOff_Date() {
		return RollOff_Date;
	}

	/**
	 * @param rollOff_Date the rollOff_Date to set
	 */
	public void setRollOff_Date(String rollOff_Date) {
		RollOff_Date = rollOff_Date;
	}



	private String RollOff_Date;
	/**
	 * @return the current_Owner
	 */
	public String getCurrent_Owner() {
		return Current_Owner;
	}

	/**
	 * @param current_Owner the current_Owner to set
	 */
	public void setCurrent_Owner(String current_Owner) {
		Current_Owner = current_Owner;
	}

	/**
	 * @return the machineHour
	 */
	public String getMachineHour() {
		return MachineHour;
	}

	/**
	 * @param machineHour the machineHour to set
	 */
	public void setMachineHour(String machineHour) {
		MachineHour = machineHour;
	}

	/**
	 * @return the fW_Version_Number
	 */
	public String getFW_Version_Number() {
		return FW_Version_Number;
	}

	/**
	 * @param fW_Version_Number the fW_Version_Number to set
	 */
	public void setFW_Version_Number(String fW_Version_Number) {
		FW_Version_Number = fW_Version_Number;
	}

	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return SerialNumber;
	}

	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}

	

	public List<DetailMachineInfoRespContract> getMachineInfo(
			DetailMachineInfoReqContract reqObj) {
		// TODO Auto-generated method stub
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		try
		{
			//Custom Dates (fromDate,toDate) added by Juhi on 12-August-2013 
			//DefectId:  DF20131014 - Custom Date Implementation - 20131014 - Rajani Nagaraju - && changed to || here - since for custom dates, both fromDate and todate is required
			if(((reqObj.getContact_id()==null))&&(reqObj.getRole_id()==0))
			{
				throw new CustomFault("Contact_id and Role_id should be specified");
			}
			
		}
		
		catch(CustomFault e)
		{
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		List<DetailMachineInfoRespContract> respList = new LinkedList<DetailMachineInfoRespContract>();
		MachineInfoDetailsBO machineDetailsBO=new MachineInfoDetailsBO();
		List<DetailMachineImpl> machineDetailsInfoImpl = new LinkedList<DetailMachineImpl>();
		machineDetailsInfoImpl =  machineDetailsBO.getMachineListInfo();
		
		for(int i=0;i<machineDetailsInfoImpl.size();i++)
		{	
			DetailMachineInfoRespContract respContractObj=new DetailMachineInfoRespContract();
			if (machineDetailsInfoImpl.get(i).getSerialNumber()!=null)
			{
				respContractObj.setSerialNumber(machineDetailsInfoImpl.get(i).getSerialNumber());
				
			} 
			else {
				respContractObj.setSerialNumber("NA");
			}
			if (machineDetailsInfoImpl.get(i).getCurrent_Owner()!=null)
			{
				respContractObj.setCurrent_Owner(machineDetailsInfoImpl.get(i).getCurrent_Owner());
				
			} 
			else {
				respContractObj.setCurrent_Owner("NA");
			}
			if (machineDetailsInfoImpl.get(i).getMachineHour()!=null)
			{
				respContractObj.setMachineHour(machineDetailsInfoImpl.get(i).getMachineHour());
				
			} 
			else {
				respContractObj.setMachineHour("NA");
			}
			if (machineDetailsInfoImpl.get(i).getLast_Reported()!=null)
			{
				respContractObj.setLast_Reported(machineDetailsInfoImpl.get(i).getLast_Reported());
				
			} 
			else {
				respContractObj.setLast_Reported("NA");
			}
			if (machineDetailsInfoImpl.get(i).getFW_Version_Number()!=null)
			{
				respContractObj.setFW_Version_Number(machineDetailsInfoImpl.get(i).getFW_Version_Number());
				
			} 
			else {
				respContractObj.setFW_Version_Number("NA");
			}
			if (machineDetailsInfoImpl.get(i).getRollOff_Date()!=null)
			{
				respContractObj.setRollOff_Date(machineDetailsInfoImpl.get(i).getRollOff_Date());
				
			} 
			else {
				respContractObj.setRollOff_Date("NA");
			}
			/*respContractObj.setSerialNumber(machineDetailsInfoImpl.get(i).getSerialNumber());
			respContractObj.setCurrent_Owner(machineDetailsInfoImpl.get(i).getCurrent_Owner());
			respContractObj.setMachineHour(machineDetailsInfoImpl.get(i).getMachineHour());
			respContractObj.setLast_Reported(machineDetailsInfoImpl.get(i).getLast_Reported());
			respContractObj.setFW_Version_Number(machineDetailsInfoImpl.get(i).getFW_Version_Number());
			respContractObj.setRollOff_Date(machineDetailsInfoImpl.get(i).getRollOff_Date());*/
			respList.add(respContractObj);
		}
		
		
		return respList;
	}

}
