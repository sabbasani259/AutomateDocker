/**
 * 
 */
package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetCustomGroupMapping;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessobject.OAssetGroupUsersBO;

import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.service.datacontract.AssetGroupUsersRespContract;
import remote.wise.util.HibernateUtil;

/**
 * @author roopn5
 *
 */
public class OAssetGroupUsersImpl {
	
	//DF20160115 @Roopa for Set AssetGroupUers Details into OrientDB
	
	public String setOAssetGroupUsers(AssetGroupUsersRespContract reqObject) throws CustomFault
	{
		
		String status = "SUCCESS";

	               List<String> serialNumberList=new ArrayList<String>();
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
    	Logger bLogger = BusinessErrorLoggerClass.logger;
    	
    	Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
    	
    	try{
		
		
		Query q = session.createQuery("select a.serial_number from AssetCustomGroupMapping a where a.group_id="+reqObject.getGroupId()+"");
		Iterator it = q.list().iterator();
		while(it.hasNext())
		{
			AssetEntity serialNumber = (AssetEntity)it.next();
			serialNumberList.add(String.valueOf(serialNumber.getSerial_number().getSerialNumber()));
		}
		
		OAssetGroupUsersBO setAssetGroupUsers = new OAssetGroupUsersBO();

		//DF20171211: KO369761 - Removing OrienDB from the Production Completely.
		//status = setAssetGroupUsers.setOAssetGroupUsers(reqObject.getGroupId(),reqObject.getContactId(),serialNumberList);
		
    	}
    	catch (Exception e) {
			e.printStackTrace();
			status = "FAILURE";
			fLogger.fatal("Exception :" + e);
		}

		finally {

			if (session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}
		
		return status;
		
	}

}
