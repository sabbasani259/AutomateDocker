package remote.wise.businessobject;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;
////import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.File;
import java.io.FileFilter;
import java.sql.*;

import remote.wise.exception.CustomFault;
import remote.wise.log.BusinessErrorLogging.BusinessErrorLoggerClass;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.log.RejectedPacketLogging.RejectedPktLoggerClass;
import remote.wise.service.datacontract.AlertThresholdRespContract;
import remote.wise.service.datacontract.FotaStoredProcRespContract;
import remote.wise.util.ConnectMySqlfota;
import remote.wise.util.HibernateUtil;
//import remote.wise.util.WiseLogger;

public class FotaStoredBO {
	/*public static WiseLogger infoLogger = WiseLogger.getLogger("FotaStoredBO:","info");
	public static WiseLogger businessError = WiseLogger.getLogger("FotaStoredBO:","businessError");
	public static WiseLogger fatalError = WiseLogger.getLogger("FotaStoredBOBO:","fatalError");*/
	
	public String setFotaStoredProc()
	{

		String status = "SUCCESS";

	/*	Logger businessError = Logger.getLogger("businessErrorLogger");
		Logger fatalError = Logger.getLogger("fatalErrorLogger");
	*/
		Logger fLogger = FatalLoggerClass.logger;
    	Logger iLogger = InfoLoggerClass.logger;
    	
		Statement statement1 = null;
		Statement statement2 = null;
		ResultSet resultSet=null;
		ConnectMySqlfota connectionObj = new ConnectMySqlfota();
		Connection con = connectionObj.getConnection();

		try
		{
			statement1 = (Statement)con.createStatement();
			statement2 = (Statement)con.createStatement();
			String  sql1 ="Select file_path, file_id from fota_content_server_t where (version_id is null or version_id = '')";
			resultSet= statement1.executeQuery(sql1);

			while(resultSet.next()){
				String s1 =   resultSet.getString(1);
				int  i = resultSet.getInt(2);

				String string2[] = s1.split("/");
				String filename = string2[3];
				iLogger.info(filename.indexOf(".bin"));
				iLogger.info("***" + filename.substring(1, filename.indexOf(".bin")));
				String s =  filename.substring(1, filename.indexOf(".bin")).toString();
				String S1 = filename.substring(17, filename.indexOf(".bin")).toString();
				String Filename=s.concat(".bin");
				iLogger.info(" File Name : " +Filename);
				String fileSquence1 = filename.substring(filename.lastIndexOf(".")+1);
				int fileSquence = Integer.parseInt(fileSquence1);
				iLogger.info("fileSquence:"+fileSquence);
				String VersionId = (filename.substring((filename.lastIndexOf("_")+1),filename.lastIndexOf(".bin")));
				iLogger.info("VersionId"+VersionId);
				String sql = "update fota_content_server_t set Version_id= '"+VersionId+"',file_sequence='"+fileSquence+"',file_name= '"+Filename+"' where File_ID= "+i;
				statement2.executeUpdate(sql);
				//query = session.createSQLQuery("INSERT INTO fota_content_server (Version_ID,File_Sequence,File_Name)VALUES ("VersionId",value2,value3)");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fLogger.fatal("Exception:"+e.getMessage());
		}
		finally {

			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException sqlEx) { } // ignore

				resultSet = null;
			}
			if (statement1 != null) {
				try {
					statement1.close();
				} catch (SQLException sql) { } // ignore

				statement1 = null;
			}

			if (statement2 != null) {
				try {
					statement2.close();
				} catch (SQLException sql) { } // ignore

				statement2 = null;
			}

			if (con != null) {
				try {
					con.close();

				}catch (SQLException sql) { } // ignore
				con = null;
			}
		}

		return status;
	}


}
