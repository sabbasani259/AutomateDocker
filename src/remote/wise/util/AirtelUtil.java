package remote.wise.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.opencsv.CSVWriter;

import remote.wise.exception.CustomFault;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.ACUResponse;
import remote.wise.service.datacontract.AccessTokenResponse;
import remote.wise.service.datacontract.AirtelCustomerBasketResponse;
import remote.wise.service.datacontract.SimDataResponse;

//20231127 CR452-AirtelApiIntegration-prasad
public class AirtelUtil {

	public static String genarateAccessToken() throws CustomFault {
		OutputStream os = null;
		String output = "FAILURE";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		BufferedReader br = null;
		String result = null;
		ObjectMapper mapper = new ObjectMapper();

		try {
			String URL = "https://openapi.airtel.in/iot/api/developer/generate/authtoken";

			System.out.println("airtelSimAccessToken UrL:" + URL);
			iLogger.info("airtelSimAccessToken UrL:" + URL);

			String urlParameters = "client_id=Fn7wFLcAruXuQLI0Q3Nt1Ue7WTbOP3my&client_secret=epKwyVjCdtZMLhR5DjUJ7w9axoM0Zisj";
			byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
			int postDataLength = postData.length;

			URL url = new URL(URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("accept", "application/json");
			connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("apikey", "SOkYF8XTG7OM6sMAqvT3VFdv3OQ0SQzL");
			connection.setRequestProperty("iv-user", "developer.16170@jcb.com");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(postData);
			wr.flush();

			System.out.println("airtelSimAccessToken ::---> HTTP code from Wise :" + connection.getResponseCode());
			iLogger.info("airtelSimAccessToken ::---> HTTP code from Wise :" + connection.getResponseCode());
			if (connection.getResponseCode() != 200) {
				iLogger.info("Failed : HTTP error code : " + connection.getResponseCode());
				System.out.println("Failed : HTTP error code :" + connection.getResponseCode());

				throw new CustomFault("Failed : HTTP error code : " + connection.getResponseCode());
			} else {

				br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
				while ((result = br.readLine()) != null) {
					output = result;
				}
				AccessTokenResponse respObj = new ObjectMapper().readValue(output, AccessTokenResponse.class);
				output = respObj.getData().getAccess_token() + "|" + respObj.getData().getRefresh_token();

			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("airtelSimAccessToken :Exception" + e.getMessage());
			fatalLogger.fatal("airtelSimAccessToken :Exception" + e.getMessage());

		}

		return output;

	}

	public static List<String> getBasketDetails(String token) throws CustomFault {

		OutputStream os = null;
		String output = "FAILURE";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		BufferedReader br = null;
		String result = null;
		ObjectMapper mapper = new ObjectMapper();
		List<String> basketIds = new ArrayList<>();

		try {
			String URL = "https://openapi.airtel.in/iot/api/customer/details/baskets?detailedView=true&myBaskets=false&pageNo=1&pageSize=250";

			System.out.println("getBasketDetails UrL:" + URL);
			iLogger.info("getBasketDetails UrL:" + URL);

			URL url = new URL(URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("apikey", "SOkYF8XTG7OM6sMAqvT3VFdv3OQ0SQzL");
			connection.setRequestProperty("customer-id", "16170");
			connection.setRequestProperty("iv-user", "developer.16170@jcb.com");
			connection.setRequestProperty("authorization", token);

			System.out.println("getBasketDetails ::---> HTTP code from Wise :" + connection.getResponseCode());
			iLogger.info("getBasketDetails ::---> HTTP code from Wise :" + connection.getResponseCode());
			if (connection.getResponseCode() != 200) {

				iLogger.info("Failed : HTTP error code : " + connection.getResponseCode());
				System.out.println("Failed : HTTP error code :" + connection.getResponseCode());

				throw new CustomFault("Failed : HTTP error code : " + connection.getResponseCode());

			} else {

				br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
				while ((result = br.readLine()) != null) {
					output = result;
				}

				AirtelCustomerBasketResponse respObj = new ObjectMapper().readValue(output,
						AirtelCustomerBasketResponse.class);

				for (int i = 0; i < respObj.getData().getBaskets().size(); i++) {
					basketIds.add(respObj.getData().getBaskets().get(i).getBasketId());

				}

			}
		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("getBasketDetails :Exception" + e.getMessage());
			fatalLogger.fatal("getBasketDetails :Exception" + e.getMessage());

		}
		return basketIds;

	}

	public static Map<String , Map<String ,String>> getSimDetails(String token, String basketId  ) {

		String output = "FAILURE";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		BufferedReader br = null;
		String result = null;
//		List<SimDataResponse> mDetails = new ArrayList<>();
		Map<String , Map<String ,String>> response = new HashMap<String , Map<String ,String>>();
 
		try {
			String URL = "https://openapi.airtel.in/iot/api/customer/details/basket/" + basketId + "/sims";

			iLogger.info("getBasketDetails UrL:" + URL);

			URL url = new URL(URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("apikey", "SOkYF8XTG7OM6sMAqvT3VFdv3OQ0SQzL");
			connection.setRequestProperty("customer-id", "16170");
			connection.setRequestProperty("iv-user", "developer.16170@jcb.com");
			connection.setRequestProperty("authorization", token);

			iLogger.info("getBasketDetails ::---> HTTP code from Wise :" + connection.getResponseCode());
			if (connection.getResponseCode() == 401) {

				String tokenDetails = AirtelUtil.genarateAccessToken();
				token = tokenDetails.split("\\|")[0];
				URL = "https://openapi.airtel.in/iot/api/customer/details/basket/" + basketId + "/sims";

//				iLogger.info("getBasketDetails UrL:" + URL);

				url = new URL(URL);
				HttpURLConnection conn1 = (HttpURLConnection) url.openConnection();
				conn1.setDoOutput(true);
				conn1.setRequestMethod("GET");
				conn1.setRequestProperty("Content-Type", "application/json");
				conn1.setRequestProperty("Accept", "application/json");
				conn1.setRequestProperty("apikey", "SOkYF8XTG7OM6sMAqvT3VFdv3OQ0SQzL");
				conn1.setRequestProperty("customer-id", "16170");
				conn1.setRequestProperty("iv-user", "developer.16170@jcb.com");
				conn1.setRequestProperty("authorization", token);

				iLogger.info("getBasketDetails ::---> HTTP code from Wise :" + conn1.getResponseCode());
				if (conn1.getResponseCode() != 200) {

					iLogger.info("Failed : HTTP error code : " + conn1.getResponseCode());
					System.out.println("Failed : HTTP error code :" + conn1.getResponseCode());
					throw new CustomFault("Failed : HTTP error code : " + conn1.getResponseCode());
				} else {

					br = new BufferedReader(new InputStreamReader((conn1.getInputStream())));
					while ((result = br.readLine()) != null) {
						output = result;
					}
					conn1.disconnect();

					SimDataResponse respObj = new ObjectMapper().readValue(output, SimDataResponse.class);

					String total = respObj.getData().getTotal();

					double limit = Integer.parseInt(total);

					double limit1 = limit / 100;
					int ceil = (int) Math.ceil(limit1);
			
					for (int i = 1; i <= ceil; i++) {
						URL = "https://openapi.airtel.in/iot/api/customer/details/basket/" + basketId + "/sims?pageNo="
								+ i + "&pageSize=100";

					//	iLogger.info("getBasketDetails UrL:" + URL);

						url = new URL(URL);
						HttpURLConnection conn2 = (HttpURLConnection) url.openConnection();
						conn2.setDoOutput(true);
						conn2.setRequestMethod("GET");
						conn2.setRequestProperty("Content-Type", "application/json");
						conn2.setRequestProperty("Accept", "application/json");
						conn2.setRequestProperty("apikey", "SOkYF8XTG7OM6sMAqvT3VFdv3OQ0SQzL");
						conn2.setRequestProperty("customer-id", "16170");
						conn2.setRequestProperty("iv-user", "developer.16170@jcb.com");
						conn2.setRequestProperty("authorization", token);

					//	iLogger.info("getBasketDetails ::---> HTTP code from Wise :" + conn2.getResponseCode());
						iLogger.info(" basketiD :" + basketId + " total count: " + total + " ceil: " + ceil + " i :" +i + " HTTP code ::" + + conn2.getResponseCode() );
						if (conn2.getResponseCode() == 401) {

							tokenDetails = AirtelUtil.genarateAccessToken();
							token = tokenDetails.split("\\|")[0];
							URL = "https://openapi.airtel.in/iot/api/customer/details/basket/" + basketId
									+ "/sims?pageNo=" + i + "&pageSize=100";

							iLogger.info(" Token Issue , So Re-Hitting getBasketDetails UrL:" + URL);

							url = new URL(URL);
							HttpURLConnection conn3 = (HttpURLConnection) url.openConnection();
							conn3.setDoOutput(true);
							conn3.setRequestMethod("GET");
							conn3.setRequestProperty("Content-Type", "application/json");
							conn3.setRequestProperty("Accept", "application/json");
							conn3.setRequestProperty("apikey", "SOkYF8XTG7OM6sMAqvT3VFdv3OQ0SQzL");
							conn3.setRequestProperty("customer-id", "16170");
							conn3.setRequestProperty("iv-user", "developer.16170@jcb.com");
							conn3.setRequestProperty("authorization", token);

							iLogger.info("getBasketDetails ::---> HTTP code from Wise :" + conn3.getResponseCode());

							if (conn3.getResponseCode() != 200) {

								iLogger.info("Failed : HTTP error code : " + conn3.getResponseCode());
								throw new CustomFault("Failed : HTTP error code : " + conn3.getResponseCode());
							} else {

								br = new BufferedReader(new InputStreamReader((conn3.getInputStream())));
								while ((result = br.readLine()) != null) {
									output = result;
								}
								conn3.disconnect();
								SimDataResponse respObj2 = new ObjectMapper().readValue(output, SimDataResponse.class);
								
								for(int i1 = 0 ; i1 < respObj2.getData().getSims().size() ; i1++ ){
									Map<String , String> map = new HashMap<>();
									map.put("status",respObj2.getData().getSims().get(i1).getStatus());
									map.put("IMSI",respObj2.getData().getSims().get(i1).getImsi());
								 	response.put(respObj2.getData().getSims().get(i1).getSimNo(), map )	;
								}
							}
						} else if (conn2.getResponseCode() != 200 && conn2.getResponseCode() != 400) {

							iLogger.info("Failed : HTTP error code : " + conn2.getResponseCode());
							throw new CustomFault("Failed : HTTP error code : " + conn2.getResponseCode());

						} else {

							br = new BufferedReader(new InputStreamReader((conn2.getInputStream())));
							while ((result = br.readLine()) != null) {
								output = result;
							}
							conn2.disconnect();
							SimDataResponse respObj2 = new ObjectMapper().readValue(output, SimDataResponse.class);
							
							for(int i1 = 0 ; i1 < respObj2.getData().getSims().size() ; i1++ ){
								Map<String , String> map = new HashMap<>();
								map.put("status",respObj2.getData().getSims().get(i1).getStatus());
								map.put("IMSI",respObj2.getData().getSims().get(i1).getImsi());
							 	response.put(respObj2.getData().getSims().get(i1).getSimNo(), map )	;
							}

						}

					}
				}

			} else if (connection.getResponseCode() != 200 && connection.getResponseCode() != 400) {
				iLogger.info("Failed : HTTP error code : " + connection.getResponseCode());
				throw new CustomFault("Failed : HTTP error code : " + connection.getResponseCode());

			} else {

				br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
				while ((result = br.readLine()) != null) {
					output = result;
				}
				connection.disconnect();

				SimDataResponse respObj = new ObjectMapper().readValue(output, SimDataResponse.class);

				String total = respObj.getData().getTotal();

				double limit = Integer.parseInt(total);

				double limit1 = limit / 100;
				int ceil = (int) Math.ceil(limit1);

//				iLogger.info(" basketiD :" + basketId + " total count: " + total + " ceil: " + ceil);
				for (int i = 1; i <= ceil; i++) {

					URL = "https://openapi.airtel.in/iot/api/customer/details/basket/" + basketId + "/sims?pageNo=" + i
							+ "&pageSize=100";

				//	iLogger.info("getBasketDetails UrL:" + URL);

					url = new URL(URL);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setDoOutput(true);
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Content-Type", "application/json");
					conn.setRequestProperty("Accept", "application/json");
					conn.setRequestProperty("apikey", "SOkYF8XTG7OM6sMAqvT3VFdv3OQ0SQzL");
					conn.setRequestProperty("customer-id", "16170");
					conn.setRequestProperty("iv-user", "developer.16170@jcb.com");
					conn.setRequestProperty("authorization", token);

//					iLogger.info("getBasketDetails ::---> HTTP code from Wise :" + conn.getResponseCode());
					iLogger.info(" basketiD :" + basketId + " total count: " + total + " ceil: " + ceil + " i :" +i + " HTTP code ::" + + conn.getResponseCode() );
					if (conn.getResponseCode() == 401) {

						String tokenDetails = AirtelUtil.genarateAccessToken();
						token = tokenDetails.split("\\|")[0];
						URL = "https://openapi.airtel.in/iot/api/customer/details/basket/" + basketId + "/sims?pageNo="
								+ i + "&pageSize=100";

						iLogger.info(" Token Issue , So Re-Hitting getBasketDetails UrL:" + URL);
						url = new URL(URL);
						HttpURLConnection conn2 = (HttpURLConnection) url.openConnection();
						conn2.setDoOutput(true);
						conn2.setRequestMethod("GET");
						conn2.setRequestProperty("Content-Type", "application/json");
						conn2.setRequestProperty("Accept", "application/json");
						conn2.setRequestProperty("apikey", "SOkYF8XTG7OM6sMAqvT3VFdv3OQ0SQzL");
						conn2.setRequestProperty("customer-id", "16170");
						conn2.setRequestProperty("iv-user", "developer.16170@jcb.com");
						conn2.setRequestProperty("authorization", token);

						iLogger.info("getBasketDetails ::---> HTTP code from Wise :" + conn2.getResponseCode());

						if (conn2.getResponseCode() != 200) {

							iLogger.info("Failed : HTTP error code : " + conn2.getResponseCode());
							throw new CustomFault("Failed : HTTP error code : " + conn2.getResponseCode());
						} else {

							br = new BufferedReader(new InputStreamReader((conn2.getInputStream())));
							while ((result = br.readLine()) != null) {
								output = result;
							}
							conn2.disconnect();
							SimDataResponse respObj2 = new ObjectMapper().readValue(output, SimDataResponse.class);
							for(int i1 = 0 ; i1 < respObj2.getData().getSims().size() ; i1++ ){
								Map<String , String> map = new HashMap<>();
								map.put("status",respObj2.getData().getSims().get(i1).getStatus());
								map.put("IMSI",respObj2.getData().getSims().get(i1).getImsi());
							 	response.put(respObj2.getData().getSims().get(i1).getSimNo(), map )	;
							}
						}
					} else if (conn.getResponseCode() != 200 && conn.getResponseCode() != 400) {

						iLogger.info("Failed : HTTP error code : " + conn.getResponseCode());
						throw new CustomFault("Failed : HTTP error code : " + conn.getResponseCode());

					} else {

						br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
						while ((result = br.readLine()) != null) {
							output = result;
						}
						conn.disconnect();
						SimDataResponse respObj2 = new ObjectMapper().readValue(output, SimDataResponse.class);
						for(int i1 = 0 ; i1 < respObj2.getData().getSims().size() ; i1++ ){
							Map<String , String> map = new HashMap<>();
							map.put("status",respObj2.getData().getSims().get(i1).getStatus());
							map.put("IMSI",respObj2.getData().getSims().get(i1).getImsi());
						 	response.put(respObj2.getData().getSims().get(i1).getSimNo(), map )	;
						}
						iLogger.info(" basketiD :" + basketId + "  Iteration of i " + i + " response " + response);

					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getBasketDetails :Exception" + e.getMessage());
			fatalLogger.fatal("getBasketDetails :Exception" + e.getMessage());

		} 
		return response;

	}

	public static 	List<ACUResponse> getSimAndVin() {

		List<ACUResponse> respObj = new ArrayList<>();

		Logger infoLogger = InfoLoggerClass.logger;
		Logger fatalLogger = FatalLoggerClass.logger;
		

		String query = "select a.Serial_Number,acu.SIM_No,acu.IMEI_Number  from asset a, asset_control_unit acu  where a.status = 1 and acu.Serial_Number = a.Serial_Number and acu.SIM_No  is not null";
		ConnectMySQL factory = new ConnectMySQL();
		try (Connection conn = factory.getConnection();
				Statement statement = conn.createStatement();
		) {
			
			ResultSet rs = statement.executeQuery(query);
			
			while(rs.next()){
			String vin = rs.getString("Serial_Number");
			String sim = rs.getString("SIM_No");
			String imei = rs.getString("IMEI_Number");

			ACUResponse acuresponse = new ACUResponse();
			acuresponse.setVin(vin);
			acuresponse.setIMEINo(imei);
			acuresponse.setSimNo(sim);

			respObj.add(acuresponse);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("getSimAndVin :Exception " + e.getMessage());
			fatalLogger.fatal("getSimAndVin :Exception " + e.getMessage());

		}

		return respObj;
	}

	public static String insertDataIntoTable(ACUResponse obj) {
		String status ="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		// insert into Airtel_SIM_Data_TEMP (Serial_Number ,Sim_No,IMEI_NO,IMSI_NO,status) values("1234","1234","1234","1234", "Status")
		String insertQuery = "insert into Airtel_SIM_Data_TEMP (Serial_Number ,Sim_No,IMEI_NO,IMSI_NO,status) values(?,?,?,?,?)";//ME10008770.n
	
		iLogger.info("updateDB() insertQuery():: " + insertQuery);
		try (Connection connection = connFactory.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(insertQuery);) {
			
			statement.setString(1, obj.getVin());
			statement.setString(2, obj.getSimNo());
			statement.setString(3, obj.getIMEINo());
			statement.setString(5, obj.getStatus());
			statement.executeUpdate();
			

		} catch (Exception e) {
			System.out.println("insertQuery : "+insertQuery);
			fLogger.fatal("insertQuery : "+insertQuery);
			fLogger.fatal("updateDB()::issue while updating DB "
					+ e.getMessage());
			status="FAILURE";
		}
		return status;
	}
	
	public static String truncateDataFromTable() {
		String query = "TRUNCATE TABLE Airtel_SIM_Data_TEMP";
		String status = "FAILURE";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection
						.createStatement()) {
			statement.executeUpdate(query);
			status="SUCCESS";
		} catch (Exception e) {
			fLogger.fatal("truncateDataFromTable()::issue while deleting data in DB "
					+ e.getMessage());
		}
		iLogger.info("TRUNCATE Airtel_SIM_Data_TEMP Table Status:: " + status);
		return status;
	}
	
	public static String swapDataFromTable() {
		String status="SUCCESS";
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String tableRenameQuery = "RENAME TABLE Airtel_SIM_Data TO Airtel_SIM_Data_OLD, "
				+ "Airtel_SIM_Data_TEMP TO Airtel_SIM_Data, " + "Airtel_SIM_Data_OLD TO Airtel_SIM_Data_TEMP";
		ConnectMySQL connFactory = new ConnectMySQL();
		try (Connection connection = connFactory.getConnection();
				Statement statement = connection
						.createStatement()) {
		statement.execute(tableRenameQuery);
		}catch (Exception e) {
			fLogger.fatal("updateDB()::issue while updating DB "
					+ e.getMessage());
			status="FAILURE";
		}
		iLogger.info("Airtel_SIM_Data_TEMP renamed to Airtel_SIM_Data");
		return status;
	}

	public static  String insertIntoCSVFile() {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		ConnectMySQL connFactory = new ConnectMySQL();
		Connection connection = null;
		String result = null;
		Statement stmt = null;
		Statement stmt1 = null;
		int count = 0;
		int loopFlag = 0;
		try {
			connection = connFactory.getConnection();
			if (connection == null) {
				iLogger.info(" mysql connection" + connection);
				return "Fail Update the Records due to sql connection issues.!";
			} else {

				stmt = connection.createStatement();
				String countQuery = "select count(*) from Airtel_SIM_Data";
				ResultSet rs = stmt.executeQuery(countQuery);

				while (rs.next()) {
					count = rs.getInt("count(*)");

				}

				File file = new File("/data3/JCBLiveLink/AirtelSim/AirtelSimDetails.csv");
				FileWriter outputfile = new FileWriter(file);

				CSVWriter writer = new CSVWriter(outputfile);
				String[] header = { "Serial_Number", "Sim_No","IMEI_NO", "IMSI_NO", "Status"};

				writer.writeNext(header);
				while (loopFlag < count) {
					stmt1 = connection.createStatement();

					String query = "select * from Airtel_SIM_Data limit " + loopFlag + "," + "5000";
					iLogger.info(query);

					ResultSet rs1 = stmt1.executeQuery(query);

					while (rs1.next()) {
						String SerialNumber;
						String Sim_No = null;
						String IMEI_NO = null;
						String IMSI_NO = null;
						String status = null;
						
						SerialNumber = rs1.getString("Serial_Number");
						
						if (rs1.getString("Sim_No") != null) {
							Sim_No = rs1.getString("Sim_No");
						} else {
							Sim_No = "NA";
						}
						if (rs1.getString("IMEI_NO") != null) {
							IMEI_NO = rs1.getString("IMEI_NO");
						} else {
							IMEI_NO = "NA";
						}
						if (rs1.getString("IMSI_NO") != null) {
							IMSI_NO = rs1.getString("IMSI_NO");
						} else {
							IMSI_NO = "NA";
						}
						if (rs1.getString("status") != null) {
							status = rs1.getString("status");
						} else {
							status = "NA";
						}
						
						

						String[] data1 = { SerialNumber,Sim_No,IMEI_NO, IMSI_NO, status };
					
						writer.writeNext(data1);
					}
					loopFlag = loopFlag + 5000;
				}
				writer.close();
			}

			return "Succuss";

		} catch (Exception e) {
			e.printStackTrace();
			fLogger.fatal("Exception e " + e.getMessage());
			return e.getMessage();
		} finally {
			try {

				if (stmt != null)
					stmt.close();

				if (stmt1 != null)
					stmt1.close();

				if (connection != null)
					connection.close();

			}

			catch (SQLException e) {
				fLogger.fatal("SQLException :" + e);
			}
		}

	}



}
