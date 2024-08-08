/**
 * JCB6266 : 20221111 : Dhiraj K : logging and Fault table update issue 
 */
package remote.wise.EAintegration.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.businessentity.FaultDetails;
import remote.wise.businessentity.InterfaceErrorCodes;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;
import remote.wise.util.HibernateUtil;

public class ErrorMessageHandler 
{
	//****************************************** To Insert/Update Fault Table for the Failed Messages ***************************************

	//DF20180205 - KO369761 - extra parameters added in this new method for interface logics redesign.
	public void handleErrorMessages_new(String messageId,String messageString,String fileRef, String process, String reprocessJobCode, String faultCause, String errorCode, String rejectionPoint) 
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;

		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("ErrorMessageHandler"+ "Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();

			int newFaultMsg =1;
			InterfaceErrorCodes errorCodeEntity = null;

			try
			{
				Query errorCodeQ = session.createQuery("from InterfaceErrorCodes where error_code= "+errorCode);
				Iterator errorCodeItr = errorCodeQ.list().iterator();
				if(errorCodeItr.hasNext()){
					errorCodeEntity = (InterfaceErrorCodes)errorCodeItr.next();
				}

				//DF20180207:KO369761 - Checking and removing for added offset(for identifying reprocess jobs we added a offset.)
				if(messageId!=null && messageId.split("\\|").length>1){
					messageId = messageId.split("\\|")[0];
				}

				Query query = session.createQuery("from FaultDetails where messageId='"+messageId+"'");
				Iterator itr = query.list().iterator();
				int deletionFlag = 0;
				while(itr.hasNext())
				{
					newFaultMsg=0;

					FaultDetails faultObj = (FaultDetails) itr.next();
					int faultCounter = faultObj.getFailureCounter()+1;
					faultObj.setFailureCounter(faultCounter);
					deletionFlag = faultObj.getDeletionFlag();

					//update reopenedDate if the failure counter < 5, since automatically failed messages will be reprocessed 5 times
					//DF20141031 - Rajani Nagaraju - update reopenedDate if the failure counter < 10, since automatically failed messages will be reprocessed 10 times
					if( (faultCounter) < 10)
					{
						//update reopened date to currentTime+6hrs
						Date currentDate = new java.util.Date(System.currentTimeMillis() + (6 * 60 * 60 * 1000));
						faultObj.setReprocessTimeStamp(new Timestamp(currentDate.getTime()));
						iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" Updating the fault table ");
					}
					else
					{
						//update reopened date to null
						faultObj.setReprocessTimeStamp(null);
					}

					//Update the Fault Cause - 20140715 - Rajani Nagaraju 
					faultObj.setFailureCause(faultCause);
					faultObj.setMessageString(messageString);
					faultObj.setErrorCode(errorCodeEntity);
					faultObj.setRejectionPoint(rejectionPoint);
					iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" Updating the Fault details table");
					session.update(faultObj);
				}
				
				//DF20180928 - KO369761 - Marking Previous duplicate records for deletion.
				if(deletionFlag == 0){
					try{
						ConnectMySQL connMySql = new ConnectMySQL();
						prodConnection = connMySql.getConnection();
						statement = prodConnection.createStatement();

						statement.executeUpdate("update fault_details set deletion_flag = 1 where message_id != '"+messageId+"' and message_string = '"+messageString+"'");

					}catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						fLogger.fatal("ErrorMessageHandler"+" Exception :"+e);
					}
				}

				if(newFaultMsg==1)
				{
					FaultDetails faultObj = new FaultDetails();
					faultObj.setMessageId(messageId);
					faultObj.setMessageString(messageString);
					faultObj.setFileName(fileRef);
					faultObj.setProcess(process);
					faultObj.setReprocessJobCode(reprocessJobCode);
					faultObj.setFailureCounter(1);
					faultObj.setErrorCode(errorCodeEntity);
					faultObj.setRejectionPoint(rejectionPoint);
					faultObj.setDeletionFlag(0);

					//reopened date = currentTime+6hrs
					Date currentDate = new java.util.Date(System.currentTimeMillis() + (6 * 60 * 60 * 1000));
					faultObj.setReprocessTimeStamp(new Timestamp(currentDate.getTime()));

					//20140715 - Rajani Nagaraju - Set Failure Cause
					faultObj.setFailureCause(faultCause);
					iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" Inserting data to  Fault details table");
					session.save(faultObj);
				}
			}
			catch(Exception e)
			{
				FileWriter fileWritter=null;
				BufferedWriter bufferWritter=null;

				try
				{
					//Put the record into a file in EAFiles folder if the record cannot be inserted to fault_details table
					int newFile=0;
					if(fileRef!=null)
					{
						//----- Create file name
						//String fileName=fileRef.split("\\.")[0];
						String fileName=fileRef+"_1000000"+".txt";
						File file = new File("/user/JCBLiveLink/EAIntegration/reprocessing/"+fileName);
						iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" created the file with filename "+fileName);
						//if file doesnt exists, then create it
						if(!file.exists())
						{
							newFile=1;
							file.createNewFile();
						}

						fileWritter = new FileWriter(file,true);
						bufferWritter = new BufferedWriter(fileWritter);
						if(newFile==0)
							bufferWritter.newLine();
						bufferWritter.write(messageString);

						iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" writing data to the file with filename "+fileName);
					}
				}

				catch(Exception e1)
				{
					fLogger.error("ErrorMessageHandler"+ fileRef+":"+messageString+":Cannot Connect to DB and cant create file as well: "+e1);
				}

				finally
				{
					if(bufferWritter!=null)
						bufferWritter.close();
					if(fileWritter!=null)
						fileWritter.close();
				}
			}
			finally
			{
				//DF20150508 - Rajani Nagaraju - Addig try catch around commit
				try
				{
					if(session.isOpen())
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e2)
				{
					fLogger.fatal("ErrorMessageHandler"+" Exception :"+e2);
				}

				if(session.isOpen())
				{
					session.flush();
					session.close();
				}
			}}catch(Exception e)
			{
				FileWriter fileWritter=null;
				BufferedWriter bufferWritter=null;

				try
				{
					//Put the record into a file in EAFiles folder if the record cannot be inserted to fault_details table
					int newFile=0;
					if(fileRef!=null)
					{
						//----- Create file name
						String fileName=fileRef.split(".")[0];
						fileName=fileName+"_1000000"+".txt";
						File file = new File("/user/JCBLiveLink/EAIntegration/reprocessing/"+fileName);
						iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" created the file with filename "+fileName);
						//if file doesnt exists, then create it
						if(!file.exists())
						{
							newFile=1;
							file.createNewFile();
						}

						fileWritter = new FileWriter(file,true);
						bufferWritter = new BufferedWriter(fileWritter);
						if(newFile==0)
							bufferWritter.newLine();
						bufferWritter.write(messageString);

						iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" writing data to the file with filename "+fileName);
					}
				}

				catch(IOException e1)
				{
					fLogger.error("ErrorMessageHandler"+ fileRef+":"+messageString+":Cannot Connect to DB and cant create file as well: "+e1);
				}

				finally
				{
					if(bufferWritter!=null)
						try {
							if(fileWritter!=null)
								fileWritter.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				}
			}
		finally {
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//DF20140715 - Rajani Nagaraju -  Robust Logging for EA File Processing 
	public void handleErrorMessages(String messageId,String messageString,String fileRef, String process, String reprocessJobCode, String faultCause) 
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;

		try
		{
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
			if(session.getTransaction().isActive() && session.isDirty())
			{
				iLogger.info("ErrorMessageHandler"+ "Opening a new session");
				session = HibernateUtil.getSessionFactory().openSession();
			}
			session.beginTransaction();

			int newFaultMsg =1;

			try
			{
				//DF20180928 - KO369761 - Marking Previous duplicate records for deletion.
				try{
					ConnectMySQL connMySql = new ConnectMySQL();
					prodConnection = connMySql.getConnection();
					statement = prodConnection.createStatement();
					//statement.executeUpdate("update fault_details set deletion_flag = 1 where message_id != '"+messageId+"' and message_string = '"+messageString+"'"); // JCB6266.o
					//JCB6266.sn
					String query = "update fault_details set deletion_flag = 1 where message_id != '"+messageId+"' and message_string = '"+messageString+"'";
					iLogger.info(query);
					statement.executeUpdate(query);
					//JCB6266.en
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					fLogger.fatal("ErrorMessageHandler"+" Exception :"+e);
				}

				Query query = session.createQuery("from FaultDetails where messageId='"+messageId+"'");
				Iterator itr = query.list().iterator();

				while(itr.hasNext())
				{
					newFaultMsg=0;

					FaultDetails faultObj = (FaultDetails) itr.next();
					int faultCounter = faultObj.getFailureCounter()+1;
					faultObj.setFailureCounter(faultCounter);

					//update reopenedDate if the failure counter < 5, since automatically failed messages will be reprocessed 5 times
					//DF20141031 - Rajani Nagaraju - update reopenedDate if the failure counter < 10, since automatically failed messages will be reprocessed 10 times
					if( (faultCounter) < 10)
					{
						//update reopened date to currentTime+6hrs
						Date currentDate = new java.util.Date(System.currentTimeMillis() + (6 * 60 * 60 * 1000));
						faultObj.setReprocessTimeStamp(new Timestamp(currentDate.getTime()));
						iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" Updating the fault table ");
					}
					else
					{
						//update reopened date to null
						faultObj.setReprocessTimeStamp(null);
					}

					//Update the Fault Cause - 20140715 - Rajani Nagaraju 
					faultObj.setFailureCause(faultCause);
					faultObj.setMessageString(messageString);
					iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" :  Updating the Fault details table");
					session.update(faultObj);
				}

				if(newFaultMsg==1)
				{
					FaultDetails faultObj = new FaultDetails();
					faultObj.setMessageId(messageId);
					faultObj.setMessageString(messageString);
					faultObj.setFileName(fileRef);
					faultObj.setProcess(process);
					faultObj.setReprocessJobCode(reprocessJobCode);
					faultObj.setFailureCounter(1);

					//reopened date = currentTime+6hrs
							Date currentDate = new java.util.Date(System.currentTimeMillis() + (6 * 60 * 60 * 1000));
							faultObj.setReprocessTimeStamp(new Timestamp(currentDate.getTime()));

							//20140715 - Rajani Nagaraju - Set Failure Cause
							faultObj.setFailureCause(faultCause);
							iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" Inserting data to  Fault details table");
							session.save(faultObj);
				}
			}

			catch(Exception e)
			{
				FileWriter fileWritter=null;
				BufferedWriter bufferWritter=null;

				try
				{
					//Put the record into a file in EAFiles folder if the record cannot be inserted to fault_details table
					int newFile=0;
					if(fileRef!=null)
					{
						//----- Create file name
						String fileName=fileRef.split("_")[0];
						String currentDate= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
						fileName=fileName+"_"+currentDate.replaceAll("-", "")+"_1000000"+".txt";
						File file = new File("/user/JCBLiveLink/EAIntegration/EAFiles/"+fileName);
						iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" created the file with filename "+fileName);
						//if file doesnt exists, then create it
						if(!file.exists())
						{
							newFile=1;
							file.createNewFile();
						}

						fileWritter = new FileWriter(file,true);
						bufferWritter = new BufferedWriter(fileWritter);
						if(newFile==0)
							bufferWritter.write("\n");
						bufferWritter.write(messageString);

						iLogger.info("ErrorMessageHandler"+ fileRef+":"+messageString+" writing data to the file with filename "+fileName);
					}
				}

				catch(IOException e1)
				{
					fLogger.error("ErrorMessageHandler"+ fileRef+":"+messageString+":Cannot Connect to DB and cant create file as well: "+e1);
				}

				finally
				{
					if(bufferWritter!=null)
						bufferWritter.close();
					if(fileWritter!=null)
						fileWritter.close();
				}
			}

			finally
			{
				//DF20150508 - Rajani Nagaraju - Addig try catch around commit
				try
				{
					if(session.isOpen())
						if(session.getTransaction().isActive())
						{
							session.getTransaction().commit();
						}
				}
				catch(Exception e2)
				{
					fLogger.fatal("ErrorMessageHandler"+" Exception :"+e2);
				}

				if(session.isOpen())
				{
					session.flush();
					session.close();
				}

			}

		}

		catch(Exception e)
		{
			FileWriter fileWritter=null;
			BufferedWriter bufferWritter=null;

			try
			{
				int newFile=0;
				//Put the record into a file in EAFiles folder if the record cannot be inserted to fault_details table
				if(fileRef!=null)
				{
					//----- Create file name
					String fileName=fileRef.split("_")[0];
					String currentDate= new SimpleDateFormat("yyyy-MM-dd").format(new Date());
					fileName=fileName+"_"+currentDate.replaceAll("-", "")+"_1000000"+".txt";
					File file = new File("/user/JCBLiveLink/EAIntegration/EAFiles/"+fileName);
					iLogger.info("ErrorMessageHandler outer fianlly"+ fileRef+":"+messageString+" writing data to the file with filename "+fileName);

					//if file doesnt exists, then create it
					if(!file.exists())
					{
						newFile=1;
						file.createNewFile();
					}

					fileWritter = new FileWriter(file,true);
					bufferWritter = new BufferedWriter(fileWritter);
					if(newFile==0)
						bufferWritter.write("\n");
					bufferWritter.write(messageString);
					iLogger.info("ErrorMessageHandler outer fianlly"+ fileRef+":"+messageString+" writing data to the file with filename "+fileName);

				}
			}

			catch(IOException e1)
			{
				fLogger.error("ErrorMessageHandler"+ fileRef+":"+messageString+":Cannot Connect to DB and cant create file as well: "+e1);
			}

			finally
			{
				try
				{
					if(bufferWritter!=null)
						bufferWritter.close();
					if(fileWritter!=null)
						fileWritter.close();
				}
				catch(IOException e2)
				{
					fLogger.error(e2);
				}
			}
		}finally{
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		
		}
	}


	//************************************** To delete from Fault Table if the message is reprocess successfully *******************************
	public void deleteErrorMessage(String messageId)
	{
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		Connection prodConnection = null;
		Statement statement = null;
		ResultSet rs = null;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		//DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new session when the getCurrentSession returns a dirty session(txns which is not yet committed exists)
		if(session.getTransaction().isActive() && session.isDirty())
		{
			iLogger.info("ErrorMessageHandler deleteErrorMessage Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();

		try
		{
			//DF20180207:KO369761 - Checking and removing for added offset(for identifying reprocess jobs we added a offset.)
			if(messageId!=null && messageId.split("\\|").length>1){
				messageId = messageId.split("\\|")[0];
			}
			
			//DF20180928 - KO369761 - Marking Previous duplicate records for deletion.
			try{
				ConnectMySQL connMySql = new ConnectMySQL();
				prodConnection = connMySql.getConnection();
				statement = prodConnection.createStatement();
				
				rs = statement.executeQuery("select message_string from fault_details where message_id = '"+messageId+"'");
				if(rs.next()){
					String messageString = rs.getString("message_string");
					statement.executeUpdate("update fault_details set deletion_flag = 1 where message_id != '"+messageId+"' and message_string = '"+messageString+"'");
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				fLogger.fatal("ErrorMessageHandler"+" Exception :"+e);
			}
			
			Query query = session.createQuery("delete from FaultDetails where messageId='"+messageId+"'");
			query.executeUpdate();
		}

		catch(Exception e)
		{
			fLogger.fatal("ErrorMessageHandler deleteErrorMessage  Exception :"+e);
		}

		finally
		{
			//DF20150508 - Rajani Nagaraju - Addig try catch around commit
			try
			{
				if(session.isOpen())
					if(session.getTransaction().isActive())
					{
						session.getTransaction().commit();
					}
			}
			catch(Exception e2)
			{
				fLogger.fatal("ErrorMessageHandler deleteErrorMessage Exception :"+e2);
			}

			if(session.isOpen())
			{
				session.flush();
				session.close();
			}
			
			if(rs != null)
				try{
					rs.close();
				}catch (SQLException e) {
					e.printStackTrace();
				}
			if(statement!=null)
				try {
					statement.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			if (prodConnection != null) {
				try {
					prodConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
