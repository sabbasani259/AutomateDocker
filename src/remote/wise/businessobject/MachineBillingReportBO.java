package remote.wise.businessobject;

import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.BillingReportDetailEntity;
import remote.wise.businessentity.BillingReportEntity;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.DateUtil;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class MachineBillingReportBO {

	/*public static WiseLogger businessError = WiseLogger.getLogger("MachineBillingReportBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("MachineBillingReportBO:","fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger("LoginRegistrationBO:","info");*/
	
	String SerialNumber;
	String OldSerialNumber;
	String RollOffDate;
	String Profile;
	String Model;
	String oldNew;
	Long billingCalculation;
	Long ActualMachineCount;
	Long NewRolledMachine;
	Long InvoicedAmount ;
	String InstallDate;
	Long previousBilledCount;
	
	/**
	 * @return the previousBilledCount
	 */
	public Long getPreviousBilledCount() {
		return previousBilledCount;
	}

	/**
	 * @param previousBilledCount the previousBilledCount to set
	 */
	public void setPreviousBilledCount(Long previousBilledCount) {
		this.previousBilledCount = previousBilledCount;
	}

	/**
	 * @return the actualMachineCount
	 */
	public Long getActualMachineCount() {
		return ActualMachineCount;
	}

	/**
	 * @param actualMachineCount the actualMachineCount to set
	 */
	public void setActualMachineCount(Long actualMachineCount) {
		ActualMachineCount = actualMachineCount;
	}

	/**
	 * @return the billingCalculation
	 */
	public Long getBillingCalculation() {
		return billingCalculation;
	}

	/**
	 * @param billingCalculation the billingCalculation to set
	 */
	public void setBillingCalculation(Long billingCalculation) {
		this.billingCalculation = billingCalculation;
	}

	/**
	 * @return the oldNew
	 */
	public String getOldNew() {
		return oldNew;
	}

	/**
	 * @param oldNew the oldNew to set
	 */
	public void setOldNew(String oldNew) {
		this.oldNew = oldNew;
	}

	/**
	 * @return the oldSerialNumber
	 */
	public String getOldSerialNumber() {
		return OldSerialNumber;
	}

	/**
	 * @param oldSerialNumber the oldSerialNumber to set
	 */
	public void setOldSerialNumber(String oldSerialNumber) {
		OldSerialNumber = oldSerialNumber;
	}

	/**
	 * @return the newRolledMachine
	 */
	public Long getNewRolledMachine() {
		return NewRolledMachine;
	}

	/**
	 * @param newRolledMachine the newRolledMachine to set
	 */
	public void setNewRolledMachine(Long newRolledMachine) {
		NewRolledMachine = newRolledMachine;
	}


	public String getSerialNumber() {
		return SerialNumber;
	}

	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}

	/**
	 * @return the rollOffDate
	 */
	public String getRollOffDate() {
		return RollOffDate;
	}

	/**
	 * @param rollOffDate the rollOffDate to set
	 */
	public void setRollOffDate(String rollOffDate) {
		RollOffDate = rollOffDate;
	}

	/**
	 * @return the profile
	 */
	public String getProfile() {
		return Profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(String profile) {
		Profile = profile;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return Model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		Model = model;
	}

	/**
	 * @return the installDate
	 */
	public String getInstallDate() {
		return InstallDate;
	}

	/**
	 * @param installDate the installDate to set
	 */
	public void setInstallDate(String installDate) {
		InstallDate = installDate;
	}
	/**
	 * @return the invoicedAmount
	 */
	public Long getInvoicedAmount() {
		return InvoicedAmount;
	}

	/**
	 * @param invoicedAmount the invoicedAmount to set
	 */
	public void setInvoicedAmount(Long invoicedAmount) {
		InvoicedAmount = invoicedAmount;
	}

	public List<MachineBillingReportBO> machineBillingReportList(
			String fromDate, String toDate) {
		
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
		List<MachineBillingReportBO> MachineBillingListBO = new LinkedList<MachineBillingReportBO>();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try
		{
			Properties prop = new Properties();
			prop.load(getClass()
					.getClassLoader()
					.getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
			String cost_per_machine_per_month = null;
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			
			//DF20140404 - Rajani Nagaraju - To get the RolledOff Data from BillingReport Detail Report
			/*Query query = session.createQuery("select a.serial_number,a.install_date,a.dateTime,b.assetGroupName,b.assetTypeName " +
					"from AssetEntity a,AssetClassDimensionEntity b where a.dateTime>='"+fromDate+"' and a.dateTime<='"+toDate+"' " +
							" and a.productId=b.productId and a.active_status=1 ");*/
			Query query = session.createQuery("select a.serial_number,a.install_date,c.rolledOffDate,b.assetGroupName,b.assetTypeName " +
							" from AssetEntity a,AssetClassDimensionEntity b, BillingReportDetailEntity c " +
							" where c.rolledOffDate>='"+fromDate+"' and c.rolledOffDate<='"+toDate+"' " +
							" and a.productId=b.productId " +
							" and a.serial_number=c.serial_number " +
							" and a.active_status=1 ");
			
			Iterator itr = query.list().iterator();
			Object[] result = null;
			List<String> serialNumberList = new LinkedList<String>();
			while(itr.hasNext())
			{
				MachineBillingReportBO machinelistBO=new MachineBillingReportBO();
				result = (Object[]) itr.next();
				AssetControlUnitEntity a=(AssetControlUnitEntity)result[0];
				String serialnumber=a.getSerialNumber();
				machinelistBO.setSerialNumber(serialnumber);
				machinelistBO.setInstallDate(String.valueOf((Timestamp)result[1]));
				machinelistBO.setRollOffDate(String.valueOf((Timestamp)result[2]));
				machinelistBO.setProfile((String)result[3]);
				machinelistBO.setModel((String)result[4]);
				machinelistBO.setOldNew("current");
				MachineBillingListBO.add(machinelistBO);
				serialNumberList.add(serialnumber);
			}
			
			iLogger.info("size of list "+serialNumberList.size());
			Query query1 = session.createQuery("select a.serial_number,a.install_date,c.rolledOffDate,b.assetGroupName,b.assetTypeName " +
							" from AssetEntity a,AssetClassDimensionEntity b, BillingReportDetailEntity c" +
							" where c.rolledOffDate<='"+fromDate+"' " +
							" and a.productId=b.productId  " +
							" and a.serial_number=c.serial_number " +
							" and a.active_status=1 ");
			Iterator itr1 = query1.list().iterator();
			Object[] result1 = null;
			while(itr1.hasNext())
			{
				MachineBillingReportBO machinelistBO2=new MachineBillingReportBO();
				result1 = (Object[]) itr1.next();
				AssetControlUnitEntity a=(AssetControlUnitEntity)result1[0];
				String serialnumber=a.getSerialNumber();
				machinelistBO2.setSerialNumber(serialnumber);
				machinelistBO2.setInstallDate(String.valueOf((Timestamp)result1[1]));
				machinelistBO2.setRollOffDate(String.valueOf((Timestamp)result1[2]));
				machinelistBO2.setProfile((String)result1[3]);
				machinelistBO2.setModel((String)result1[4]);
				machinelistBO2.setOldNew("old");
				MachineBillingListBO.add(machinelistBO2);
				serialNumberList.add(serialnumber);
			}
			MachineBillingReportBO machinelistBO1=new MachineBillingReportBO();
			machinelistBO1.setSerialNumber("Summary");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(fromDate);
			DateUtil dateUtilObj = new DateUtil();
			dateUtilObj  = dateUtilObj. getCurrentDateUtility(date);
			int month = dateUtilObj. getMonth ();
			int year = dateUtilObj. getYear();
			long BillingCal=0L;
			int billingId=0;
			
			Query query2 = session.createQuery("select actual_machine_count,commulative_Actual_machine_count,billing_cal,billing_id from  BillingReportEntity where year='"+year+"' and month='"+month+"'");
			Iterator itr2 = query2.list().iterator();
			Object[] result2 = null;
			while(itr2.hasNext())
			{
				result2 = (Object[]) itr2.next();
				if(result2[0]!=null)
				{
					machinelistBO1.setNewRolledMachine((Long)result2[0]);
				}
				if(result2[1]!=null)
				{
						machinelistBO1.setActualMachineCount((Long)result2[1]);
				}
				if(result2[2]!=null)
				{
					BillingCal = (Long)result2[2];
					machinelistBO1.setBillingCalculation((Long)result2[2]);
				}
				if(result2[3]!=null)
				{
					billingId = (Integer)result2[3];
				}
			}
			cost_per_machine_per_month=prop.getProperty("Cost_Per_Machine_Per_Month");
			long costPerMachinePerMonth = Long.parseLong(cost_per_machine_per_month);
			machinelistBO1.setInvoicedAmount(costPerMachinePerMonth * BillingCal);
			
			
			billingId=billingId-1;
			long PrevBillingCal=0L;
			//DefectID:20140404 - Rajani Nagaraju - Modifying the below query to return the data correctly
			Query prevBillingQ = session.createQuery(" select a from BillingReportEntity a " +
					" where (a.year='"+year+"' AND a.month < '"+month+"')"+" OR (a.year < '"+year+"')" +
					" order by a.year desc, a.month desc");
			prevBillingQ.setMaxResults(1);
			Iterator prevBillingItr = prevBillingQ.list().iterator();
			while(prevBillingItr.hasNext())
			{
				BillingReportEntity prevBilling = (BillingReportEntity) prevBillingItr.next();
				PrevBillingCal = prevBilling.getBilling_cal();
				machinelistBO1.setPreviousBilledCount(PrevBillingCal);
			}
			
			/*Query query3 = session.createQuery("select billing_cal from BillingReportEntity where billing_id='"+billingId+"' ");
			Iterator itr3 = query3.list().iterator();
			while(itr3.hasNext())
			{
				PrevBillingCal = (Long)itr3.next();
				machinelistBO1.setPreviousBilledCount(PrevBillingCal);
			}*/
			MachineBillingListBO.add(machinelistBO1);
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
		return MachineBillingListBO;
	}

	
	//DF20140403 - Rajani Nagaraju - Adding the capability to update billing data for previous months
	public String setBillingCalculation(String month, String year) 
	{
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
    	Logger fLogger = FatalLoggerClass.logger;
		try
		{
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			String minOrderQty = prop.getProperty("MIN_Order_Qty");
			Long minOrderQtyLong = Long.parseLong(minOrderQty);
			
			//Check whether it is a new Insertion / Update
			Query chkUpdateQ = session.createQuery(" from BillingReportEntity where month="+month+" and year="+year);
			Iterator chkupdateItr = chkUpdateQ.list().iterator();
			int update=0;
			BillingReportEntity updateBillingRepObj=null;
			while(chkupdateItr.hasNext())
			{
				updateBillingRepObj = (BillingReportEntity)chkupdateItr.next();
				update=1;
			}
			
			//Get the Last Record details in BillingReport Entity
			long prevCummMachineCount=0L;
			long prevBillCal=0L;
			Query prevBillingQ = session.createQuery(" select a from BillingReportEntity a " +
					" where (a.year='"+year+"' AND a.month < '"+month+"')"+" OR (a.year < '"+year+"')" +
					" order by a.year desc, a.month desc");
			prevBillingQ.setMaxResults(1);
			Iterator prevBillingItr = prevBillingQ.list().iterator();
			while(prevBillingItr.hasNext())
			{
				BillingReportEntity prevBilling = (BillingReportEntity) prevBillingItr.next();
				prevCummMachineCount = prevBilling.getCommulative_Actual_machine_count();
				prevBillCal = prevBilling.getBilling_cal();
			}
			
			
			if(update==1)
			{
				//Delete the record from BillingReportDetail, Update BillingReport and Insert the vins newly into BillingReportDetail
				Query deleteDetailQuery = session.createQuery(" delete from BillingReportDetailEntity where billing_id='"+updateBillingRepObj.getBilling_id()+"'");
				int row1= deleteDetailQuery.executeUpdate();
			}
			
			
			//Get the List of VINs that are Rolled Off in the previous Month
			List<AssetEntity> rolledOffVinList = new LinkedList<AssetEntity>();
			Query query = session.createQuery("select a from AssetEntity a where dateTime like '"+year+"-"+month+"%'" +
												" and active_status=1 and a.serial_number not in " +
	 											" ( select b.serial_number from BillingReportDetailEntity b )");
			Iterator itr = query.list().iterator();
			while(itr.hasNext())
			{
				AssetEntity asset = (AssetEntity) itr.next();
				rolledOffVinList.add(asset);
			}
			
			long rolledOffMachineCount = rolledOffVinList.size();
			
			
			BillingReportEntity billingReportEntity =null;
			if(update==0)
				billingReportEntity = new BillingReportEntity();
			else
				billingReportEntity = updateBillingRepObj;
				//billingReportEntity.setBilling_id(updateBillingRepObj.getBilling_id());
			
			//System.out.println("rolledOffMachineCount:"+rolledOffMachineCount);
			//System.out.println("prevCummMachineCount:"+prevCummMachineCount);
			
			billingReportEntity.setActual_machine_count(rolledOffMachineCount);
			billingReportEntity.setCommulative_Actual_machine_count(rolledOffMachineCount+prevCummMachineCount);
			if(rolledOffMachineCount>minOrderQtyLong)
				billingReportEntity.setBilling_cal(rolledOffMachineCount+prevBillCal);
			else
				billingReportEntity.setBilling_cal(minOrderQtyLong+prevBillCal);
			billingReportEntity.setMonth(Integer.parseInt(month));
			billingReportEntity.setYear(Integer.parseInt(year));
			
			if(update==0)
				session.save(billingReportEntity);
			else
				session.update(billingReportEntity);
			
			
			//Insert the record into BillingReportDetailEntity
			for(int i=0; i<rolledOffVinList.size(); i++)
			{
				BillingReportDetailEntity billingDetailobj = new BillingReportDetailEntity();
				billingDetailobj.setBilling_id(billingReportEntity);
				billingDetailobj.setSerial_number(rolledOffVinList.get(i));
				billingDetailobj.setRolledOffDate(rolledOffVinList.get(i).getDateTime());
				session.save(billingDetailobj);
			}
			
		}	
		
		catch(Exception e)
		{
			e.printStackTrace();
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
		
		return "SUCCESS";
	}

}
