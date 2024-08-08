package remote.wise.businessobject;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class ActiveMachineListReportBO {

	/*public static WiseLogger businessError = WiseLogger.getLogger("ActiveMachineListReportBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("ActiveMachineListReportBO:","fatalError");*/
	String SerialNumber;
	String RollOffDate;
	String Profile;
	String Model;
	String InstallDate;
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
	
	public List<ActiveMachineListReportBO> getactiveMachineList(String toDate) 
	{
		Logger fLogger = FatalLoggerClass.logger;
		// TODO Auto-generated method stub
		List<ActiveMachineListReportBO> ActiveMachineListBO = new LinkedList<ActiveMachineListReportBO>();
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		    session.beginTransaction();
		    try
		    {
		    	if(! (session.isOpen() ))
                {
                            session = HibernateUtil.getSessionFactory().getCurrentSession();
                            session.getTransaction().begin();
                } 
		    	Query query = session.createQuery("select a.serial_number,a.install_date,a.dateTime,b.assetGroupName,b.assetTypeName from AssetEntity a,AssetClassDimensionEntity b where a.dateTime<='"+toDate+"' and a.productId=b.productId and a.active_status=1 ");
				Iterator itr = query.list().iterator();
				Object[] result = null;
				while(itr.hasNext())
				{
					ActiveMachineListReportBO activeMachineBO=new ActiveMachineListReportBO();
					result = (Object[]) itr.next();
					AssetControlUnitEntity a=(AssetControlUnitEntity)result[0];
					String serialnumber=a.getSerialNumber();
					activeMachineBO.setSerialNumber(serialnumber);
					activeMachineBO.setInstallDate(String.valueOf((Timestamp)result[1]));
					activeMachineBO.setRollOffDate(String.valueOf((Timestamp)result[2]));
					activeMachineBO.setProfile((String)result[3]);
					activeMachineBO.setModel((String)result[4]);
					ActiveMachineListBO.add(activeMachineBO);
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
		return ActiveMachineListBO;
	}

}
