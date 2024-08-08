package remote.wise.service.webservice;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;

@Path("DealerListPropertiesFile")
public class DealerListPropertiesFile {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> dealerListPropertiesFile() {
		Logger fLogger = FatalLoggerClass.logger;
		String path = "/data3/JCBLiveLink/Non-DbmsUpdateFile/dealerList/dealerList.properties";
		List<String> dealname = new ArrayList<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(path));
			String currentLine = reader.readLine();
			if (!currentLine.isEmpty()) {
				String split1 = currentLine.split("=")[1];
				String[] split2 = split1.split("\\|");
				for (int i = 0; i < split2.length; i++) {
					dealname.add(split2[i]);
				}
			}
		} catch (FileNotFoundException e) {
			fLogger.fatal("FileNotFoundException  "+ e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			fLogger.fatal("IOException  "+ e.getMessage());
			e.printStackTrace();
		} catch (Exception e){
			fLogger.fatal("Exception  "+ e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return dealname;
	}

}
