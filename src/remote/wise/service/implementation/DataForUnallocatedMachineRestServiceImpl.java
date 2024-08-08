package remote.wise.service.implementation;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import com.opencsv.CSVWriter;

import remote.wise.dao.DataForUnallocatedMachineDao;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.CommonUtil;

public class DataForUnallocatedMachineRestServiceImpl {
	
	private Logger fLogger = FatalLoggerClass.logger;
	private Logger iLogger = InfoLoggerClass.logger;

	public List<Map<String,Object>> getOwnerData() {
		
		DataForUnallocatedMachineDao obj=new DataForUnallocatedMachineDao();
		List<Map<String,Object>> map=obj.fetchDataFromDB();
		return map;

	}
	public void saveToCopy(List<Map<String,Object>> data)
	{
    	String sourceDir = null;
		Properties prop = new Properties();
		
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));
			sourceDir= prop.getProperty("MachineReportPath");
		}
		
		catch (IOException e1) {
			
			e1.printStackTrace();
			fLogger.fatal("issue in while getting path from configuration path"
					+ e1.getMessage());
		}
		
		File file = new File(sourceDir+"/UnallocatedMachineReport.csv");
		iLogger.info("path for MachineUnallocatedReport" + file);
		try(CSVWriter writer =new CSVWriter(new FileWriter(file))) {
			if(!data.isEmpty())
			{
				Map<String,Object> firstRecord=data.get(0);
				String[] header=firstRecord.keySet().toArray(new String[0]);
				writer.writeNext(header);
			}
			
			for(Map<String,Object> record : data)
			{
				String[] recordValues=record.values().stream().map(value->(value!=null)?value.toString():"NA").toArray(String[]::new);
				writer.writeNext(recordValues);
				iLogger.info(recordValues);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}

