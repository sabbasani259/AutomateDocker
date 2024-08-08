package remote.wise.service.implementation;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.HibernateUtil;

public class ECCDealersRESTServiceImpl {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getECCDealerDetails() {

		String result = null;
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String eccDealersQ = ("select eccAccCode, eccAccName from AccountMapping where llAccCode in (select accountCode from AccountEntity) group by eccAccName");
		Query query = session.createQuery(eccDealersQ);
		try{
			Iterator iterator = query.list().iterator();
			Object[] resultQ = null;
			HashMap<String,String> eccDealerMap = new HashMap<String,String>();
			JSONObject jsonObj = null;
			JSONArray jsonArray = new JSONArray();
			jsonObj = new JSONObject();
			while(iterator.hasNext()){
				resultQ = (Object[]) iterator.next();
				String eccCode = (String) resultQ[0];
				String accName = (String) resultQ[1];
				eccDealerMap.put(eccCode, accName);
			}
			jsonObj.putAll(eccDealerMap);
			jsonArray.add(jsonObj);
			result = jsonArray.toString();
		}catch(Exception e){
			fLogger.fatal("Exception in ECCDealersRESTServiceImpl:"+e);
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