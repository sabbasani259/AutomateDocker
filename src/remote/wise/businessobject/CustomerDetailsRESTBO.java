package remote.wise.businessobject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;

import remote.wise.service.datacontract.CustomerDetailsRespContract;
import remote.wise.util.HibernateUtil;

public class CustomerDetailsRESTBO {

	@SuppressWarnings("rawtypes")
	public List<CustomerDetailsRespContract> getCustomerDetails(String customerCode){

		List<CustomerDetailsRespContract>customerList = new ArrayList<CustomerDetailsRespContract>();
		Logger fLogger = FatalLoggerClass.logger;
		String custName =null;
		String emailId = null;
		String contactNumber=null;
		String cityName = null;
		String customerMaster = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try{
			session.beginTransaction();
			String customerQ = ("select a.customerdetail from CustomerMasterEntity a where a.customerCode='"+customerCode+"'");
			Query query = session.createQuery(customerQ);
			Iterator custItr = query.list().iterator();

			if(custItr.hasNext()){
				customerMaster = (String) custItr.next();
				String[] result = customerMaster.split("\\|");
				custName = result[1];
				cityName = result[4];
				emailId = result[9];
				contactNumber = result[10];
				CustomerDetailsRespContract respObj = new CustomerDetailsRespContract();
				respObj.setCustomerName(custName);
				respObj.setCity(cityName);
				respObj.setEmailId(emailId);
				respObj.setContactNumber(contactNumber);
				customerList.add(respObj);
			}
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
		return customerList;
	}
}
