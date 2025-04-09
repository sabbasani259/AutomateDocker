/**
 * 
 */
package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

/**
 * @author KI270523
 *
 */
public class ProfileCodeImpl {
	
	public String getProfileCodeDetails(){
		
		String result = null;
		Logger fLogger = FatalLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		
		String selectProfileCode = "select a.asset_group_name,b.asset_grp_code from AssetGroupEntity a, AssetGroupProfileEntity b where a.asset_group_id=b.asset_grp_id";
		Query query=session.createQuery(selectProfileCode);
		try{
		Iterator iterator=query.list().iterator();
		Object[] result1=null;
		HashMap profileMap=new HashMap();
		JSONObject jsonobj=null;
		JSONArray jarry=new JSONArray();
		jsonobj=new JSONObject();
		
		while(iterator.hasNext())
		{
			
			result1 = (Object[]) iterator.next();
			profileMap.put(result1[0].toString(), result1[1].toString());
			
		}
		jsonobj.putAll(profileMap);
		jarry.add(jsonobj);
		
		  result=jarry.toString();
		 //System.out.println("result---"+result);
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
	public HashMap getAssetModels(String loginTenancyId,int roleId,String assetGroupIds) {
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		String result = null;

		HashMap modelMap=new HashMap();
		List<Integer> ll=new ArrayList<>();
		
		try{

			String roleName=null;
			String  accId=null;
			Object[] result1=null;
			
			JSONObject jsonobj=null;
			JSONArray jarry=new JSONArray();
			jsonobj=new JSONObject();
			Query queryRoleName=session.createSQLQuery("select Role_Name from role where role_id="+"'"+roleId+"'");
			Iterator iterator=queryRoleName.list().iterator();
			while(iterator.hasNext())
			{
				roleName=(String)iterator.next();
			}
			String assetGroupidsInString ="";
			if(assetGroupIds !=null && !assetGroupIds.equals("")){
				assetGroupidsInString = convertListToString(assetGroupIds).toString();
			}
			 
			if(roleName != null && (roleName.equalsIgnoreCase("Customer") || roleName.equalsIgnoreCase("Customer Fleet Manager")))
		{
			String qry2 = null;
			if(assetGroupidsInString==""){
				qry2 = "select  distinct(at.asset_type_group_name),at.asset_group_id from asset_type at where at.asset_type_id in "+
						"(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("+loginTenancyId+")))";
			}
			else{
				qry2 = "select  distinct(at.asset_type_group_name),at.asset_group_id from asset_type at where at.asset_group_id in ("+assetGroupidsInString+") and at.asset_type_id in "+
						"(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID  in ("+loginTenancyId+")))";
			}
			iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-assetGroupIds:"+assetGroupIds+"-query:"+qry2);

			Query query2=session.createSQLQuery(qry2);
			Iterator iterator2=query2.list().iterator();
			while(iterator2.hasNext())
			{
				
				result1 = (Object[]) iterator2.next();
				if(result1[1] != null){
				if(result1[0] != null)
				{
					String qry3="select at.Asset_Type_ID from asset_type at where at.asset_type_group_name="+"'"+result1[0]+"'";
					Query query3=session.createSQLQuery(qry3);
					Iterator iterator3=query3.list().iterator();
					ll=new ArrayList<>();
					while(iterator3.hasNext())
					{
						
						ll.add((Integer)iterator3.next());
					}
					if(modelMap.containsKey(result1[1])){
						HashMap map = (HashMap) modelMap.get(result1[1]);
						map.put(result1[0], ll);
						modelMap.put(result1[1], map);
					}
					else{
						HashMap map = new HashMap();
						map.put(result1[0], ll);
						modelMap.put(result1[1], map);
					}
					
				}
				
			}	
				
			}
		}else{
			String qry4=null;
			if(assetGroupidsInString==""){
				qry4="select  distinct(at.asset_type_group_name), at.asset_group_id from asset_type at";
			}
			else{
				qry4="select  distinct(at.asset_type_group_name), at.asset_group_id from asset_type at where at.asset_group_id in ("+assetGroupidsInString+")";
			}
			iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-assetGroupIds:"+assetGroupIds+"-query:"+qry4);

			
			Query query4=session.createSQLQuery(qry4);
			Iterator iterator4=query4.list().iterator();
			while(iterator4.hasNext())
			{
				
				result1 = (Object[]) iterator4.next();
				if(result1[1] != null){
				if(result1[0] != null)
				{
					String qry5="select at.Asset_Type_ID from asset_type at where at.asset_type_group_name="+"'"+result1[0]+"'";
					Query query5=session.createSQLQuery(qry5);
					Iterator iterator5=query5.list().iterator();
					ll=new ArrayList<>();
					while(iterator5.hasNext())
					{
						
						ll.add((Integer)iterator5.next());
						
					}
					if(modelMap.containsKey(result1[1])){
						HashMap map = (HashMap) modelMap.get(result1[1]);
						map.put(result1[0], ll);
						modelMap.put(result1[1], map);
					}
					else{
						HashMap map = new HashMap();
						map.put(result1[0], ll);
						modelMap.put(result1[1], map);
					}	
				}
				}	
				
			}
		}
		}catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}finally{
	         
	          if(session.isOpen())
	          {
	             
	                session.close();
	          }
	          
	    }
		
		return modelMap;
	}
	private StringBuffer convertListToString(String assetGroupIds) {
        StringBuffer assetGroupIdLists=null;
        String[] split = assetGroupIds.split(java.util.regex.Pattern.quote(","));
        for(String i:split)
        {
              if(assetGroupIdLists == null){
                assetGroupIdLists=new StringBuffer();
                    assetGroupIdLists.append("'"+i+"'");
              }
              else{
                    assetGroupIdLists.append(",'"+i+"'");
              }
        }
        return assetGroupIdLists;
  }

	/*private StringBuffer convertListToString(String assetGroupIds) {
		StringBuffer assetGroupIdLists=new StringBuffer("");
		String[] split = assetGroupIds.split(java.util.regex.Pattern.quote(","));
		//assetGroupIds.split(Pattern.quote(","));
		for(String i:split)
		{
			if(assetGroupIdLists.equals("")){
				assetGroupIdLists.append("'"+i+"'");
			}
			else{
				assetGroupIdLists.append(",'"+i+"'");
			}
		}
		System.out.println(assetGroupIdLists);
		return assetGroupIdLists;
	}*/
	public String getAssetProfiles(String loginTenancyId,int roleId){
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		String result = null;

		HashMap profileMap=new HashMap();
		String roleName=null;
		
		
		JSONObject jsonobj=null;
		JSONArray jarry=new JSONArray();
		jsonobj=new JSONObject();
		try{
			Query queryRoleName=session.createSQLQuery("select Role_Name from role where role_id="+"'"+roleId+"'");
			Iterator iterator=queryRoleName.list().iterator();
			while(iterator.hasNext())
			{
				roleName=(String)iterator.next();
			}
		
		if(roleName != null && (roleName.equalsIgnoreCase("Customer") || roleName.equalsIgnoreCase("Customer Fleet Manager")))
		{
			String qry="select ag.Asset_Group_ID,  ag.Asseet_Group_Name from asset_group ag where ag.asset_group_id in "+
	"  (select distinct(p.Asset_Group_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("+loginTenancyId+")))";
			iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-query:"+qry);
			Query query=session.createSQLQuery(qry);
			List list=query.list();
			Iterator iterator2 = list.iterator();
			Object[] result1=null;
			
			while(iterator2.hasNext())
			{
				
				result1 = (Object[]) iterator2.next();
				if(result1[0] != null){
				if(result1[1]!=null){
					profileMap.put(result1[0].toString(), result1[1].toString());
				}
				}
				
			}
			jsonobj.putAll(profileMap);
			jarry.add(jsonobj);
			
			  result=jarry.toString();
		}else{
			String qry="select ag.Asset_Group_ID,  ag.Asseet_Group_Name from asset_group ag";
			iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-query:"+qry);
			Query query=session.createSQLQuery(qry);
			Iterator iterator3=query.list().iterator();
			Object[] result1=null;
			
			while(iterator3.hasNext())
			{
				
				result1 = (Object[]) iterator3.next();
				if(result1[0] != null){
				if(result1[1] !=null){
					profileMap.put(result1[0].toString(), result1[1].toString());
				}
				}
				
			}
			jsonobj.putAll(profileMap);
			jarry.add(jsonobj);
			
			  result=jarry.toString();
		}
		
			
		}catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}finally{
	         
	          if(session.isOpen())
	          {
	             
	                session.close();
	          }
		}
		return result;
}
	
	
public HashMap getModels(String loginTenancyId,int roleId,String assetGroupIds) {
		
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		String result = null;

		HashMap modelMap=new HashMap<String, String>();
		
		try{

			String roleName=null;
			String  accId=null;
			Object[] result1=null;
			
			JSONObject jsonobj=null;
			JSONArray jarry=new JSONArray();
			jsonobj=new JSONObject();
			Query queryRoleName=session.createSQLQuery("select Role_Name from role where role_id="+"'"+roleId+"'");
			Iterator iterator=queryRoleName.list().iterator();
			while(iterator.hasNext())
			{
				roleName=(String)iterator.next();
			}
			String assetGroupidsInString ="";
			if(assetGroupIds !=null && !assetGroupIds.equals("")){
				assetGroupidsInString = convertListToString(assetGroupIds).toString();
			}
			if(roleName != null && (roleName.equalsIgnoreCase("Customer") || roleName.equalsIgnoreCase("Customer Fleet Manager")))
			{
			String qry2 = null;
			if(assetGroupidsInString==""){
				qry2 = "select  distinct(at.asset_type_group_name),at.asset_group_id from asset_type at where at.asset_type_id in "+
						"(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("+loginTenancyId+")))";
			}
			else{
				qry2 = "select  distinct(at.asset_type_group_name),at.asset_group_id from asset_type at where at.asset_group_id in ("+assetGroupidsInString+") and at.asset_type_id in "+
						"(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("+loginTenancyId+")))";
			}
			iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-assetGroupIds:"+assetGroupIds+"-query:"+qry2);

			Query query2=session.createSQLQuery(qry2);
			Iterator iterator2=query2.list().iterator();
			while(iterator2.hasNext())
			{
				
				result1 = (Object[]) iterator2.next();
				if(result1[1] != null){
				if(result1[0] != null)
				{
					System.out.println("result1[1] "+result1[1]);
					String qry3="select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="+"'"+result1[0]+"'";
					Query query3=session.createSQLQuery(qry3);
					//query2.setString(0, result1[1]);
					System.out.println("query3  "+query3);
					Iterator iterator3=query3.list().iterator();
					List<String> ll=new ArrayList<>();
					while(iterator3.hasNext())
					{
						
						ll.add((String)iterator3.next());
					}
					
					if(modelMap.containsKey(result1[1])){
						HashMap map = (HashMap) modelMap.get(result1[1]);
						map.put(result1[0], ll);
						modelMap.put(result1[1], map);
					}
					else{
						HashMap map = new HashMap();
						map.put(result1[0], ll);
						modelMap.put(result1[1], map);
					}
				}
			}
			}
			
		}else{
			String qry4 = null;
			if(assetGroupidsInString==""){
				qry4="select  distinct(at.asset_type_group_name), at.asset_group_id from asset_type at";
			}
			else{
				qry4="select  distinct(at.asset_type_group_name), at.asset_group_id from asset_type at where at.asset_group_id in ("+assetGroupidsInString+")";
			}
			iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-assetGroupIds:"+assetGroupIds+"-query:"+qry4);

			Query query4=session.createSQLQuery(qry4);
			Iterator iterator4=query4.list().iterator();
			while(iterator4.hasNext())
			{
				
				result1 = (Object[]) iterator4.next();
				if(result1[1] != null){
				if(result1[0] != null)
				{
					String qry5="select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="+"'"+result1[0]+"'";
					Query query5=session.createSQLQuery(qry5);
					//query2.setString(0, result1[1]);
					System.out.println("query5  "+query5);
					Iterator iterator5=query5.list().iterator();
					List<String> ll=new ArrayList<>();
					while(iterator5.hasNext())
					{
						
						ll.add((String)iterator5.next());
					}
					
					if(modelMap.containsKey(result1[1])){
						HashMap map = (HashMap) modelMap.get(result1[1]);
						map.put(result1[0], ll);
						modelMap.put(result1[1], map);
					}
					else{
						HashMap map = new HashMap();
						map.put(result1[0], ll);
						modelMap.put(result1[1], map);
					}
				}
		}
			}
		}	
		}catch(Exception e)
		{
			fLogger.fatal("Exception :"+e);
		}finally{
	         
	          if(session.isOpen())
	          {
	             
	                session.close();
	          }
	          
	    }
		
		return modelMap;
	}
public String getAssetProfileCodes(String loginTenancyId,int roleId){
	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;
	
	Session session = HibernateUtil.getSessionFactory().openSession();
	String result = null;

	HashMap profileMap=new HashMap();
	String roleName=null;
	
	
	JSONObject jsonobj=null;
	JSONArray jarry=new JSONArray();
	jsonobj=new JSONObject();
	try{
		Query queryRoleName=session.createSQLQuery("select Role_Name from role where role_id="+"'"+roleId+"'");
		Iterator iterator=queryRoleName.list().iterator();
		while(iterator.hasNext())
		{
			roleName=(String)iterator.next();
		}
	
		if(roleName != null && (roleName.equalsIgnoreCase("Customer") || roleName.equalsIgnoreCase("Customer Fleet Manager")))
	{
		String qry="select a.Asseet_Group_Name,b.asset_grp_code from asset_group a, asset_group_profile b where a.Asset_Group_ID=b.asset_grp_id and a.Asset_Group_ID in "+
"  (select distinct(p.Asset_Group_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in("+loginTenancyId+")))";
		iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-query:"+qry);
		Query query=session.createSQLQuery(qry);
		List list=query.list();
		System.out.println(list.size());
		Iterator iterator2 = list.iterator();
		Object[] result1=null;
		
		while(iterator2.hasNext())
		{
			
			result1 = (Object[]) iterator2.next();
			if(result1[0] != null){
			if(result1[1]!=null){
				profileMap.put(result1[0].toString(), result1[1].toString());
			}
			}
			
		}
		jsonobj.putAll(profileMap);
		jarry.add(jsonobj);
		
		  result=jarry.toString();
	}else{
		String qry="select a.Asseet_Group_Name,b.asset_grp_code from asset_group a, asset_group_profile b where a.Asset_Group_ID=b.asset_grp_id";
		iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-query:"+qry);
		Query query=session.createSQLQuery(qry);
		Iterator iterator3=query.list().iterator();
		Object[] result1=null;
		
		while(iterator3.hasNext())
		{
			
			result1 = (Object[]) iterator3.next();
			if(result1[0] != null){
			if(result1[1] != null){
				profileMap.put(result1[0].toString(), result1[1].toString());
			}
			}
			
		}
		jsonobj.putAll(profileMap);
		jarry.add(jsonobj);
		
		  result=jarry.toString();
	}
	
		
	}catch(Exception e)
	{
		fLogger.fatal("Exception :"+e);
	}finally{
         
          if(session.isOpen())
          {
             
                session.close();
          }
	}
	return result;
}

public HashMap getModelCodes(String loginTenancyId,int roleId,String assetGroupCodes) {
	
	Logger fLogger = FatalLoggerClass.logger;
	Logger iLogger = InfoLoggerClass.logger;
	
	Session session = HibernateUtil.getSessionFactory().openSession();

	HashMap modelMap=new HashMap<String, String>();
	
	try{

		String roleName=null;
		Object[] result1=null;
		
		JSONObject jsonobj=null;
		JSONArray jarry=new JSONArray();
		jsonobj=new JSONObject();
		Query queryRoleName=session.createSQLQuery("select Role_Name from role where role_id="+"'"+roleId+"'");
		Iterator iterator=queryRoleName.list().iterator();
		while(iterator.hasNext())
		{
			roleName=(String)iterator.next();
		}
		String assetGroupCodesInString ="";
		if(assetGroupCodes !=null && !assetGroupCodes.equals("")){
			assetGroupCodesInString = convertListToString(assetGroupCodes).toString();
		}
		if(roleName != null && (roleName.equalsIgnoreCase("Customer") || roleName.equalsIgnoreCase("Customer Fleet Manager"))){
		String qry2 = null;
		if(assetGroupCodesInString==""){
			qry2 = "select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where at.asset_group_id=agp.asset_grp_id and at.asset_type_id in "+
					"(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("+loginTenancyId+")))";
		}
		else{
			qry2 = "select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where agp.asset_grp_code in ("+assetGroupCodesInString+") and agp.asset_grp_id=at.asset_group_id and at.asset_type_id in "+
					"(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("+loginTenancyId+")))";
		}
		iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-assetGroupCodes:"+assetGroupCodes+"-query:"+qry2);

		Query query2=session.createSQLQuery(qry2);
		Iterator iterator2=query2.list().iterator();
		while(iterator2.hasNext())
		{
			
			result1 = (Object[]) iterator2.next();
			if(result1[1] != null){
			if(result1[0] != null)
			{
				String qry3="select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="+"'"+result1[0]+"'";
				Query query3=session.createSQLQuery(qry3);
				Iterator iterator3=query3.list().iterator();
				List<String> ll=new ArrayList<>();
				while(iterator3.hasNext())
				{
					
					ll.add((String)iterator3.next());
				}
				
				if(modelMap.containsKey(result1[1])){
					HashMap map = (HashMap) modelMap.get(result1[1]);
					map.put(result1[0], ll);
					modelMap.put(result1[1], map);
				}
				else{
					HashMap map = new HashMap();
					map.put(result1[0], ll);
					modelMap.put(result1[1], map);
				}
			}
			}
		}
		
	}else{
		String qry4 = null;
		if(assetGroupCodesInString==""){
			qry4="select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where at.asset_group_id=agp.asset_grp_id";
		}
		else{
			qry4="select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where at.asset_group_id=agp.asset_grp_id and agp.asset_grp_code in ("+assetGroupCodesInString+")";
		}
		iLogger.info("loginTenancyId:"+loginTenancyId+"-roleId:"+roleId+"-assetGroupCodes:"+assetGroupCodes+"-query:"+qry4);

		Query query4=session.createSQLQuery(qry4);
		Iterator iterator4=query4.list().iterator();
		while(iterator4.hasNext())
		{
			
			result1 = (Object[]) iterator4.next();
			if(result1[1] != null){
			if(result1[0] != null)
			{
				String qry5="select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="+"'"+result1[0]+"'";
				Query query5=session.createSQLQuery(qry5);
				//query2.setString(0, result1[1]);
				System.out.println("query5  "+query5);
				Iterator iterator5=query5.list().iterator();
				List<String> ll=new ArrayList<>();
				while(iterator5.hasNext())
				{
					
					ll.add((String)iterator5.next());
				}
				
				if(modelMap.containsKey(result1[1])){
					HashMap map = (HashMap) modelMap.get(result1[1]);
					map.put(result1[0], ll);
					modelMap.put(result1[1], map);
				}
				else{
					HashMap map = new HashMap();
					map.put(result1[0], ll);
					modelMap.put(result1[1], map);
				}
			}
			}
	}
	
	}	
	}catch(Exception e)
	{
		fLogger.fatal("Exception :"+e);
	}finally{
         
          if(session.isOpen())
          {
             
                session.close();
          }
          
    }
	
	return modelMap;
}
public static void main(String[] a){
	new ProfileCodeImpl().convertListToString("1");
}

	public String getAssetProfileCodeforTenancy(String loginTenancyId) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().openSession();
		String result = null;

		HashMap profileMap = new HashMap();
		String roleName = null;

		JSONObject jsonobj = null;
		JSONArray jarry = new JSONArray();
		jsonobj = new JSONObject();
		try {

			String qry = "select a.Asseet_Group_Name,b.asset_grp_code from asset_group a, asset_group_profile b where a.Asset_Group_ID=b.asset_grp_id and a.Asset_Group_ID in "
					+ "  (select distinct(a.Asset_Group_ID) from  asset_owner_snapshot  a where a.account_id in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in("
					+ loginTenancyId + ")))";

			Query query = session.createSQLQuery(qry);
			List list = query.list();
			Iterator iterator2 = list.iterator();
			Object[] result1 = null;

			while (iterator2.hasNext()) {

				result1 = (Object[]) iterator2.next();
				if (result1[0] != null) {
					if (result1[1] != null) {
						profileMap.put(result1[0].toString(), result1[1].toString());
					}
				}

			}
			jsonobj.putAll(profileMap);
			jarry.add(jsonobj);

			result = jarry.toString();

		} catch (Exception e) {
			fLogger.fatal("Exception :" + e);
		} finally {

			if (session.isOpen()) {

				session.close();
			}
		}
		return result;

	}
	
	public HashMap getBSVModelCodes(String loginTenancyId, int roleId, String assetGroupCodes,String isBSV) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().openSession();

		HashMap modelMap = new HashMap<String, String>();

		try {

			String roleName = null;
			Object[] result1 = null;

			JSONObject jsonobj = null;
			JSONArray jarry = new JSONArray();
			jsonobj = new JSONObject();
			Query queryRoleName = session
					.createSQLQuery("select Role_Name from role where role_id=" + "'" + roleId + "'");
			Iterator iterator = queryRoleName.list().iterator();
			while (iterator.hasNext()) {
				roleName = (String) iterator.next();
			}
			String assetGroupCodesInString = "";
			if (assetGroupCodes != null && !assetGroupCodes.equals("")) {
				assetGroupCodesInString = convertListToString(assetGroupCodes).toString();
			}
			if (isBSV.equals("1")) {
			if (roleName != null
					&& (roleName.equalsIgnoreCase("Customer") || roleName.equalsIgnoreCase("Customer Fleet Manager"))) {
				String qry2 = null;
				if (assetGroupCodesInString == "") {
					qry2 = "select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where at.asset_group_id=agp.asset_grp_id and at.asset_type_id in "
							+ "(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("
							+ loginTenancyId + "))) and at.BSV_Check=1";
				} else {
					qry2 = "select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where agp.asset_grp_code in ("
							+ assetGroupCodesInString
							+ ") and agp.asset_grp_id=at.asset_group_id and at.asset_type_id in "
							+ "(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("
							+ loginTenancyId + "))) and at.BSV_Check=1";
				}
				iLogger.info("loginTenancyId:" + loginTenancyId + "-roleId:" + roleId + "-assetGroupCodes:"
						+ assetGroupCodes + "-query:" + qry2);

				Query query2 = session.createSQLQuery(qry2);
				Iterator iterator2 = query2.list().iterator();
				while (iterator2.hasNext()) {

					result1 = (Object[]) iterator2.next();
					if (result1[1] != null) {
						if (result1[0] != null) {
							String qry3 = "select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="
									+ "'" + result1[0] + "'";
							Query query3 = session.createSQLQuery(qry3);
							Iterator iterator3 = query3.list().iterator();
							List<String> ll = new ArrayList<>();
							while (iterator3.hasNext()) {

								ll.add((String) iterator3.next());
							}

							if (modelMap.containsKey(result1[1])) {
								HashMap map = (HashMap) modelMap.get(result1[1]);
								map.put(result1[0], ll);
								modelMap.put(result1[1], map);
							} else {
								HashMap map = new HashMap();
								map.put(result1[0], ll);
								modelMap.put(result1[1], map);
							}
						}
					}
				}

			} else {
				String qry4 = null;
				if (assetGroupCodesInString == "") {
					qry4 = "select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where at.asset_group_id=agp.asset_grp_id and at.BSV_Check=1";
				} else {
					qry4 = "select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where at.asset_group_id=agp.asset_grp_id and agp.asset_grp_code in ("
							+ assetGroupCodesInString + ") and at.BSV_Check=1";
				}
				iLogger.info("loginTenancyId:" + loginTenancyId + "-roleId:" + roleId + "-assetGroupCodes:"
						+ assetGroupCodes + "-query:" + qry4);

				Query query4 = session.createSQLQuery(qry4);
				Iterator iterator4 = query4.list().iterator();
				while (iterator4.hasNext()) {

					result1 = (Object[]) iterator4.next();
					if (result1[1] != null) {
						if (result1[0] != null) {
							String qry5 = "select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="
									+ "'" + result1[0] + "'";
							Query query5 = session.createSQLQuery(qry5);
							// query2.setString(0, result1[1]);
							System.out.println("query5  " + query5);
							Iterator iterator5 = query5.list().iterator();
							List<String> ll = new ArrayList<>();
							while (iterator5.hasNext()) {

								ll.add((String) iterator5.next());
							}

							if (modelMap.containsKey(result1[1])) {
								HashMap map = (HashMap) modelMap.get(result1[1]);
								map.put(result1[0], ll);
								modelMap.put(result1[1], map);
							} else {
								HashMap map = new HashMap();
								map.put(result1[0], ll);
								modelMap.put(result1[1], map);
							}
						}
					}
				}

			}
			}
			else
			{
				if (roleName != null
						&& (roleName.equalsIgnoreCase("Customer") || roleName.equalsIgnoreCase("Customer Fleet Manager"))) {
					String qry2 = null;
					if (assetGroupCodesInString == "") {
						qry2 = "select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where at.asset_group_id=agp.asset_grp_id and at.asset_type_id in "
								+ "(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("
								+ loginTenancyId + "))) and  (at.BSV_Check != 1 OR at.BSV_Check IS NULL) ";
					} else {
						qry2 = "select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where agp.asset_grp_code in ("
								+ assetGroupCodesInString
								+ ") and agp.asset_grp_id=at.asset_group_id and at.asset_type_id in "
								+ "(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("
								+ loginTenancyId + "))) and  (at.BSV_Check != 1 OR at.BSV_Check IS NULL)";
					}
					iLogger.info("loginTenancyId:" + loginTenancyId + "-roleId:" + roleId + "-assetGroupCodes:"
							+ assetGroupCodes + "-query:" + qry2);

					Query query2 = session.createSQLQuery(qry2);
					Iterator iterator2 = query2.list().iterator();
					while (iterator2.hasNext()) {

						result1 = (Object[]) iterator2.next();
						if (result1[1] != null) {
							if (result1[0] != null) {
								String qry3 = "select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="
										+ "'" + result1[0] + "'";
								Query query3 = session.createSQLQuery(qry3);
								Iterator iterator3 = query3.list().iterator();
								List<String> ll = new ArrayList<>();
								while (iterator3.hasNext()) {

									ll.add((String) iterator3.next());
								}

								if (modelMap.containsKey(result1[1])) {
									HashMap map = (HashMap) modelMap.get(result1[1]);
									map.put(result1[0], ll);
									modelMap.put(result1[1], map);
								} else {
									HashMap map = new HashMap();
									map.put(result1[0], ll);
									modelMap.put(result1[1], map);
								}
							}
						}
					}

				} else {
					String qry4 = null;
					if (assetGroupCodesInString == "") {
						qry4 = "select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where at.asset_group_id=agp.asset_grp_id and  (at.BSV_Check != 1 OR at.BSV_Check IS NULL)";
					} else {
						qry4 = "select  distinct(at.asset_type_group_name), agp.asset_grp_code from asset_type at, asset_group_profile agp where at.asset_group_id=agp.asset_grp_id and agp.asset_grp_code in ("
								+ assetGroupCodesInString + ") and  (at.BSV_Check != 1 OR at.BSV_Check IS NULL)";
					}
					iLogger.info("loginTenancyId:" + loginTenancyId + "-roleId:" + roleId + "-assetGroupCodes:"
							+ assetGroupCodes + "-query:" + qry4);

					Query query4 = session.createSQLQuery(qry4);
					Iterator iterator4 = query4.list().iterator();
					while (iterator4.hasNext()) {

						result1 = (Object[]) iterator4.next();
						if (result1[1] != null) {
							if (result1[0] != null) {
								String qry5 = "select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="
										+ "'" + result1[0] + "'";
								Query query5 = session.createSQLQuery(qry5);
								// query2.setString(0, result1[1]);
								System.out.println("query5  " + query5);
								Iterator iterator5 = query5.list().iterator();
								List<String> ll = new ArrayList<>();
								while (iterator5.hasNext()) {

									ll.add((String) iterator5.next());
								}

								if (modelMap.containsKey(result1[1])) {
									HashMap map = (HashMap) modelMap.get(result1[1]);
									map.put(result1[0], ll);
									modelMap.put(result1[1], map);
								} else {
									HashMap map = new HashMap();
									map.put(result1[0], ll);
									modelMap.put(result1[1], map);
								}
							}
						}
					}

				}
			}
		} catch (Exception e) {
			fLogger.fatal("Exception :" + e);
		} finally {

			if (session.isOpen()) {

				session.close();
			}

		}

		return modelMap;
	}
	
	public HashMap getBSVModels(String loginTenancyId, int roleId, String assetGroupIds,String isBSV) {

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().openSession();
		String result = null;

		HashMap modelMap = new HashMap<String, String>();

		try {

			String roleName = null;
			String accId = null;
			Object[] result1 = null;

			JSONObject jsonobj = null;
			JSONArray jarry = new JSONArray();
			jsonobj = new JSONObject();
			Query queryRoleName = session
					.createSQLQuery("select Role_Name from role where role_id=" + "'" + roleId + "'");
			Iterator iterator = queryRoleName.list().iterator();
			while (iterator.hasNext()) {
				roleName = (String) iterator.next();
			}
			String assetGroupidsInString = "";
			if (assetGroupIds != null && !assetGroupIds.equals("")) {
				assetGroupidsInString = convertListToString(assetGroupIds).toString();
			}
			if (isBSV.equals("1")) {
			if (roleName != null
					&& (roleName.equalsIgnoreCase("Customer") || roleName.equalsIgnoreCase("Customer Fleet Manager"))) {
				
				String qry2 = null;
				if (assetGroupidsInString == "") {
					qry2 = "select  distinct(at.asset_type_group_name),at.asset_group_id from asset_type at where at.asset_type_id in "
							+ "(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("
							+ loginTenancyId + "))) and at.BSV_Check=1";
				} else {
					qry2 = "select  distinct(at.asset_type_group_name),at.asset_group_id from asset_type at where at.asset_group_id in ("
							+ assetGroupidsInString + ") and at.asset_type_id in "
							+ "(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("
							+ loginTenancyId + "))) and at.BSV_Check=1";
				}
				iLogger.info("loginTenancyId:" + loginTenancyId + "-roleId:" + roleId + "-assetGroupIds:"
						+ assetGroupIds + "-query:" + qry2);

				Query query2 = session.createSQLQuery(qry2);
				Iterator iterator2 = query2.list().iterator();
				while (iterator2.hasNext()) {

					result1 = (Object[]) iterator2.next();
					if (result1[1] != null) {
						if (result1[0] != null) {
							System.out.println("result1[1] " + result1[1]);
							String qry3 = "select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="
									+ "'" + result1[0] + "'";
							Query query3 = session.createSQLQuery(qry3);
							// query2.setString(0, result1[1]);
							System.out.println("query3  " + query3);
							Iterator iterator3 = query3.list().iterator();
							List<String> ll = new ArrayList<>();
							while (iterator3.hasNext()) {

								ll.add((String) iterator3.next());
							}

							if (modelMap.containsKey(result1[1])) {
								HashMap map = (HashMap) modelMap.get(result1[1]);
								map.put(result1[0], ll);
								modelMap.put(result1[1], map);
							} else {
								HashMap map = new HashMap();
								map.put(result1[0], ll);
								modelMap.put(result1[1], map);
							}
						}
					}
				}

			} else {
				String qry4 = null;
				if (assetGroupidsInString == "") {
					qry4 = "select  distinct(at.asset_type_group_name), at.asset_group_id from asset_type at where at.BSV_Check=1";
				} else {
					qry4 = "select  distinct(at.asset_type_group_name), at.asset_group_id from asset_type at where at.asset_group_id in ("
							+ assetGroupidsInString + ") and at.BSV_Check=1";
				}
				iLogger.info("loginTenancyId:" + loginTenancyId + "-roleId:" + roleId + "-assetGroupIds:"
						+ assetGroupIds + "-query:" + qry4);

				Query query4 = session.createSQLQuery(qry4);
				Iterator iterator4 = query4.list().iterator();
				while (iterator4.hasNext()) {

					result1 = (Object[]) iterator4.next();
					if (result1[1] != null) {
						if (result1[0] != null) {
							String qry5 = "select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="
									+ "'" + result1[0] + "'";
							Query query5 = session.createSQLQuery(qry5);
							// query2.setString(0, result1[1]);
							System.out.println("query5  " + query5);
							Iterator iterator5 = query5.list().iterator();
							List<String> ll = new ArrayList<>();
							while (iterator5.hasNext()) {

								ll.add((String) iterator5.next());
							}

							if (modelMap.containsKey(result1[1])) {
								HashMap map = (HashMap) modelMap.get(result1[1]);
								map.put(result1[0], ll);
								modelMap.put(result1[1], map);
							} else {
								HashMap map = new HashMap();
								map.put(result1[0], ll);
								modelMap.put(result1[1], map);
							}
						}
					}
				}
			}
			}
			else {
				if (roleName != null
						&& (roleName.equalsIgnoreCase("Customer") || roleName.equalsIgnoreCase("Customer Fleet Manager"))) {
					String qry2 = null;
					if (assetGroupidsInString == "") {
						qry2 = "select  distinct(at.asset_type_group_name),at.asset_group_id from asset_type at where at.asset_type_id in "
								+ "(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("
								+ loginTenancyId + "))) and  (at.BSV_Check != 1 OR at.BSV_Check IS NULL) ";
					} else {
						qry2 = "select  distinct(at.asset_type_group_name),at.asset_group_id from asset_type at where at.asset_group_id in ("
								+ assetGroupidsInString + ") and at.asset_type_id in "
								+ "(select distinct(p.Asset_Type_ID) from products p, asset  a where a.product_id=p.Product_ID and  a.Primary_Owner_ID in (select at.Account_ID from account_tenancy at where at.Tenancy_ID in ("
								+ loginTenancyId + "))) and  (at.BSV_Check != 1 OR at.BSV_Check IS NULL)";
					}
					iLogger.info("loginTenancyId:" + loginTenancyId + "-roleId:" + roleId + "-assetGroupIds:"
							+ assetGroupIds + "-query:" + qry2);

					Query query2 = session.createSQLQuery(qry2);
					Iterator iterator2 = query2.list().iterator();
					while (iterator2.hasNext()) {

						result1 = (Object[]) iterator2.next();
						if (result1[1] != null) {
							if (result1[0] != null) {
								System.out.println("result1[1] " + result1[1]);
								String qry3 = "select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="
										+ "'" + result1[0] + "'";
								Query query3 = session.createSQLQuery(qry3);
								// query2.setString(0, result1[1]);
								System.out.println("query3  " + query3);
								Iterator iterator3 = query3.list().iterator();
								List<String> ll = new ArrayList<>();
								while (iterator3.hasNext()) {

									ll.add((String) iterator3.next());
								}

								if (modelMap.containsKey(result1[1])) {
									HashMap map = (HashMap) modelMap.get(result1[1]);
									map.put(result1[0], ll);
									modelMap.put(result1[1], map);
								} else {
									HashMap map = new HashMap();
									map.put(result1[0], ll);
									modelMap.put(result1[1], map);
								}
							}
						}
					}

				} else {
					String qry4 = null;
					if (assetGroupidsInString == "") {
						qry4 = "select  distinct(at.asset_type_group_name), at.asset_group_id from asset_type at where (at.BSV_Check != 1 OR at.BSV_Check IS NULL) ";
					} else {
						qry4 = "select  distinct(at.asset_type_group_name), at.asset_group_id from asset_type at where at.asset_group_id in ("
								+ assetGroupidsInString + ") and  (at.BSV_Check != 1 OR at.BSV_Check IS NULL)";
					}
					iLogger.info("loginTenancyId:" + loginTenancyId + "-roleId:" + roleId + "-assetGroupIds:"
							+ assetGroupIds + "-query:" + qry4);

					Query query4 = session.createSQLQuery(qry4);
					Iterator iterator4 = query4.list().iterator();
					while (iterator4.hasNext()) {

						result1 = (Object[]) iterator4.next();
						if (result1[1] != null) {
							if (result1[0] != null) {
								String qry5 = "select at.Asset_Type_Code from asset_type at where at.asset_type_group_name="
										+ "'" + result1[0] + "'";
								Query query5 = session.createSQLQuery(qry5);
								// query2.setString(0, result1[1]);
								System.out.println("query5  " + query5);
								Iterator iterator5 = query5.list().iterator();
								List<String> ll = new ArrayList<>();
								while (iterator5.hasNext()) {

									ll.add((String) iterator5.next());
								}

								if (modelMap.containsKey(result1[1])) {
									HashMap map = (HashMap) modelMap.get(result1[1]);
									map.put(result1[0], ll);
									modelMap.put(result1[1], map);
								} else {
									HashMap map = new HashMap();
									map.put(result1[0], ll);
									modelMap.put(result1[1], map);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			fLogger.fatal("Exception :" + e);
		} finally {

			if (session.isOpen()) {

				session.close();
			}

		}

		return modelMap;
	}
}