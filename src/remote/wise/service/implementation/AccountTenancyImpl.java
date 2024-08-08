/**
 * 
 */
package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.TenancyEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.HibernateUtil;

/**
 * @author KI270523
 *
 */
public class AccountTenancyImpl {
	
	public String getAccountFromTenancy(String stringTenancyId){
		
	//System.out.println("TenancyId--"+stringTenancyId);
	// System.out.println("List-----"+tenancyId);	
    Logger fLogger = FatalLoggerClass.logger;
    String result = null;
    
	Session session = HibernateUtil.getSessionFactory().getCurrentSession();
	session.beginTransaction();
		
	//String Selectquery="select a.account_id,a.tenancy_id from AccountTenancyMapping a where a.tenancy_id in ("+stringTenancyId+")";
	
	//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
	
	String Selectquery="select c.mappingCode from AccountEntity c where c.status=true and c.account_id=(select account_id from AccountTenancyMapping where tenancy_id in ("+stringTenancyId+"))";
		
		//System.out.println("Selectquery---------"+Selectquery);
		Query query=session.createQuery(Selectquery);
		try{
		Iterator iterator=query.list().iterator();
		//Object[] result1=null;
		HashMap accountMap=new HashMap();
		JSONObject jsonobj=null;
		JSONArray jarry=new JSONArray();
		jsonobj=new JSONObject();
		
		if(iterator.hasNext())
		{
			
			/*result1 = (Object[]) iterator.next();
			AccountEntity accountId = (AccountEntity) result1[0];
			TenancyEntity tenancyID = (TenancyEntity) result1[1];
			
			System.out.println("account Id "+accountId.getAccountCode());
			System.out.println("tenancy id  "+tenancyID.getTenancy_id());
			accountMap.put(String.valueOf(tenancyID.getTenancy_id()),String.valueOf(accountId.getAccountCode()));*/
			
			String accountCode=(String) iterator.next();
			
			accountMap.put(stringTenancyId,accountCode);
		}
		jsonobj.putAll(accountMap);
		jarry.add(jsonobj);
		
		  result=jarry.toString();
		 // System.out.println("result------"+result);
		}catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}finally{
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
		
		
		return result;
	}
	
		//DF20190816-Abhishek::To fetch account code based on tenancyIdList 
		public List<String> getAccountCodeFromTenancy(String stringTenancyId){
			
			//System.out.println("TenancyId--"+stringTenancyId);
			// System.out.println("List-----"+tenancyId);	
		    Logger fLogger = FatalLoggerClass.logger;
		    String result = null;
		    List<String> accountList=new LinkedList<String>();
		    
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
				
			//String Selectquery="select a.account_id,a.tenancy_id from AccountTenancyMapping a where a.tenancy_id in ("+stringTenancyId+")";
			
			//Df20180129 @Roopa Multiple BP code changes--Taking the list of users that are mapped to given account code, based on Mapping code
			
			String Selectquery="select a.accountCode from AccountEntity a where a.mappingCode in(select c.mappingCode from AccountEntity c where c.status=true and " +
					"c.account_id in (select account_id from AccountTenancyMapping where tenancy_id in ("+stringTenancyId+")))";
				
				//System.out.println("Selectquery---------"+Selectquery);
				Query query=session.createQuery(Selectquery);
				try{
				Iterator iterator=query.list().iterator();
				//Object[] result1=null;
				
				/*JSONObject jsonobj=null;
				JSONArray jarry=new JSONArray();
				jsonobj=new JSONObject();*/
				
				while(iterator.hasNext())
				{
					
					/*result1 = (Object[]) iterator.next();
					AccountEntity accountId = (AccountEntity) result1[0];
					TenancyEntity tenancyID = (TenancyEntity) result1[1];
					
					System.out.println("account Id "+accountId.getAccountCode());
					System.out.println("tenancy id  "+tenancyID.getTenancy_id());
					accountMap.put(String.valueOf(tenancyID.getTenancy_id()),String.valueOf(accountId.getAccountCode()));*/
					
					String accountCode=(String) iterator.next();
					
					accountList.add(accountCode);
				}
				/*jsonobj.putAll(accountMap);
				jarry.add(jsonobj);
				
				  result=jarry.toString();*/
				 // System.out.println("result------"+result);
				}catch(Exception e)
				{
					fLogger.fatal("Exception :"+e);
				}finally{
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
				
				
				return accountList;
			}

}
