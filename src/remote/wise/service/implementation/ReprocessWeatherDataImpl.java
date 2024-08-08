package remote.wise.service.implementation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import remote.wise.handler.WeatherDataProducer;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class ReprocessWeatherDataImpl {

	public String reprocessWeatherData() {
		String status = "SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String query = "SELECT * FROM weather_data_reprocess WHERE processed_flag=0";
		List<String> vinList = new ArrayList<>();
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(query)) {
			while (rs.next()) {
				vinList.add(rs.getString("serial_number"));
			}
			for (String vin : vinList) {
				String updateQuery = "UPDATE weather_data_reprocess SET processed_flag=1 WHERE serial_number='" + vin
						+ "'";
				statement.executeUpdate(updateQuery);
				// Call Weather data publisher
				iLogger.info("Reprocessing Weather details for vin :" + vin);
				new WeatherDataProducer(vin);
			}
		} catch (Exception e) {
			status = "FAILURE";
			fLogger.fatal(
					"ReprocessWeatherData :: reprocessWeatherData :: Unable to reprocess weather_data_reprocess after failure.",
					e);
		}
		return status;
	}

}
