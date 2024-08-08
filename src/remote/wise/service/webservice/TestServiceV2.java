package remote.wise.service.webservice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.util.ConnectMySQL;


@Path("/TestServiceV2")
public class TestServiceV2 {

    @GET
    @Path("/testMethod")
    @Produces(MediaType.TEXT_PLAIN)
    public String testMethod() {
        String status = "FAILURE";
        Logger fLogger = FatalLoggerClass.logger;
        String query = "SELECT Industry_Name from industry WHERE Industry_ID=1";
        String industryName = null;
        ConnectMySQL connectPool = new ConnectMySQL();
        try (Connection connection = connectPool.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                industryName = rs.getString("Industry_Name");
            }
            if (industryName != null) {
                status = "SUCCESS";
            }
        } catch (Exception e) {
            fLogger.fatal("Error in connecting Database");
        }
        return status;
    }
}
