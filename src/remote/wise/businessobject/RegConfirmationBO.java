package remote.wise.businessobject;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetGroupEntity;
import remote.wise.businessentity.CustomAssetGroupEntity;
import remote.wise.businessentity.RegConfirmationEntity;
import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class RegConfirmationBO {

	/*public static WiseLogger businessError = WiseLogger.getLogger("AssetCustomGroupDetailsBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("AssetCustomGroupDetailsBO:","fatalError");*/

	String IMEI_number;
	String phone_number;
	String user_ID;
	/**
	 * @return the user_ID
	 */
	public String getUser_ID() {
		return user_ID;
	}

	/**
	 * @param user_ID the user_ID to set
	 */
	public void setUser_ID(String user_ID) {
		this.user_ID = user_ID;
	}

	/**
	 * @return the iMEI_number
	 */
	public String getIMEI_number() {
		return IMEI_number;
	}

	/**
	 * @param iMEI_number the iMEI_number to set
	 */
	public void setIMEI_number(String iMEI_number) {
		IMEI_number = iMEI_number;
	}

	/**
	 * @return the phone_number
	 */
	public String getPhone_number() {
		return phone_number;
	}

	/**
	 * @param phone_number the phone_number to set
	 */
	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}


	public String deleteConfirmation(String imeiNumber, boolean flag) throws CustomFault {
		// TODO Auto-generated method stub
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		String status = "SUCCESS";

		//Logger businessError = Logger.getLogger("businessErrorLogger");
		//Logger fatalError = Logger.getLogger("fatalErrorLogger");
		//validate the IMEI Number 


		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();

		try
		{
			if(imeiNumber==null ||imeiNumber.isEmpty())
			{
				throw new CustomFault("Invalid imeiNumber");
			}
			Query deleteDetailQuery = session.createQuery("delete from RegConfirmationEntity where IMEI_number="+imeiNumber);
			int row1= deleteDetailQuery.executeUpdate();
		}

		catch(CustomFault e)
		{
			status = "FAILURE";
			bLogger.error("Custom Fault: "+ e.getFaultInfo());
		}
		catch(Exception e)
		{
			status = "FAILURE";
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

		return status;

	}

	public String setConfirmation(String imeiNumber, String phoneNumber, String userID) throws CustomFault {
		// TODO Auto-generated method stub
		
    	
    	
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		String status = "SUCCESS";
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			if(imeiNumber==null ||imeiNumber.isEmpty())
			{
				throw new CustomFault("Invalid imeiNumber");
			}
			if(phoneNumber==null ||phoneNumber.isEmpty())
			{
				throw new CustomFault("Invalid PhoneNumber");
			}
				RegConfirmationEntity regConfirmationEntity= new RegConfirmationEntity();
				regConfirmationEntity.setIMEI_number(imeiNumber);
				regConfirmationEntity.setPhone_number(phoneNumber);
				regConfirmationEntity.setUser_ID(userID);
				session.save("RegConfirmationEntity",regConfirmationEntity);
		}
		catch(CustomFault e)
		{
			status = "FAILURE";
			bLogger.error("Custom Fault:"+e.getFaultInfo());
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

		return status;
	}

	public RegConfirmationBO getConfirmation(String imeiNumber) {
		// TODO Auto-generated method stub
		//Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		RegConfirmationBO regConfirmationBO = new RegConfirmationBO();
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		try
		{
			if(! (session.isOpen() ))
			{
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.getTransaction().begin();
			} 
			Query query = session.createQuery("select IMEI_number,phone_number,user_ID from RegConfirmationEntity where IMEI_number='"+imeiNumber+"'");
			Iterator itr = query.list().iterator();
			Object[] result = null;
			while(itr.hasNext())
			{
				result = (Object[]) itr.next();
				regConfirmationBO.setIMEI_number((String)result[0]);
				regConfirmationBO.setPhone_number((String)result[1]);
				regConfirmationBO.setUser_ID((String)result[2]);
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
		return regConfirmationBO;

	}

}
