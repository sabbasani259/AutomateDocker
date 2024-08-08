package remote.wise.service.implementation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.AccountEntity;
import remote.wise.businessentity.ServiceScheduleEntity;
import remote.wise.dal.DynamicAMS_Doc_DAL;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.pojo.AMSDoc_DAO;
import remote.wise.service.datacontract.AssestServiceHistoryGetRespContract;
import remote.wise.service.datacontract.AssetServiceScheduleGetReqContract;
import remote.wise.service.datacontract.AssetServiceScheduleGetRespContract;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

//CR373 - 20230125 - Prasad - Service History Api
public class AssetServiceScheduleImplTwo {

	public List<AssestServiceHistoryGetRespContract> getAssetserviceScheduleTwo(String serialNumber ,String startDate , String endDate) {
		System.out.println("Start getAssetserviceScheduleTwo--->");
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
	
		List<AssestServiceHistoryGetRespContract> respobjList = new ArrayList<>();
		String doneCMH = null;
		String serviceDate = null;
		String serviceTicketNumber = null;
		String comments = null;
		Connection connection = null;
		Statement preparedStatementSelectQuery = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		try {
			connection = connFactory.getConnection();
			if (connection == null) {
				 iLogger.info(" mysql connection" + connection);
				String fail = "Fail Update the Records due to sql connection issues.!";
			}

			else {
				String query = null;
				if(null != startDate && null != endDate){
					
					query= "select sh.CMH,sh.serviceDate, sh.serviceTicketNumber, sh.comments FROM  service_history sh where  sh.serialNumber = '"+ serialNumber + "'"
							+ " and serviceDate >= '"+startDate+"' and serviceDate <= '"+endDate+"'   ";
				}else {
					query = "select sh.CMH,sh.serviceDate, sh.serviceTicketNumber, sh.comments FROM  service_history sh where  sh.serialNumber = '"+ serialNumber + "'  ";
					
				}
				 
				 iLogger.info("Query running in Service His--->" + query);
				 System.out.println("Query running in Service His--->" + query);
				preparedStatementSelectQuery = connection.prepareStatement(query);
				ResultSet rs = preparedStatementSelectQuery.executeQuery(query);
				while (rs.next()) {
					AssestServiceHistoryGetRespContract respob = new AssestServiceHistoryGetRespContract();
					doneCMH = rs.getString("CMH");
					serviceDate = rs.getString("serviceDate");
					serviceTicketNumber = rs.getString("serviceTicketNumber");
					comments = rs.getString("comments");
//					if(doneCMH != null){
//					double value = Double.parseDouble(doneCMH);
//					doneCMH = String.format("%.2f", value);}
					respob.setServiceDoneAt(serviceDate.substring(0,19));
					respob.setServiceDone(doneCMH);
					respob.setJobCardNumber(serviceTicketNumber);
					respob.setComments(comments);
					respobjList.add(respob);
					

				}
				
			}


		}

		catch (Exception e) {

			e.printStackTrace();

		} finally {
			try {
				if (preparedStatementSelectQuery != null)
					preparedStatementSelectQuery.close();

				if (connection != null)
					connection.close();

			}

			catch (SQLException e) {
				fLogger.fatal("SQLException :" + e);
			}
		}

		return respobjList;
	}

	public String getCurrentCMH(String serialNumber) {
		System.out.println("Start getCurrentCMH--->");

		
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;

		String currentCMH = null;

		Connection connection = null;
		Statement preparedStatementSelectQuery = null;
		ConnectMySQL connFactory = new ConnectMySQL();
		try {
			connection = connFactory.getConnection();
			if (connection == null) {
				 iLogger.info(" mysql connection" + connection);
				
			}

			else {
				String query = "select  cm.tmh FROM  com_rep_oem_enhanced cm  where  serial_number = '" + serialNumber
						+ "' ";

				System.out.println("Query running--->" + query);
				preparedStatementSelectQuery = connection.prepareStatement(query);
				ResultSet rs = preparedStatementSelectQuery.executeQuery(query);
				while (rs.next()) {

					currentCMH = rs.getString("tmh");
				   if(null != currentCMH){
					double value = Double.parseDouble(currentCMH);
					currentCMH = String.format("%.2f", value);
				   }

				}
			}

		}

		catch (Exception e) {

			e.printStackTrace();

		} finally {
			try {
				if (preparedStatementSelectQuery != null)
					preparedStatementSelectQuery.close();

				if (connection != null)
					connection.close();

			}

			catch (SQLException e) {
				fLogger.fatal("SQLException :" + e);
			}
		}

		iLogger.info("currentCMH for serialNumber :" + serialNumber + currentCMH);
		return currentCMH;
	}

}
