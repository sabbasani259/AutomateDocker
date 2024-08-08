package remote.wise.service.implementation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.HibernateUtil;

import remote.wise.service.datacontract.VinDetailsResponseContract;


public class VINServiceDetailsRESTImpl {


	@SuppressWarnings({"rawtypes" })
	public List<VinDetailsResponseContract> getVINServiceDetails(String serialNumber){

		List<VinDetailsResponseContract>vinDetailsList = new ArrayList<VinDetailsResponseContract>();
		Logger fLogger = FatalLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		String VINserviceQ = ("select serviceScheduleId, serviceName,dbmsPartCode from ServiceScheduleEntity where serviceScheduleId in (select serviceScheduleId from AssetServiceScheduleEntity where serialNumber ='"+serialNumber+"')");
		Query query1 = session.createQuery(VINserviceQ);
		try{
			Iterator iterator1 = query1.list().iterator();
			Object[] resultQ = null;
			while(iterator1.hasNext()){
				resultQ = (Object[]) iterator1.next();
				int serviceScheduleId = (Integer)resultQ[0];
				String serviceName = (String)resultQ[1];
				String dbmsPartCode = (String)resultQ[2];
				if(dbmsPartCode.matches("^[0-9]+$") && (!(serviceName.equalsIgnoreCase("Beyond Warranty")))){
					String serviceHistoryQ = ("from ServiceHistoryEntity where servicescheduleId ="+serviceScheduleId+" and serialNumber='"+serialNumber+"'");
					Query query2 = session.createQuery(serviceHistoryQ);
					Iterator iterator2 = query2.list().iterator();
					if(!iterator2.hasNext()){
						VinDetailsResponseContract vinResponse = new VinDetailsResponseContract();
						vinResponse.setServiceScheduleId(serviceScheduleId);
						vinResponse.setServiceName(serviceName);
						vinResponse.setDBMSPartCode(dbmsPartCode);
						vinDetailsList.add(vinResponse);
					}
				}
			}
		}catch(Exception e){
			fLogger.fatal("Exception in VINServiceDetailsRESTImpl:"+e);
		}finally{
			if(session.getTransaction().isActive()){
				session.getTransaction().commit();
			}if(session.isOpen()){
				session.flush();
				session.close();
			}
		}
		return vinDetailsList;
	}
}
