package remote.wise.service.webservice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountContactMapping;
import remote.wise.businessentity.AccountTenancyMapping;
import remote.wise.businessobject.MapBO2;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.implementation.MapImpl;
import remote.wise.util.HibernateUtil;


@Path("mapCache")
public class MapOverviewCacheRESTService {

	public static HashMap<Integer, HashMap<String, List<MapImpl>>> mapCache = null;
	
	@GET
	@Path("updateMapCache")
	@Produces(MediaType.TEXT_HTML)
	public static String getMapOverviewCache(){

		Logger iLogger = InfoLoggerClass.logger;
		List<MapImpl> response = null;

		iLogger.info("MapOverviewCacheRESTService:WebService Invoked");
		long startTime = System.currentTimeMillis();

		
		//HashMap<String, List<MapImpl>> innerHashMap = new HashMap<String, List<MapImpl>>();
		mapCache = new HashMap<Integer,HashMap<String, List<MapImpl>>>();
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Integer> tenancyIdList =null;
		String loginID = null;
		int[] accountList = {1001,1200,1201,1202,1203,1204,1205,1206,1207,2001,2002,2003,2004,2005,2006,2007,2008,2009,50000};
		List<Integer> loginUserTenancyList = null;
		HashMap<String, List<MapImpl>> innerHashMap = null;

		MapBO2 boObj = new MapBO2();
		for(int account:accountList){
			tenancyIdList = new LinkedList<Integer>();
			loginUserTenancyList = new LinkedList<Integer>();
			Query accountQ = session.createQuery(" from AccountTenancyMapping where account_id = "+account);
	    	  Iterator accountItr = accountQ.list().iterator();
	    	  if (accountItr.hasNext()) {
	    		  AccountTenancyMapping accTen = (AccountTenancyMapping) accountItr.next();
	    		  tenancyIdList.add(accTen.getTenancy_id().getTenancy_id());
	    		  loginUserTenancyList.add(accTen.getTenancy_id().getTenancy_id());
	    	  }
	    	  Query contactQ = session.createQuery(" from AccountContactMapping where account_id = "+account);
	    	  Iterator contactItr = contactQ.list().iterator();
	    	  if (contactItr.hasNext()) {
	    		  AccountContactMapping contact = (AccountContactMapping) contactItr.next();
	    		  //contact.getContact_id().getContact_id() should not present in group user table.
	    		  loginID = contact.getContact_id().getContact_id();
	    	  }
	    	  innerHashMap = new HashMap<String, List<MapImpl>>();
	    	  if(!(loginID == null || tenancyIdList==null || tenancyIdList.size() == 0 || loginUserTenancyList == null || loginUserTenancyList.size() == 0 )){
	    		  for(int i =0;i<=10;i++){
	    			  
	    			//Df20171218 @Roopa including country code filter in the map service
	    			  response = boObj.getOverviewMapDetails(loginID, null, null, tenancyIdList, false, loginUserTenancyList, null, null, i,null,null);
	    			  innerHashMap.put("call"+i,response);
	    		  }
	    	  }		mapCache.put(account, innerHashMap);
		}
		
		long endTime=System.currentTimeMillis();
		//System.out.println(mapCache);
		
		iLogger.info("serviceName:MapOverviewCacheRESTService~executionTime:"+(endTime-startTime)+"~"+loginID+"~"+"SUCCESS");
		
		return "SUCCESS";
	}
}
