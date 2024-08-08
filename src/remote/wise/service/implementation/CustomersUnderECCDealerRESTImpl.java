package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import remote.wise.businessentity.CustomerMasterEntity;
import remote.wise.businessobject.TenancyBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.NonDBMSCustomerEntity;
import remote.wise.util.HibernateUtil;

public class CustomersUnderECCDealerRESTImpl {

	@SuppressWarnings({ "rawtypes" })
	public List<NonDBMSCustomerEntity> getCustomersUnderDealer(String eccCode){

		List<NonDBMSCustomerEntity> nonDBMScustomerList = new ArrayList<NonDBMSCustomerEntity>();
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		CustomerMasterEntity customerEntity = null;

		Session session  = null;
		try{
			TenancyBO tenancyBoObj = new TenancyBO();
			String llAccountCode = tenancyBoObj.getLLAccountCode(eccCode);
			iLogger.info("CustomersUnderECCDealerLog - llAccountCode from TenancyBO:"+llAccountCode);
			if(llAccountCode==null){
				throw new CustomFault("Data not found in Mapping table for the AccountCode:"+eccCode);
			}
			else{
				eccCode = llAccountCode;
			}
			if(session == null||(session != null && session.getTransaction().isActive() && session.isDirty())){
				session = HibernateUtil.getSessionFactory().openSession();
			}
			
			String customerQ = (" from CustomerMasterEntity a where dealerCode='" +eccCode+"'");
			Query query = session.createQuery(customerQ);
			Iterator iterator = query.list().iterator();
			while(iterator.hasNext()){
				customerEntity = (CustomerMasterEntity) iterator.next();
				NonDBMSCustomerEntity nonDBMSCustomer = new NonDBMSCustomerEntity();
				nonDBMSCustomer.setCustomerCode(customerEntity.getCustomerCode());
				nonDBMSCustomer.setDealerECCCode(customerEntity.getDealerCode());
				nonDBMScustomerList.add(nonDBMSCustomer);
			}
		}catch(Exception e){
			fLogger.fatal("Exception in CustomersUnderECCDealerRESTImpl :"+e);
		}finally{
			if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return nonDBMScustomerList;
	}
}
