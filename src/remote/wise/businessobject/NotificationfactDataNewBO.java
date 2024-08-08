package remote.wise.businessobject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.antlr.grammar.v3.ANTLRv3Parser.id_return;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountDimensionEntity;
import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.AssetClassDimensionEntity;
import remote.wise.businessentity.AssetEntity;
import remote.wise.businessentity.EventEntity;
import remote.wise.businessentity.NotificationDimensionEntity;
import remote.wise.businessentity.NotificationFactEntity;
import remote.wise.businessentity.TenancyDimensionEntity;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class NotificationfactDataNewBO {

	/*public static WiseLogger businessError = WiseLogger.getLogger(
			"NotificationfactDataNewBO:", "businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger(
			"NotificationfactDataNewBO:", "fatalError");
	public static WiseLogger infoLogger = WiseLogger.getLogger(
			"NotificationfactDataNewBO:", "info");
*/
	private SimpleDateFormat simpleDtStr = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss");
	private DateFormat dateStr = new SimpleDateFormat("yyyy-MM-dd");

	private AccountDimensionEntity accountDimensionId = null;
	private AssetClassDimensionEntity assetClassDimensionEntity = null;
	private TenancyDimensionEntity tenancyId = null;

	public void getVinSpecificData(Session session, String serialNumber,
			Timestamp transDate) {
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("getting data for VIN:"+serialNumber+" on timsstamp:"+transDate);
    	
		try {
			accountDimensionId = null;
			assetClassDimensionEntity = null;
			tenancyId = null;
			
			Date ownershipStartDate = null;
			int accountId1 = 0;
			Query query = null;
			query = session.createQuery("select max(ownershipStartDate) "
					+ "FROM AssetAccountMapping where serialNumber='"
					+ serialNumber + "' " + "and  ownershipStartDate <= '"
					+ transDate + "'");

			Iterator iterator = query.list().iterator();
			
			if (iterator.hasNext()) {
				ownershipStartDate = (Date) iterator.next();

				if (ownershipStartDate != null) {
					String owdate = dateStr.format(ownershipStartDate);
					query = session
							.createQuery("select a.accountId "
									+ "from AssetAccountMapping a ,AssetEntity c "
									+ "where a.serialNumber='"
									+ serialNumber
									+ "'  and c.active_status=true and c.serial_number=a.serialNumber "
									+ " and a.ownershipStartDate like '"
									+ owdate + "'");
					iterator = query.list().iterator();

					if (iterator.hasNext()) {
						AccountEntity accountEntity = (AccountEntity) iterator.next();
						accountId1 = accountEntity.getAccount_id();
						iLogger.info("AccountEntity "+ accountEntity.getAccount_id());

					}
				}
			}

			if (ownershipStartDate == null) {
				query = session.createQuery("select a.primary_owner_id "
						+ "from AssetEntity a " + "where a.serial_number='"
						+ serialNumber + "'and a.active_status=true ");
				iterator = query.list().iterator();

				if (iterator.hasNext()) {
					accountId1 = (Integer) iterator.next();
				}
				iLogger.info("AccountEntity when ownershipData is Null"+ accountId1);
			}

			query = session
					.createQuery("from AccountTenancyMapping where account_id="
							+ accountId1);
			iterator = query.list().iterator();
			if (iterator.hasNext()) {
				//DF20150408 - Rajani Nagaraju - Subquery returns more than one row exception - If for some reason, data is populated wrongly like 
				//-- like we have 2 tenancies for 1 account
				Query query3 = session.createQuery("from TenancyDimensionEntity where tenacy_Dimension_Id=" +
						"(select max(tenacy_Dimension_Id) from TenancyDimensionEntity where tenancyId =" +
						"(select min(tenancy_id) from AccountTenancyMapping where account_id="
								+ accountId1 + "))");
				Iterator itr3 = query3.list().iterator();

				if (itr3.hasNext()) {
					tenancyId = (TenancyDimensionEntity) itr3.next();
					iLogger.info("tenancy_dimension_id " + tenancyId);

				}
			}
			query = session
					.createQuery("from AccountDimensionEntity  where accountId="
							+ accountId1);
			iterator = query.list().iterator();

			while (iterator.hasNext()) {
				accountDimensionId = (AccountDimensionEntity) iterator.next();
				iLogger.info("AccountDimensionEntity"
						+ accountDimensionId.getAccountDimensionId());
			}

			query = session
					.createQuery("from AssetClassDimensionEntity b where b.assetClassDimensionId=(select max(a.assetClassDimensionId) from AssetClassDimensionEntity a where a.productId=(select productId from AssetEntity where serial_number='"
							+ serialNumber + "'))");
			iterator = query.list().iterator();
			while (iterator.hasNext()) {
				assetClassDimensionEntity = (AssetClassDimensionEntity) iterator.next();
				iLogger.info("AssetClassDimensionEntity"
						+ assetClassDimensionEntity.getAssetClassDimensionId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String setNotificationData() {
		Logger iLogger = InfoLoggerClass.logger;
		
    	Logger fLogger = FatalLoggerClass.logger;
    	
		int count2Process = 0;
		int countProcessed = 0;

		String vinProcessed = "";
		String vin2Process = "";

		Timestamp maxOlapDt = null;
		Timestamp lastOlapDt = null;
		Timestamp tranTime = null;
		Object[] result = null;

		AssetEntity vinIdentified = null;

		Session session = HibernateUtil.getSessionFactory().openSession();

		try {
			iLogger .info("NotificationfactDataNewBO: setNotificationData start: "
					+ simpleDtStr.format(Calendar.getInstance().getTime()));

			Query qry = session.createQuery("select max(a.Time_Key)  "
					+ " from NotificationFactEntity_DayAgg a");
			List lst = qry.list();
			Iterator itr = lst.iterator();

			if ((itr.hasNext()) && (lst.size() > 0) && (lst.get(0) != null)) {
				maxOlapDt = (Timestamp) itr.next();
			} else {
				qry = session
						.createQuery("select min(b.date) from DateDimensionEntity b");
				lst = qry.list();
				itr = lst.iterator();
				if ((itr.hasNext()) && (lst.size() > 0) && (lst.get(0) != null)) {
					maxOlapDt = (Timestamp) itr.next();
				} else {
					maxOlapDt = Timestamp.valueOf("2012-01-01 00:00:01");
				}
			}

			lastOlapDt = maxOlapDt; // Initialize to max OLAP date

			if (maxOlapDt.after(Timestamp.valueOf("2011-12-31 23:59:00"))) {
				qry = session
						.createQuery("select a.serialNumber, max(a.transactionTime) "
								+ " from AssetMonitoringHeaderEntity a "
								+ " where a.createdTimestamp >= '"
								+ maxOlapDt
								+ "'"
								+ " group by a.serialNumber, date(a.transactionTime) "
								+ " order by a.serialNumber, a.transactionTime");
				lst = qry.list();
				itr = lst.iterator();
				count2Process = lst.size();

				iLogger.info("NotificationfactDataNewBO: Last OLAP dt "
						+ lastOlapDt);
				iLogger.info("NotificationfactDataNewBO: Distint VIN count "
						+ count2Process);

				while (itr.hasNext()) {
					result = (Object[]) itr.next();

					vinIdentified = (AssetEntity) result[0];
					tranTime = (Timestamp) result[1];

					vin2Process = vinIdentified.getSerial_number()
							.getSerialNumber();

					if (!vin2Process.equalsIgnoreCase(vinProcessed)) {
						getVinSpecificData(session, vin2Process, tranTime);
					}
					populateNotificnDayAgg(vin2Process, tranTime);
					vinProcessed = vin2Process;
					countProcessed++;
				} // End of while loop for itrVin2Process.hasNext()
			} // End of if - lastOlapDt check
		} // End of Try block

		catch (Exception e) {
			fLogger
					.fatal("NotificationfactDataNewBO: Fatal error processing VIN: "
							+ vin2Process
							+ " "
							+ tranTime
							+ " "
							+ "Total Processed: "
							+ countProcessed
							+ " Message: " + e.getMessage());
			e.printStackTrace();
		}

		finally {
			iLogger.info("NotificationfactDataNewBO: setNotificationData end: "
					+ "Total Processed: " + countProcessed + " End time: "
					+ simpleDtStr.format(Calendar.getInstance().getTime()));
			if (session.isOpen()) {
				session.flush();
				session.close();
			}
		}
		return "success";
	}

	public void populateNotificnDayAgg(String serialNumber, Timestamp tranTime) {
		
		Logger iLogger = InfoLoggerClass.logger;
    	Logger fLogger = FatalLoggerClass.logger;
    	
		Session sessionIns = HibernateUtil.getSessionFactory().openSession();
		sessionIns.beginTransaction();
		try {
			iLogger.info("Populating NotificnDayAgg for VIN:"+serialNumber+" and TimeStamp:"+tranTime);
			if(tranTime!=null && serialNumber!=null){
				String transaction_timestamp = dateStr.format(tranTime);
				// get entries for this serial number, transaction time from asset event table
				Query query = sessionIns
						.createQuery("SELECT aee.serialNumber, aee.eventId, count(aee.eventId)"
								+ " FROM AssetEventEntity aee"
								+ " WHERE aee.serialNumber ='"
								+ serialNumber
								+ "' AND aee.eventGeneratedTime LIKE '"
								+ transaction_timestamp
								+ "%'"
								+ " GROUP BY aee.serialNumber,aee.eventId");
				int eventId = 0;
				Iterator iterator = query.list().iterator();
				Object[] result = null;

				NotificationDimensionEntity notificationDimensionEntity = null;
				int notificationDimensionId = 0, notificationCount = 0;
				long notificationcount = 0;
				while (iterator.hasNext()) {
					result = (Object[]) iterator.next();

					if (result[1] != null) {
						eventId = ((EventEntity) result[1]).getEventId();
						Query iDQuery = sessionIns
								.createQuery("From NotificationDimensionEntity nde where nde.Notification_Dimension_Id = (select max(Notification_Dimension_Id) from NotificationDimensionEntity where Notification_Id ="
										+ eventId
										+ " and LastUpdated_Date <='"
										+ tranTime + "' group by Notification_Id )");

						Iterator itr6 = iDQuery.list().iterator();

						while (itr6.hasNext()) {
							notificationDimensionEntity = (NotificationDimensionEntity) itr6
									.next();
							notificationDimensionId = notificationDimensionEntity
									.getNotification_Dimension_Id();
							iLogger.info("NotificationDimensionEntity "
									+ notificationDimensionId);
						}

					}
					if (result[2] != null) {
						notificationcount = (Long) result[2];
						notificationCount = (int) notificationcount;
					}

					// For above PIN, timekey ,event id , look for any entry in agg
					// table. if its there UPDATE, else INSERT
					Query checkQuery = sessionIns
							.createQuery("FROM NotificationFactEntity_DayAgg dayAgg"
									+ " WHERE dayAgg.SerialNumber ='"
									+ serialNumber
									+ "' AND dayAgg.Time_Key LIKE '"
									+ transaction_timestamp
									+ "%'"
									+ " AND dayAgg.Notification_Id = "
									+ notificationDimensionId);
					List checkList = checkQuery.list();
					Iterator checkIterator = checkList.iterator();
					if (checkList.size() > 0) {// UPDATE
						while (checkIterator.hasNext()) {
							NotificationFactEntity notificationFactEntity_DayAgg = (NotificationFactEntity) checkIterator
									.next();
							notificationFactEntity_DayAgg
									.setNotificationCount(notificationCount);
							iLogger.info("updating " + serialNumber + " for "
									+ transaction_timestamp + " id " + eventId);
							sessionIns.update("NotificationFactEntity_DayAgg",
									notificationFactEntity_DayAgg);
						}
					}

					else {// INSERT
						NotificationFactEntity notificationFactEntity_DayAgg = new NotificationFactEntity();

						notificationFactEntity_DayAgg.setAccount_Id(accountDimensionId);
						notificationFactEntity_DayAgg
								.setAssetClass_Id(assetClassDimensionEntity);
						notificationFactEntity_DayAgg
								.setNotification_Id(notificationDimensionEntity);
						notificationFactEntity_DayAgg.setSerialNumber(serialNumber);
						notificationFactEntity_DayAgg.setTenancy_Id(tenancyId);
						notificationFactEntity_DayAgg
								.setNotificationCount(notificationCount);

						notificationFactEntity_DayAgg.setTime_Key(Timestamp
								.valueOf(dateStr.format(tranTime) + " 00:00:00"));
						iLogger.info("inserting " + serialNumber + " for "
								+ transaction_timestamp + " id " + eventId);
						sessionIns.save("NotificationFactEntity_DayAgg",
								notificationFactEntity_DayAgg);

					}

				}
			}
			else{
				iLogger.info("failed inserting/updating since trans time is NULL OR/AND serialNumber is NULL !!");
			}
			

		} catch (Exception e) {
			if (sessionIns.getTransaction().isActive()) {
				sessionIns.getTransaction().rollback();
			}
			fLogger.fatal("NotificationfactDataNewBO: Fatal error processing VIN: "
					+ serialNumber + "Message: " + e.getMessage());
			e.printStackTrace();
		}

		finally {
			if (sessionIns.getTransaction().isActive()) {
				sessionIns.getTransaction().commit();
			}

			if (sessionIns.isOpen()) {
				sessionIns.flush();
				sessionIns.close();
			}

			sessionIns = null;
		}

	}

}