package remote.wise.service.implementation;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.dao.DisconnectedVinsDAO;
import remote.wise.log.InfoLogging.InfoLoggerClass;

public class DisconnectedVINSImpl {

	public String updateDisconnectedVINSFromVF(List<String> filesList,String sourceDir,String destinationDir) throws IOException {
		Logger infoLogger = InfoLoggerClass.logger;
		DisconnectedVinsDAO daoObj= new DisconnectedVinsDAO();
		Timestamp requestTS=new Timestamp(new Date().getTime());
		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
		String date= dformat.format(requestTS);

		infoLogger.info( "filesList: "+filesList+" sourceDir: "+sourceDir+ " destinationDir: "+destinationDir);
		String result=null;
		for(String inputFile:filesList){
			result=	daoObj.updateBlockedVinsAndFileWithCount(inputFile,sourceDir,destinationDir,date);
		}
		
		
		if(result!=null && result.equalsIgnoreCase("success")){
				result=	daoObj.updateAssetTable(date);
		}
		if(result!=null && result.equalsIgnoreCase("success")){
			result=	daoObj.updateTraceabilityForBlockedVin(date);
		}
		return result;
		
	}

}
