package remote.wise.businessobject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

////import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.ClientEntity;
import remote.wise.businessentity.RoleEntity;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.implementation.RoleImpl;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class RoleBO extends BaseBusinessObject
{
	//DefectId:1337 - Rajani Nagaraju - 20130923 - Log4j Changes - Using static logger object all throughout the application
	/*public static WiseLogger businessError = WiseLogger.getLogger("RoleBO:","businessError");*/
	
	RoleEntity roleObj;

	/**
	 * @return the roleObj
	 */
	public RoleEntity getRoleObj() {
		return roleObj;
	}

	/**
	 * @param roleObj the roleObj to set
	 */
	public void setRoleObj(RoleEntity roleObj) {
		this.roleObj = roleObj;
	}

	public RoleBO getRoleObj(int role_id)
	{
		roleObj=new RoleEntity(role_id);
		return this;
	}

	/**
	 * method to get roles list
	 * @return list of roles
	 */
	public List<RoleImpl> getRoles(){
		//Logger businessError = Logger.getLogger("businessErrorLogger");
		
    	Logger bLogger = BusinessErrorLoggerClass.logger;
		roleObj=null;
		RoleImpl impl=null;
		RoleEntity entity =null;
		List<RoleImpl> roleList = new ArrayList<RoleImpl>();
		Session s1 = HibernateUtil.getSessionFactory().getCurrentSession();
		s1.beginTransaction();		
		try {
			//get Client Details
			Properties prop = new Properties();
			String clientName=null;

			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			clientName= prop.getProperty("ClientName");

			IndustryBO industryBoObj = new IndustryBO();
			ClientEntity clientEntity = industryBoObj.getClientEntity(clientName);
			//END of get Client Details	
          if(! (s1.isOpen() ))
                  {
                              s1 = HibernateUtil.getSessionFactory().getCurrentSession();
                              s1.getTransaction().begin();
                  }

			String query="from RoleEntity where client_id="+clientEntity.getClient_id();
			Query query2 = s1.createQuery(query);
			List list1 = query2.list();
			Iterator iter = list1.iterator();

			while(iter.hasNext()){
				impl= new RoleImpl();		    	
				entity = (RoleEntity)iter.next();
				impl.setRoleId(entity.getRole_id());
				impl.setRoleName(entity.getRole_name());
				roleList.add(impl);
			}
		}
		catch(Exception e)
		{
			bLogger.error("Couldn't get the role");
		}

		finally
		{
			if(s1.getTransaction().isActive())
				s1.getTransaction().commit();
			if(s1.isOpen())
			{
				s1.flush();
				s1.close();
			}
		}
		return roleList;
	}		
}
