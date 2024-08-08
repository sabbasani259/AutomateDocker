package remote.wise.service.implementation;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.HibernateUtil;
import org.hibernate.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class RegionServiceImpl {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getRegionServiceDetails(int parentTenancyID) {

		String result = null;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String regionQ = "select a.tenancy_id, a.tenancy_name from TenancyEntity a where Parent_Tenancy_ID=" +parentTenancyID+" ";
		Query query = session.createQuery(regionQ);

		try{
			Iterator iterator = query.list().iterator();
			Object[] resultQ = null;
			HashMap<Integer,String> regionMap = new HashMap<Integer,String>();
			JSONObject jsonObj = null;
			JSONArray jsonArray = new JSONArray();
			jsonObj = new JSONObject();
			while(iterator.hasNext()){
				resultQ = (Object[]) iterator.next();
				int tenancyId = (Integer) resultQ[0];
				String TenancyName =  (String) resultQ[1];
				regionMap.put(tenancyId, TenancyName);
			}
			jsonObj.putAll(regionMap);
			jsonArray.add(jsonObj);
			result = jsonArray.toString();

		}catch(Exception e){
			fLogger.fatal("Exception :"+e);
		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return result;
	}
}
