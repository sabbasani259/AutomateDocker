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

import remote.wise.businessentity.AssetEventEntity;
import remote.wise.businessentity.SOSCategoryMasterEntity;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.HibernateUtil;

/**
 * @author KO369761
 *
 */
public class SOSAlertCategoryImpl {

	public List<HashMap<String, String>> getSOSAlertCategories(int assetEventId){

		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<HashMap<String, String>> response = new ArrayList<HashMap<String, String>>();
		SOSCategoryMasterEntity sosCategory = null;
		HashMap<String, String> sosCategoryMap = null;
		String comments = null;
		int categoryId = 0;

		try{
			session.beginTransaction();
			
			Query aeQuery = session.createQuery(" from AssetEventEntity ae  where assetEventId ="+assetEventId);
			Iterator aeItr = aeQuery.list().iterator();
			if(aeItr.hasNext()){
				AssetEventEntity assetEvent = (AssetEventEntity) aeItr.next();
				comments = assetEvent.getComments();
			}

			if(comments != null){
				if(comments.split("\\|").length>1){
					categoryId = Integer.parseInt(comments.split("\\|")[0]);
					comments = comments.split("\\|")[1];
				}
			}

			Query categoryQuery = session.createQuery(" from SOSCategoryMasterEntity where categoryId = "+categoryId);
			Iterator queryItr = categoryQuery.list().iterator();
			if(queryItr.hasNext()){
				sosCategory = (SOSCategoryMasterEntity)queryItr.next();
				sosCategoryMap = new HashMap<String, String>();
				sosCategoryMap.put("category_id", String.valueOf(sosCategory.getCategoryId()));
				sosCategoryMap.put("category_name", sosCategory.getCategoryName());
				response.add(sosCategoryMap);
				categoryId = sosCategory.getCategoryId();
			}
			
			Query query = session.createQuery(" from SOSCategoryMasterEntity");
			Iterator categoryItr = query.list().iterator();

			while(categoryItr.hasNext()){
				sosCategory = (SOSCategoryMasterEntity)categoryItr.next();
				if(categoryId != sosCategory.getCategoryId()){
					sosCategoryMap = new HashMap<String, String>();
					sosCategoryMap.put("category_id", String.valueOf(sosCategory.getCategoryId()));
					sosCategoryMap.put("category_name", sosCategory.getCategoryName());
					response.add(sosCategoryMap);
				}
			}

		}catch(Exception e){
			fLogger.error("Exception Caught: "+e.getMessage());

		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}

		return response;
	}
}
