/**
 * 
 */
package remote.wise.util;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;

/**
 * @author ROOPN5
 *
 */
public class ApplicationAlertClosureExtended implements Runnable{
	Thread t;
	int segment;
	List<Integer> dtcEvents;
	List<Integer> normalEvents;
	HashMap<String, String> vinAlertStatusMapDetails;
	String dtcquery;
	String normalEventquery;

	public ApplicationAlertClosureExtended()
	{

	}

	public ApplicationAlertClosureExtended(int segmentID, List<Integer> dtcEventIds, List<Integer> normalEventIds, HashMap<String, String> vinAlertStatusMap, String dtcPreparedQuery, String normalEventPreparedQuery)
	{
		t = new Thread(this, "ApplicationAlertClosure");
		this.segment=segmentID;
		this.dtcEvents=dtcEventIds;
		this.normalEvents=normalEventIds;
		this.vinAlertStatusMapDetails=vinAlertStatusMap;
		this.dtcquery=dtcPreparedQuery;
		this.normalEventquery=normalEventPreparedQuery;

		t.start();
	}


	@Override
	public void run() {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		iLogger.info("ApplicationAlertClosureRESTService::Segment::"+segment+"START");

		String status =null;

		long startTime = System.currentTimeMillis();
		try{
			status = closeAlerts();
		}
		catch(Exception e){
			e.printStackTrace();
			fLogger.fatal("ApplicationAlertClosureRESTService::Segment::"+segment+"Exception Occured::"+e.getMessage());
		}
		long endTime = System.currentTimeMillis();
		iLogger.info("ApplicationAlertClosureRESTService::Segment::"+segment+" Turn around Time (in ms):"+(endTime-startTime)+"Status::"+status);
	}

	private String closeAlerts() {
		String result="SUCCESS";

		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;

		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		Statement stmt=null;
		Statement stmt1=null;
		ResultSet rs=null;
		ResultSet rs1=null;

		//Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		Timestamp currentTime = new Timestamp(new Date().getTime());

		ListToStringConversion conversion = new ListToStringConversion();

		try{

			String dtcIdListAsString = conversion.getIntegerListString(dtcEvents).toString();

			String normalEventIdListAsString = conversion.getIntegerListString(normalEvents).toString();

			/*if(session==null || !session.isOpen() || session.isDirty())
				session = HibernateUtil.getSessionFactory().openSession();*/

			/*Query VINS_PER_SEGID_Query = session.createQuery("select a.serial_number from AssetEntity a where a.segmentId = "+segment);
			Iterator VINS_PER_SEGID_Iterator = VINS_PER_SEGID_Query.list().iterator();
			iLogger.info("ApplicationAlertClosureRESTService::Segment::"+segment+" VIN Size:: "+VINS_PER_SEGID_Query.list().size());

			if(session!=null || session.isOpen()){
				try{
					session.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}*/

			connection = connFactory.getConnection();

			stmt=connection.createStatement();
			stmt1=connection.createStatement();

			rs=stmt.executeQuery("Select Serial_Number from asset where Segment_ID="+segment);

			ps1 = connection.prepareStatement(dtcquery);
			ps2 = connection.prepareStatement(normalEventquery);

			while(rs.next()) {


				//String serial_number = ((AssetControlUnitEntity)VINS_PER_SEGID_Iterator.next()).getSerialNumber();

				String serial_number = rs.getString("Serial_Number");


				if(vinAlertStatusMapDetails.containsKey(serial_number) && vinAlertStatusMapDetails.get(serial_number)!=null){
					try{
						String logTS=null;
						String closeDtc="false";
						String closeNormal="false";

						if(vinAlertStatusMapDetails.get(serial_number).split(",").length>1){

							logTS=vinAlertStatusMapDetails.get(serial_number).split(",")[0];

							String alertStatus=vinAlertStatusMapDetails.get(serial_number).split(",")[1]; 
							if(alertStatus.length()>1){

								if(alertStatus.substring(0, 1).equalsIgnoreCase("1")){
									closeDtc="true";
								}
								if(alertStatus.substring(1, 2).equalsIgnoreCase("1")){
									closeNormal="true";
								}

							}
						}
						if(closeDtc.equalsIgnoreCase("true")){


							ps1.setInt(1, 0);
							ps1.setString(2, logTS);
							ps1.setTimestamp(3, currentTime);
							ps1.setString(4, serial_number);
							ps1.setInt(5, 1);

							int j=6;

							if(stmt1==null || stmt1.isClosed())
								stmt1=connection.createStatement();	
							//DF20190204 @abhishek---->add partition key for faster retrieval
							rs1=stmt1.executeQuery("Select Event_ID,Event_Generated_Time from asset_event where Serial_Number='"+serial_number+"' and Active_Status=1 and PartitionKey =1 and Event_ID in ("+dtcIdListAsString+")");

							while(rs1.next()){

								// @Roopa Pushing the records to KAFKA queue for MOOLDA_Alerts

								String eventId=String.valueOf(rs1.getInt("Event_ID"));

								try{


									HashMap<String,String> MOOLDADataPayloadMap = new HashMap<String,String>();

									MOOLDADataPayloadMap.put("TXN_KEY","ApplicationAlertClosure"+"_"+serial_number+"_"+logTS);
									MOOLDADataPayloadMap.put("ASSET_ID",serial_number);
									MOOLDADataPayloadMap.put("TXN_TIME",logTS);
									MOOLDADataPayloadMap.put("EVT_GEN_TIME",String.valueOf(rs1.getTimestamp("Event_Generated_Time")));
									MOOLDADataPayloadMap.put("EVT_CLOSE_TIME",logTS);
									MOOLDADataPayloadMap.put("EVT_CREATED_TIME",String.valueOf(currentTime));
									MOOLDADataPayloadMap.put("EVENT_ID",eventId);
									MOOLDADataPayloadMap.put("EVENT_STATUS","0");
//									iLogger.debug("ApplicationAlertClosureRESTService::Segment::"+segment+" VIN::"+serial_number+"::invoking MOOLDA_Alerts Kafka publisher for EventID closue:"+eventId);
									new MOOLDAKafkaPublisher(MOOLDADataPayloadMap.get("TXN_KEY"),MOOLDADataPayloadMap);
								}
								catch(Exception e){
									e.printStackTrace();
									fLogger.fatal("ApplicationAlertClosureRESTService::Segment::"+segment+" VIN::"+serial_number+":Exception in invoking MOOLDA_Alerts Kafka publisher for EventID closure:"+eventId+":"+e.getMessage());
								}

								//@Roopa Pushing the records to KAFKA queue for MOOLDA_Alerts END


							}

							for(int i = 0; i <dtcEvents.size(); i++){
								ps1.setInt(j, dtcEvents.get(i));
								j++;
							}

							ps1.addBatch();

						}

						if(closeNormal.equalsIgnoreCase("true")){

							ps2.setInt(1, 0);
							ps2.setString(2, logTS);
							ps2.setTimestamp(3, currentTime);
							ps2.setString(4, serial_number);
							ps2.setInt(5, 1);

							int j=6;

							if(stmt1==null || stmt1.isClosed())
								stmt1=connection.createStatement();	

							rs1=stmt1.executeQuery("Select Event_ID,Event_Generated_Time from asset_event where Serial_Number='"+serial_number+"' and Active_Status=1 and PartitionKey =1 and Event_ID in ("+normalEventIdListAsString+")");

							while(rs1.next()){

								// @Roopa Pushing the records to KAFKA queue for MOOLDA_Alerts

								String eventId=String.valueOf(rs1.getInt("Event_ID"));

								try{


									HashMap<String,String> MOOLDADataPayloadMap = new HashMap<String,String>();



									MOOLDADataPayloadMap.put("TXN_KEY","ApplicationAlertClosure"+"_"+serial_number+"_"+logTS);
									MOOLDADataPayloadMap.put("ASSET_ID",serial_number);
									MOOLDADataPayloadMap.put("TXN_TIME",logTS);
									MOOLDADataPayloadMap.put("EVT_GEN_TIME",String.valueOf(rs1.getTimestamp("Event_Generated_Time")));
									MOOLDADataPayloadMap.put("EVT_CLOSE_TIME",logTS);
									MOOLDADataPayloadMap.put("EVT_CREATED_TIME",String.valueOf(currentTime));
									MOOLDADataPayloadMap.put("EVENT_ID",eventId);
									MOOLDADataPayloadMap.put("EVENT_STATUS","0");
//									iLogger.debug("ApplicationAlertClosureRESTService::Segment::"+segment+" VIN::"+serial_number+"::invoking MOOLDA_Alerts Kafka publisher for EventID closue:"+eventId);
									new MOOLDAKafkaPublisher(MOOLDADataPayloadMap.get("TXN_KEY"),MOOLDADataPayloadMap);
								}
								catch(Exception e){
									e.printStackTrace();
									fLogger.fatal("ApplicationAlertClosureRESTService::Segment::"+segment+" VIN::"+serial_number+":Exception in invoking MOOLDA_Alerts Kafka publisher for EventID closure:"+eventId+":"+e.getMessage());
								}

								//@Roopa Pushing the records to KAFKA queue for MOOLDA_Alerts END


							}

							for(int i = 0; i <normalEvents.size(); i++){
								ps2.setInt(j, normalEvents.get(i));
								j++;
							}

							ps2.addBatch();

						}




						iLogger.info("ApplicationAlertClosureRESTService::Segment::"+segment+" VIN::"+serial_number+" Alert Closure");
					}

					catch(Exception e){
						fLogger.fatal("ApplicationAlertClosureRESTService::Segment::"+segment+" VIN::"+serial_number+" Error:;"+e.getMessage());
					}
				}


			}

			try{

				ps1.executeBatch();
				ps2.executeBatch();

				ps1.clearBatch();
				ps2.clearBatch();
			}
			catch(BatchUpdateException bue){
				bue.printStackTrace();
				fLogger.fatal("ApplicationAlertClosureRESTService::Segment::"+segment+"::BatchUpdateException::"+bue.getMessage());
			}
			catch(Exception e){
				e.printStackTrace();
				fLogger.fatal("ApplicationAlertClosureRESTService::Segment::"+segment+"::Exception::"+e.getMessage());
			}
			finally{

				try {
					if(rs != null && !rs.isClosed()){
						rs.close();

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					if(rs1 != null && !rs1.isClosed()){
						rs1.close();

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					if(stmt != null && !stmt.isClosed()){
						stmt.close();

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					if(stmt1 != null && !stmt1.isClosed()){
						stmt1.close();

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					if(ps1 != null && !ps1.isClosed()){
						ps1.close();

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					if(ps2 != null && !ps2.isClosed()){
						ps2.close();

					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

				try {
					if(connection!= null && !connection.isClosed()){
						connection.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

		catch(Exception e){

			e.printStackTrace();
			fLogger.fatal("ApplicationAlertClosureRESTService::Segment::"+segment+"Exception Occured::"+e.getMessage());
			return "FAILURE";

		}

		return result;
	}




}
