package remote.wise.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.dal.DynamicAMS_DAL;
import remote.wise.dal.DynamicAMS_Doc_DAL;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.pojo.AmsDAO;
import remote.wise.service.implementation.FotaUpgradedHistoryDetailsImpl;
import remote.wise.util.HibernateUtil;
import remote.wise.util.ListToStringConversion;

/**
 * @author kprabhu5
 * 
 */
public class FotaUpgradedHistoryBO {
	Logger iLogger = InfoLoggerClass.logger;

	public List<FotaUpgradedHistoryDetailsImpl> getFotaUpgradedHistoryDetails(List<String> serialNumberList) {

		List<FotaUpgradedHistoryDetailsImpl> implList = new ArrayList<FotaUpgradedHistoryDetailsImpl>();
		String query = null;
		Iterator iterator = null;
		AssetControlUnitEntity acuObj = null;
		Object[] result = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		ListToStringConversion conversionObj = new ListToStringConversion();
		
		try {
			List<String> serNumberList = new ArrayList<String>();
			List<String> serNumberList1 = new ArrayList<String>();
			List<String> machineNumberList = new ArrayList<String>();
			List<String> validMachineNumberList = new ArrayList<String>();
			List<String> validPINList = new ArrayList<String>();
			String serialNumber = null;
			
			iterator = serialNumberList.iterator();
			
			while(iterator.hasNext()){		
			serialNumber = (String) iterator.next();
				if (serialNumber != null && serialNumber.length() == 7) {
					machineNumberList.add(serialNumber);
				} else if (serialNumber != null && serialNumber.length() == 17) {
					serNumberList.add(serialNumber);
				} else if (serialNumber != null && serialNumber.length() == 20) {
					serNumberList.add(serialNumber.substring(3, 20));
					serNumberList1.add(serialNumber.substring(3, 20));
				}
			}
			
//			get serialNumber from the machine no. list and add to valid PIN list for 7
			String numberString = null;
			if (machineNumberList.size() > 0) {
				numberString = conversionObj.getStringList(machineNumberList).toString();
				query = "SELECT ae.serial_number,ae.machineNumber FROM AssetEntity ae WHERE ae.machineNumber IN ("+ numberString + ") and ae.active_status='1'";
				
				iterator = session.createQuery(query).list().iterator();
				while (iterator.hasNext()) {
					result = (Object[]) iterator.next();
					if (result[0] != null) {
						acuObj = (AssetControlUnitEntity) result[0];
						if (acuObj != null) {
							validPINList.add(acuObj.getSerialNumber().substring(10, 17));
						}
					}
					if (result[1] != null) {
						validMachineNumberList.add((String) result[1]);
					}
				}
			}

//			add valid PINs to valid PIN list from the input serial number list
			if (serNumberList.size() > 0) {
				numberString = conversionObj.getStringList(serNumberList).toString();
				query = "SELECT ae.serial_number FROM AssetEntity ae WHERE ae.serial_number IN ("+ numberString + ") and ae.active_status='1'";
				iterator = session.createQuery(query).list().iterator();
				
				while (iterator.hasNext()) {
					acuObj = (AssetControlUnitEntity) iterator.next();
					if (acuObj != null) {
//						if (!validPINList.contains(acuObj.getSerialNumber())) {
							validPINList.add(acuObj.getSerialNumber());
//						}
					}
				}
			}
			
			//DF20161222 @Roopa changing asset_monitoring_snapshot_new to asset_monitoring_snapshot which is having txndata as json column
			
//			get the parameters for each PIN and get the FW version from it
			FotaUpgradedHistoryDetailsImpl implObj = null;
			if (validPINList.size() > 0) {
				HashMap<String,String> txnDataMap=new HashMap<String, String>();
				String fwVersion=null;
				DynamicAMS_Doc_DAL dalObj = new DynamicAMS_Doc_DAL();
				
				String vin = null;
				List<AMSDoc_DAO> amsList = null;
				iterator = validPINList.iterator();
				while (iterator.hasNext()) {
					vin = (String) iterator.next();
				
					/*query = "SELECT ams.serialNumber ,ams.parameters FROM AssetMonitoringSnapshotEntity ams WHERE ams.serialNumber like '%"+vin+"'";
					Iterator iterator2 = session.createQuery(query).list().iterator();*/
					amsList = dalObj.getAMSData("FotaUpgradedHistoryDetailsService::getFotaUpgradedHistoryDetails", vin);
					AMSDoc_DAO amsObj = null;
					Iterator<AMSDoc_DAO> iterator2 = amsList.iterator();
					while(iterator2.hasNext()){
						amsObj = iterator2.next();
						txnDataMap = amsObj.getTxnData();
						if (txnDataMap != null) {
							fwVersion=txnDataMap.get("FW_VER");
							if (fwVersion != null) {
								implObj = new FotaUpgradedHistoryDetailsImpl();
								if(serNumberList1.contains(vin))
								{
									implObj.setSerialNumber("VIN"+vin);
								}
								else
								implObj.setSerialNumber(vin);

								implObj.setFwVersion(fwVersion);
							}
							else{
								implObj = new FotaUpgradedHistoryDetailsImpl();
								if(serNumberList1.contains(vin))
								{
									implObj.setSerialNumber("VIN"+vin);
								}
								else
								implObj.setSerialNumber(vin);
								
								implObj.setFwVersion("NA");
							}
							implList.add(implObj);
						}
					}
				}

			}

//			add invalid PINs to the output list
			iterator = serialNumberList.iterator();
			while (iterator.hasNext()) {
				serialNumber = (String) iterator.next();
				if(serialNumber.length()==20)
				{
					if (!validPINList.contains(serialNumber.substring(3,20)) && !validMachineNumberList.contains(serialNumber.substring(3,20))) {
						implObj = new FotaUpgradedHistoryDetailsImpl();
						if(serNumberList1.contains(serialNumber))
						{
							implObj.setSerialNumber("VIN"+serialNumber + " : INVALID PIN");
						}
						else
						implObj.setSerialNumber(serialNumber + " : INVALID PIN");
						
						implObj.setFwVersion("NO VERSION");
						implList.add(implObj);
					}
				}
				else{	
				if (!validPINList.contains(serialNumber) && !validMachineNumberList.contains(serialNumber)) {
					implObj = new FotaUpgradedHistoryDetailsImpl();
					if(serNumberList1.contains(serialNumber))
					{
						implObj.setSerialNumber("VIN"+serialNumber + " : INVALID PIN");
					}
					else
					implObj.setSerialNumber(serialNumber + " : INVALID PIN");
					
					implObj.setFwVersion("NO VERSION");
					implList.add(implObj);
				}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return implList;

	}
}
