package remote.wise.service.webservice;

import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

/*
 * ME100008333 : 20230704 : Dhiraj K : Test method added for Hibernate connection
 */

@Path("/TestServiceV3")
public class TestServiceV3 {

	@SuppressWarnings("rawtypes")
	@GET
	@Path("/testMethod")
	@Produces(MediaType.TEXT_PLAIN)
	public String testMethod() {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		String selectQuery = "SELECT industry_name from IndustryEntity WHERE industry_id=1";
		String industryName = null;
		String status="FAILURE";
		Query query = session.createQuery(selectQuery);
		try {
			Iterator iterator = query.list().iterator();

			if (iterator.hasNext()) {
				industryName = (String) iterator.next();
			}
			
			if (industryName != null) {
				iLogger.info("industryName:"+industryName);
				System.out.println("industryName:"+industryName);
                status = "SUCCESS";
            }

		} catch (Exception e) {
			fLogger.fatal("Exception :" + e);
		} finally {
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
