/**
 * 
 */
package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AssetControlUnitEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessobject.AlertDetailsRESTBO;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

/**
 * @author KO369761
 *
 */
public class BlockLLSubscriptionExpiredVINImpl {
	
	public String blockLLExpiredVIN(){
		
		Connection con = null;
		Statement statement = null;
		String response=null;
		AssetEntity asset = null;
		String selectQuery = null;
		String insertQuery = null;
		String updateQuery = null;

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Session session = HibernateUtil.getSessionFactory().openSession();

		try{
			iLogger.info("BlockLLSubscriptionExpiredVIN:blockLLExpiredVIN: into the implementation");
			ConnectMySQL connectionObj = new ConnectMySQL();
			con = connectionObj.getEdgeProxyConnection();
			statement = con.createStatement(); 
			ResultSet resultSet=null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date todayDate = new Date();
			session.beginTransaction();

			Query assetQuery = session.createQuery("from AssetEntity where install_date is not null and date(renewal_date) <= '"+sdf.format(todayDate)+"'");
			iLogger.info("BlockLLSubscriptionExpiredVIN:blockLLExpiredVIN: Query:"+assetQuery.getQueryString());
			Iterator assetItr = assetQuery.list().iterator();
			while(assetItr.hasNext()){
				asset = (AssetEntity) assetItr.next();
				iLogger.info("BlockLLSubscriptionExpiredVIN:blockLLExpiredVIN:blocked asset"+asset.getSerial_number().getSerialNumber());
				selectQuery = "select * from device_status_info where vin_no='"+asset.getSerial_number().getSerialNumber()+"'";
				resultSet = statement.executeQuery(selectQuery);

				if(resultSet.next()){
					iLogger.info("BlockLLSubscriptionExpiredVIN:blockLLExpiredVIN:updating in smart systems");
					updateQuery = "UPDATE device_status_info SET Renewal_Flag="+0+" where vin_no='"+asset.getSerial_number().getSerialNumber()+"'";
					statement.executeUpdate(updateQuery);
				}
				//Updating the renewal flag of the VIN
				else{
					iLogger.info("BlockLLSubscriptionExpiredVIN:blockLLExpiredVIN:inserting in smart systems");
					insertQuery = "INSERT INTO device_status_info(vin_no,Renewal_Flag) VALUES ('"+asset.getSerial_number().getSerialNumber()+"', "+0+")";
					statement.execute(insertQuery);	
				}
				asset.setRenewal_flag(0);
				session.update(asset);
			}

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			fLogger.fatal("BlockLLSubscriptionExpiredVIN:blockLLExpiredVIN: Exception caught :"+e.getMessage());
		} finally
        {
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
		return response;
	
	}

}
