package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Logger;

import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
//import remote.wise.util.ThreadForAlertDetailMigration;
import remote.wise.util.AssetEventArchivalDataPurge;
import remote.wise.util.ThreadForAlertMigration;
import remote.wise.util.ThreadForClosedAlertDetailMigration;
import remote.wise.util.ThreadForMigration;
import remote.wise.util.ThreadForOpenAlertDetailMigration;

public class DayAggMigrationImpl implements Runnable{
	Thread t;
	String startDate;
	String endDate;
	String VIN;
	String migrationType;
	
	public DayAggMigrationImpl(){
		
	}
	
    public DayAggMigrationImpl(String startDate,String endDate,String VIN,String migrationType){
      t = new Thread(this, "Migration Call");
      this.startDate=startDate;
  	  this.endDate=endDate;
  	  this.VIN=VIN;
  	  this.migrationType=migrationType;
  	  t.start();
	}

	/**
	 * @param args
	 */
	public String migrateDayAgg(String startDate,String endDate,String VIN,String migrationType) {
		// TODO Auto-generated method stub
		String response = "SUCCESS";
		
		if(migrationType.equalsIgnoreCase("RemoteDayAgg"))
			response = migrateRemoteDayAgg(startDate,endDate,VIN);
		else if(migrationType.equalsIgnoreCase("AlertSummary"))
			response = migrateAlertSummary(startDate,endDate,VIN);
		else if(migrationType.equalsIgnoreCase("AlertDetails"))
			response = migrateAlertDetails(startDate,endDate,VIN);
		//iLogger.info();
		return response;
	}

	public String migrateAlertSummary(String startDate, String endDate,
			String vIN) {
		// TODO Auto-generated method stub
		String response = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
		ExecutorService executors = null;
		//String startDate = "2016-02-29";
		ConnectMySQL connMySql = new ConnectMySQL();
		Statement statement = null;
		Connection localConnection = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		Future<String> taskResult = null;
		//Date nxtDate = sdf.parse(startDate);
		try {
			Date start_date= sdf.parse(startDate);
			String nxtDateString = sdf.format(start_date);
			//iLogger.info(nxtDateString);
			iLogger.info("received input:"+nxtDateString);
			Date end_date= sdf.parse(endDate);
			int diffInDays = (int) ((end_date.getTime() - start_date.getTime()) / (1000 * 60 * 60 * 24));
			int period = diffInDays+1;
			//System.out.println("thread pool size:"+period);
			executors = Executors.newFixedThreadPool(period);
			Date nxt_date = start_date;
			Callable<String> callable = null;
			
			Collection threads = new ArrayList();
			List<Future<Integer>> futureList = null;
				
				//iLogger.info();
				//Date nxtDay = Cal
			
			

			//ExecutorCompletionService executors = Executors.new;
			CompletionService<String> taskCompletionService = new ExecutorCompletionService<String>(executors);
			long start = System.currentTimeMillis();
			while(nxt_date.getTime()<=end_date.getTime()){
				//Date time_Key = sdf.parse(nxtDateString);
				iLogger.info("Spawning a thread for "+nxtDateString);
				callable = new ThreadForAlertMigration(nxtDateString);
				taskCompletionService.submit(callable);
				iLogger.info("thread "+nxtDateString+" has successfully created");
				cal.setTime(nxt_date);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				nxt_date = cal.getTime();
				nxtDateString = sdf.format(nxt_date);
				//iLogger.info(nxtDateString);
			//	period++;
				}
			for(int j=0;j<period;j++){
			iLogger.info("trying to take from completion service");
			try {
			taskResult = taskCompletionService.take();
			
			iLogger.info("result for a task available in queue trying to get()");
			
			iLogger.info("result from task result :"+taskResult.get()+" j:"+j);
			//Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			// TODO Auto-generated catch block
			//int[] updatedCount = bue.getUpdateCounts();
			String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('heap','"+startDate+"-"+endDate+"','"+e.getMessage()+"')";
			
			//Statement statement;
			try {
				//ConnectMySQL connMySql = new ConnectMySQL();
				localConnection = connMySql.getProdDb2Connection();
				statement = localConnection.createStatement();
				statement.executeUpdate(faultInsertStmt);
			
			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			} 
			finally{
				
				if(statement!=null)
				{
					try {
						//localConnection = connMySql.getProdDb2Connection();
						statement.close();
					} catch (SQLException innere) {
						// TODO Auto-generated catch block
						innere.printStackTrace();
					}
				}
				if(localConnection!=null)
				{
					try {
						//localConnection = connMySql.getProdDb2Connection();
						localConnection.close();
					} catch (SQLException innere) {
						// TODO Auto-generated catch block
						innere.printStackTrace();
					}
				}
				}
			taskResult.cancel(true);
			return "FAILURE";
			//e.printStackTrace();
		} catch (ExecutionException e) {
			
			String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('heap','"+startDate+"-"+endDate+"','"+e.getMessage()+"')";
			
			
			try {
				//localConnection = connMySql.getConnection();
				localConnection = connMySql.getProdDb2Connection();
				statement = localConnection.createStatement();
				statement.executeUpdate(faultInsertStmt);
			
			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			}
			finally{
			if(localConnection!=null)
			{
				try {
					localConnection.close();
				} catch (SQLException innere) {
					// TODO Auto-generated catch block
					innere.printStackTrace();
				}
			}
			}
			// TODO Auto-generated catch block
			//e.printStackTrace();
			taskResult.cancel(true);
			return "FAILURE";
		}
		}
		//Future<Integer> future = executors.submit(callable);
		long end = System.currentTimeMillis();
		response = response+(end - start);
		iLogger.info("Data fetching has completed in "+((end - start)));
		iLogger.info("all the tasks have finished");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return "Parsing failure";
			//e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executors.shutdown();
		return response;
	}

	public String migrateRemoteDayAgg(String startDate, String endDate,
			String vIN) {
		// TODO Auto-generated method stub
		String response = "SUCCESS";
		Logger iLogger = InfoLoggerClass.logger;
		
		ExecutorService executors = null;
		//String startDate = "2016-02-29";
		ConnectMySQL connMySql = new ConnectMySQL();
		Statement statement = null;
		Connection localConnection = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		Future<String> taskResult = null;
		//Date nxtDate = sdf.parse(startDate);
		try {
			
			Date start_date= sdf.parse(startDate);
			Date received_date = start_date;
			try{
			localConnection = new ConnectMySQL().getProdDb2Connection();
			//pst = localConnection.prepareStatement(insertStatement);
			statement = localConnection.createStatement();
			ResultSet lastrunRS = statement.executeQuery("select max(TxnDate) as last_run from factInsight_dayAgg");
			
			if(lastrunRS.next()){
				start_date = lastrunRS.getDate("last_run");
				cal.setTime(start_date);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				start_date = cal.getTime();
			}
			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			} 
			finally{
				
				if(statement!=null)
				{
					try {
						//localConnection = connMySql.getProdDb2Connection();
						statement.close();
					} catch (SQLException innere) {
						// TODO Auto-generated catch block
						innere.printStackTrace();
					}
				}
				if(localConnection!=null)
				{
					try {
						//localConnection = connMySql.getProdDb2Connection();
						localConnection.close();
					} catch (SQLException innere) {
						// TODO Auto-generated catch block
						innere.printStackTrace();
					}
				}
			}
			
			if(received_date.before(start_date)){
					start_date = received_date;
			}
			Date nxt_date = start_date;
			String nxtDateString = sdf.format(start_date);
			//iLogger.info(nxtDateString);
			iLogger.info("received input:"+nxtDateString);
			Date end_date= sdf.parse(endDate);
			int diffInDays = (int) ((end_date.getTime() - start_date.getTime()) / (1000 * 60 * 60 * 24));
			int period = diffInDays+1;
			//System.out.println("thread pool size:"+period);
			executors = Executors.newFixedThreadPool(period);
			
			Callable<String> callable = null;
			
			Collection threads = new ArrayList();
			List<Future<Integer>> futureList = null;
				
				//iLogger.info();
				//Date nxtDay = Cal
			
			

			//ExecutorCompletionService executors = Executors.new;
			CompletionService<String> taskCompletionService = new ExecutorCompletionService<String>(executors);
			long start = System.currentTimeMillis();
			while(nxt_date.getTime()<=end_date.getTime()){
				//Date time_Key = sdf.parse(nxtDateString);
				iLogger.info("Spawning a thread for "+nxtDateString);
				callable = new ThreadForMigration(nxtDateString,vIN);
				taskCompletionService.submit(callable);
				iLogger.info("thread "+nxtDateString+" has successfully created");
				cal.setTime(nxt_date);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				nxt_date = cal.getTime();
				nxtDateString = sdf.format(nxt_date);
				//iLogger.info(nxtDateString);
			//	period++;
				}
			for(int j=0;j<period;j++){
			iLogger.info("trying to take from completion service");
			try {
			taskResult = taskCompletionService.take();
			
			iLogger.info("result for a task available in queue trying to get()");
			
			iLogger.info("result from task result :"+taskResult.get()+" j:"+j);
			//Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			// TODO Auto-generated catch block
			//int[] updatedCount = bue.getUpdateCounts();
			String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('heap','"+startDate+"-"+endDate+"','"+e.getMessage()+"')";
			
			//Statement statement;
			try {
				//ConnectMySQL connMySql = new ConnectMySQL();
				localConnection = connMySql.getProdDb2Connection();
				statement = localConnection.createStatement();
				statement.executeUpdate(faultInsertStmt);
			
			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			} 
			finally{
				
				if(statement!=null)
				{
					try {
						//localConnection = connMySql.getProdDb2Connection();
						statement.close();
					} catch (SQLException innere) {
						// TODO Auto-generated catch block
						innere.printStackTrace();
					}
				}
				if(localConnection!=null)
				{
					try {
						//localConnection = connMySql.getProdDb2Connection();
						localConnection.close();
					} catch (SQLException innere) {
						// TODO Auto-generated catch block
						innere.printStackTrace();
					}
				}
				}
			taskResult.cancel(true);
			return "FAILURE";
			//e.printStackTrace();
		} catch (ExecutionException e) {
			
			String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('heap','"+startDate+"-"+endDate+"','"+e.getMessage()+"')";
			
			
			try {
				//localConnection = connMySql.getConnection();
				localConnection = connMySql.getProdDb2Connection();
				statement = localConnection.createStatement();
				statement.executeUpdate(faultInsertStmt);
			
			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			}
			finally{
			if(localConnection!=null)
			{
				try {
					localConnection.close();
				} catch (SQLException innere) {
					// TODO Auto-generated catch block
					innere.printStackTrace();
				}
			}
			}
			// TODO Auto-generated catch block
			//e.printStackTrace();
			taskResult.cancel(true);
			return "FAILURE";
		}
		}
		//Future<Integer> future = executors.submit(callable);
		long end = System.currentTimeMillis();
		response = response+(end - start);
		iLogger.info("Data fetching has completed in "+((end - start)));
		iLogger.info("all the tasks have finished");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return "Parsing failure";
			//e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executors.shutdown();
		return response;
	}
	
	
	public String migrateAlertDetails(String startDate, String endDate,
			String vIN) {
		// TODO Auto-generated method stub
		String response = "SUCCESS";
Logger iLogger = InfoLoggerClass.logger;
		
		ExecutorService executors = null;
		//String startDate = "2016-02-29";
		ConnectMySQL connMySql = new ConnectMySQL();
		Statement statement = null;
		Connection localConnection = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		Future<String> taskResult = null;
		//Date nxtDate = sdf.parse(startDate);
		try {
			Date start_date= sdf.parse(startDate);
			String nxtDateString = sdf.format(start_date);
			//iLogger.info(nxtDateString);
			iLogger.info("received input:"+nxtDateString);
			Date end_date= sdf.parse(endDate);
			int diffInDays = (int) ((end_date.getTime() - start_date.getTime()) / (1000 * 60 * 60 * 24));
			int period = diffInDays+1;
			//System.out.println("thread pool size:"+period);
			executors = Executors.newFixedThreadPool(period);
			Date nxt_date = start_date;
			Callable<String> callable = null;
			Callable<String> callable1 = null;
			
			Collection threads = new ArrayList();
			List<Future<Integer>> futureList = null;
				
				//iLogger.info();
				//Date nxtDay = Cal
			
			

			//ExecutorCompletionService executors = Executors.new;
			CompletionService<String> taskCompletionService = new ExecutorCompletionService<String>(executors);
			long start = System.currentTimeMillis();
			while(nxt_date.getTime()<=end_date.getTime()){
				//Date time_Key = sdf.parse(nxtDateString);
				iLogger.info("Spawning a thread for "+nxtDateString);
				callable = new ThreadForOpenAlertDetailMigration(nxtDateString);
				callable1 = new ThreadForClosedAlertDetailMigration(nxtDateString);  
				taskCompletionService.submit(callable);
				taskCompletionService.submit(callable1);
				iLogger.info("thread "+nxtDateString+" has successfully created");
				cal.setTime(nxt_date);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				nxt_date = cal.getTime();
				nxtDateString = sdf.format(nxt_date);
				//iLogger.info(nxtDateString);
			//	period++;
				}
			for(int j=0;j<period;j++){
			iLogger.info("trying to take from completion service");
			try {
			taskResult = taskCompletionService.take();
			
			iLogger.info("result for a task available in queue trying to get()");
			
			iLogger.info("result from task result :"+taskResult.get()+" j:"+j);
			//Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			// TODO Auto-generated catch block
			//int[] updatedCount = bue.getUpdateCounts();
			String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('heap','"+startDate+"-"+endDate+"','"+e.getMessage()+"')";
			
			//Statement statement;
			try {
				//ConnectMySQL connMySql = new ConnectMySQL();
				localConnection = connMySql.getProdDb2Connection();
				statement = localConnection.createStatement();
				statement.executeUpdate(faultInsertStmt);
			
			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			} 
			finally{
				
				if(statement!=null)
				{
					try {
						//localConnection = connMySql.getProdDb2Connection();
						statement.close();
					} catch (SQLException innere) {
						// TODO Auto-generated catch block
						innere.printStackTrace();
					}
				}
				if(localConnection!=null)
				{
					try {
						//localConnection = connMySql.getProdDb2Connection();
						localConnection.close();
					} catch (SQLException innere) {
						// TODO Auto-generated catch block
						innere.printStackTrace();
					}
				}
				}
			taskResult.cancel(true);
			return "FAILURE";
			//e.printStackTrace();
		} catch (ExecutionException e) {
			
			String faultInsertStmt = "insert into fault_statements(serial_number,time_key,fault_details) values('heap','"+startDate+"-"+endDate+"','"+e.getMessage()+"')";
			
			
			try {
			//	localConnection = connMySql.getConnection();
				localConnection = connMySql.getProdDb2Connection();
				statement = localConnection.createStatement();
				statement.executeUpdate(faultInsertStmt);
			
			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			}
			finally{
			if(localConnection!=null)
			{
				try {
					localConnection.close();
				} catch (SQLException innere) {
					// TODO Auto-generated catch block
					innere.printStackTrace();
				}
			}
			}
			// TODO Auto-generated catch block
			//e.printStackTrace();
			taskResult.cancel(true);
			return "FAILURE";
		}
		}
		//Future<Integer> future = executors.submit(callable);
		long end = System.currentTimeMillis();
		response = response+(end - start);
		iLogger.info("Data fetching has completed in "+((end - start)));
		iLogger.info("all the tasks have finished");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return "Parsing failure";
			//e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		executors.shutdown();
		/*new AssetEventArchivalDataPurge().archiveAssetEventData();
		try {
			Thread.sleep(500);
		
		
		localConnection = connMySql.getConnection();
		statement = localConnection.createStatement();
		statement.executeUpdate("alter table asset_event truncate partition p2");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(statement!=null){
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(localConnection != null){
				try {
					localConnection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
			
		}*/
		
		return response;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		Logger iLogger = InfoLoggerClass.logger;
		
       String response = "SUCCESS";
		
		if(migrationType.equalsIgnoreCase("RemoteDayAgg"))
			response = migrateRemoteDayAgg(startDate,endDate,VIN);
		else if(migrationType.equalsIgnoreCase("AlertSummary"))
			response = migrateAlertSummary(startDate,endDate,VIN);
		else if(migrationType.equalsIgnoreCase("AlertDetails"))
			response = migrateAlertDetails(startDate,endDate,VIN);
		
		 iLogger.info("DayAggMigrationService response::"+response);
		
	}


	
}
