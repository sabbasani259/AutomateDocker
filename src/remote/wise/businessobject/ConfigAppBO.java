package remote.wise.businessobject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import remote.wise.businessentity.ConfigAppEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;
/**
 * BO class to set and get the
 * serviceName and status for the specified configurationId
 * 
 * @author Smitha
 * 
 */
public class ConfigAppBO extends BaseBusinessObject {
	
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("ConfigAppBO:","businessError");*/
	
	
	private int configuration_id;
	 private String services;
	 private boolean isStatus;
	 private String modifiedBy;
	 private String modifiedOn;
	 
	public int getConfiguration_id() {
		return configuration_id;
	}
	public void setConfiguration_id(int configuration_id) {
		this.configuration_id = configuration_id;
	}
	public String getServices() {
		return services;
	}
	public void setServices(String services) {
		this.services = services;
	}
	public boolean isStatus() {
		return isStatus;
	}
	public void setStatus(boolean isStatus) {
		this.isStatus = isStatus;
	}	
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getModifiedOn() {
		return modifiedOn;
	}
	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	// getMethod for configure Application..
	/**
	 * This method gets
	 * serviceName and status for the specified configurationId	
	 * @param reqObj
	 *            Get the details of
	 *            serviceName and status by passing
	 *            the same to this request Object
	 * @return Returns respObj for the specified configurationId	
	 * @throws CustomFault
	 */
	 
	@SuppressWarnings("rawtypes")
	public List<ConfigAppBO> getConfigAppSettings(
			int configuration_id) throws CustomFault {

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		List<ConfigAppBO> ConfigAppList = new LinkedList<ConfigAppBO>();		

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		try {
			String queryString = null;
			if (configuration_id != 0){
				queryString = " from ConfigAppEntity where configuration_id = "+configuration_id;
			}  else if(configuration_id == 0){
				queryString=" from ConfigAppEntity ";
			}

			Iterator itr=session.createQuery(queryString).list().iterator();		
			while(itr.hasNext())
			{
				ConfigAppEntity configEntity=(ConfigAppEntity)itr.next();			
				ConfigAppBO configDetails=new ConfigAppBO();
				//DefectID:20140107-----Smitha-----date format changed.
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
				configDetails.setConfiguration_id(configEntity.getConfiguration_id());			
				configDetails.setServices(configEntity.getServices());
				configDetails.setStatus(configEntity.isStatus());
				configDetails.setModifiedBy(configEntity.getModifiedBy());
				//added by smitha on Nov 4th 2013.....DefectID: 20131104
				Date modifiedDate=configEntity.getModifiedOn();
				String finalDateModified=dateFormat.format(modifiedDate);
				configDetails.setModifiedOn(finalDateModified);
				//ended on Nov 4th 2013.....DefectID: 20131104
				ConfigAppList.add(configDetails);
			}	   
		} catch (Exception e) {
			bLogger.error("Hello this is an Business Error message" + e.getMessage());
		} finally {
			if (session.getTransaction().isActive())
				session.getTransaction().commit();
			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}
		return ConfigAppList;

	}
	
	// setMethod for configure Application..
	/**
	 * This method sets the
	 * serviceName and status for the specified configurationId	
	 * @param reqObj
	 *            Gets the serviceName and status
	 * @return Returns respObj for the specified
	 *         configurationId
	 * @throws CustomFault
	 */
	@SuppressWarnings("rawtypes")
	public String setConfigAppSettings( int configuration_id,String services,boolean isStatus,String modifiedBy) throws CustomFault {
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger bLogger = BusinessErrorLoggerClass.logger;
		
		Session session1 = HibernateUtil.getSessionFactory()
				.getCurrentSession();
		session1.beginTransaction();
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			   //get current date time with Date()
			   Date date = new Date();
			   String currentDate=dateFormat.format(date);
			   Timestamp currDate;

			String queryString = "from ConfigAppEntity where configuration_id= "
					+ configuration_id;
			List resultList = session1.createQuery(queryString).list();
			if(resultList!=null){
			}
			Iterator itr = resultList.iterator();
			int flag = 0;ConfigAppEntity configEntity =null;
			while (itr.hasNext()) {
				flag = 1;
				currDate=Timestamp.valueOf(currentDate);				
				configEntity = (ConfigAppEntity) itr.next();				
				//configEntity.setServices(services);
				boolean staus = false;
				staus=configEntity.isStatus();
				if(staus!=isStatus)
				{
					configEntity.setStatus(isStatus);
					configEntity.setModifiedBy(modifiedBy);				
					configEntity.setModifiedOn(currDate);
					configEntity.setConfiguration_id(configuration_id);
					session1.update(configEntity);	
				}
				
			}
			if (flag == 0) {
				if (!(session1.isOpen()))

				{

					session1 = HibernateUtil.getSessionFactory().openSession();
					session1.getTransaction().begin();

				}
				 configEntity = new ConfigAppEntity();				
				configEntity.setServices(services);
				configEntity.setStatus(isStatus);
				configEntity.setModifiedBy(modifiedBy);
				currDate=Timestamp.valueOf(currentDate);
				configEntity.setModifiedOn(currDate);
				configEntity.setConfiguration_id(configuration_id);

				configEntity.save();
			}
		} catch (Exception e) {
			bLogger.error("Hello this is an Business Error message" + e.getMessage());
		} finally {
		    if(session1.getTransaction().isActive())
            {
                  session1.getTransaction().commit();
            }

			if (session1.isOpen()) {			
				session1.flush();
				session1.close();
			}
		}
		return "SUCCESS";
	}
}

