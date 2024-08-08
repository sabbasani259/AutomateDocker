package com.wipro.MachineDown;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.Logger;

import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.ConnectMySQL;

public class MachineDownInterfaceImpl implements MachineDownInterface {

	private static ConnectMySQL connFactory = new ConnectMySQL();

	@Override
	public MachineDownEntity fieldValidation(String[] msgString)
			throws MachineDownCustomFault {
		// TODO Auto-generated method stub

		MachineDownEntity MachineDownEntity = new MachineDownEntity();

		if (msgString.length > 0) {
			MachineDownEntity.setB1CallId(msgString[0]);
		}
		if (msgString.length > 1) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",
						Locale.ENGLISH);

				Date date = formatter.parse(msgString[1]);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				String year = String.valueOf(cal.get(Calendar.YEAR));
				String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
				System.out.println(month.length());
				if (month.length() < 2) {
					month = "0" + month;
				}
				String dateStr = formatter.format(date);

				MachineDownEntity.setCreatedDate(dateStr);

				MachineDownEntity.setPartitionKey(Integer
						.parseInt(year + month));

				MachineDownEntity.setLastUpdated(new SimpleDateFormat(
						"dd-MM-yyyy HH:MM:ss").format(new Date()));

			} catch (Exception E) {
				E.printStackTrace();
				throw new MachineDownCustomFault("Invalid Date Format "
						+ msgString[1] + " Expected Date format is dd/MM/yyyy");
			}
		}
		if (msgString.length > 2) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("HH:MM:SS",
						Locale.ENGLISH);
				Date date = formatter.parse(msgString[2]);
				String dateStr = formatter.format(date);
				MachineDownEntity.setCreatedTime(dateStr);
			} catch (Exception E) {
				E.printStackTrace();
				throw new MachineDownCustomFault("Invalid Time Format "
						+ msgString[2] + " Expected Date format is HH:MM:SS");
			}

		}
		if (msgString.length > 3) {
			MachineDownEntity.setServiceCall(msgString[3]);
		}
		if (msgString.length > 4) {
			MachineDownEntity.setDescription(msgString[4]);
		}
		if (msgString.length > 5) {
			MachineDownEntity.setStatus(msgString[5]);
		}
		if (msgString.length > 6) {
			MachineDownEntity.setCustomerName(msgString[6]);
		}
		if (msgString.length > 7) {
			MachineDownEntity.setMobileNumber(msgString[7]);
		}
		if (msgString.length > 8) {
			MachineDownEntity.setMachineNumber(msgString[8]);
		}
		if (msgString.length > 9) {
			MachineDownEntity.setModel(msgString[9]);
		}
		if (msgString.length > 10) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY",
						Locale.ENGLISH);
				Date date = formatter.parse(msgString[10]);
				String dateStr = formatter.format(date);
				MachineDownEntity.setAssignmentDate(dateStr);
			} catch (Exception E) {
				E.printStackTrace();
				throw new MachineDownCustomFault(
						"Invalid Date Format for AssignmentDate :"
								+ msgString[10]
								+ " Expected Date format is dd/MM/YYYY");
			}
		}
		if (msgString.length > 11) {

			try {
				SimpleDateFormat formatter = new SimpleDateFormat("HH:MM:SS",
						Locale.ENGLISH);
				Date date = formatter.parse(msgString[11]);
				String dateStr = formatter.format(date);
				MachineDownEntity.setAssignmentTime(dateStr);
			} catch (Exception E) {
				E.printStackTrace();
				throw new MachineDownCustomFault(
						"Invalid Time Format for AssignmentTime :"
								+ msgString[11]
								+ " Expected Date format is HH:MM:SS");
			}

		}

		if (msgString.length > 12) {
			MachineDownEntity.setServiceType(msgString[12]);
		}
		if (msgString.length > 13) {
			MachineDownEntity.setCallType(msgString[12]);
		}
		if (msgString.length > 14) {
			MachineDownEntity.setCallSubType(msgString[14]);
		}
		if (msgString.length > 15) {
			MachineDownEntity.setDealer(msgString[15]);
		}
		if (msgString.length > 16) {
			MachineDownEntity.setAssignedEngName(msgString[16]);
		}
		if (msgString.length > 17) {
			MachineDownEntity.setMachineStatus(msgString[17]);
		}
		if (msgString.length > 18) {
			MachineDownEntity.setCallHMR(msgString[18]);
		}
		if (msgString.length > 19) {
			MachineDownEntity.setRemarks(msgString[19]);
		}

		return MachineDownEntity;
	}

	@Override
	public String processMachineDownData(File processingFile,
			String archivedFolderPath, String process, String errorFolderPath) {

		String status = "FAILURE";
		Logger iLogger = InfoLoggerClass.logger;
		Logger fLogger = FatalLoggerClass.logger;
		File archivedFile = null;
		File errorFile = null;
		BufferedReader br = null;
		// move the file to archived folder if the file is already processed
		String machineDownEaLogQuery = "select * from MachineDown_ea_log_details where "
				+ "File_Name='"
				+ processingFile.getName().substring(0,
						processingFile.getName().lastIndexOf(".")) + "'";

		int duplicateFile = 0;

		try (Connection connection = connFactory.getConnection_tracebility();
				PreparedStatement statement = connection
						.prepareStatement(machineDownEaLogQuery);
				ResultSet rs = statement.executeQuery(machineDownEaLogQuery);) {

			if (rs.next()) {
				duplicateFile = 1;
			}

			if (duplicateFile == 1) {

				iLogger.info("WISE:MachineDownInterface - " + "File "
						+ processingFile.getName()
						+ " is already processed and hence skipping the same");

				// move the file to archived folder
				archivedFile = new File(archivedFolderPath,
						processingFile.getName());

				if (archivedFile.exists())
					archivedFile.delete();
				boolean moveStatus = processingFile.renameTo(archivedFile);

				if (moveStatus)
					status="SUCCESS";
					iLogger.info("WISE:MachineDownInterface - "
							+ "File "
							+ processingFile.getName()
							+ " is moved successfully from processing to archived folder");

				return status;

			}

		} catch (Exception E) {
			E.printStackTrace();

			errorFile = new File(errorFolderPath, processingFile.getName());

			if (errorFile.exists())
				errorFile.delete();
			boolean moveStatus = processingFile.renameTo(errorFile);

			if (moveStatus)
				iLogger.info("WISE:MachineDownInterface - "
						+ "File "
						+ processingFile.getName()
						+ " is moved successfully from processing to error folder");

		} finally {
			try {

				br = new BufferedReader(new FileReader(processingFile));
				String line = br.readLine();
				String msgStringlocal = line;
				int runningNumber = 1;
				int failureStatus = 0;
				int fileDataCount = 0;
				int failureCounter = 0;

				String fileName = processingFile.getName().substring(0,
						processingFile.getName().lastIndexOf("."));
				if (fileName.length() >= 200) {
					fileName = fileName.substring(0, 200);
				}

				while (line != null) {

					try {

						if (!(line.trim() != null && line.trim().length() > 0)) {
							line = br.readLine();
							continue;
						}

						String[] msgString = line.split("\\|");

						// check if the machine number is a registered machine
						// number
						String assetValidationQuery = "";
						if (msgString.length > 8) {
							assetValidationQuery = "select * from asset where "
									+ "Serial_Number like '%" + msgString[8]
									+ "%'";
						}
						try (Connection connection0 = connFactory
								.getConnection();
								PreparedStatement statement0 = connection0
										.prepareStatement(assetValidationQuery);
								ResultSet rs = statement0
										.executeQuery(assetValidationQuery);) {

							if (rs.next()) {
								runningNumber++;
								fileDataCount++;

								MachineDownEntity machineDownEntityObj = fieldValidation(msgString);

								status = WriteMachineDownData(
										machineDownEntityObj, connFactory);
								line = br.readLine();
								if (status.equalsIgnoreCase("FAILURE")) {
									failureStatus = 1;
									failureCounter++;
								}

								

								if (runningNumber > 1) {

									try {
										insertInterfaceLogDetails(fileName,
												fileDataCount);

									}

									catch (Exception e) {
										fLogger.fatal("WISE:MachineDownInterface - "
												+ processingFile.getName()
												+ ": Exception :" + e);
									}

								}

								if (failureStatus == 0) {
									// move the file to archived folder
									archivedFile = new File(archivedFolderPath,
											processingFile.getName());

									if (archivedFile.exists())
										archivedFile.delete();
									boolean moveStatus = processingFile
											.renameTo(archivedFile);

									if (moveStatus)
										iLogger.info("WISE:MachineDownInterface - "
												+ "File "
												+ processingFile.getName()
												+ " is moved successfully from processing to archived folder");
								}
							} else {
								line = br.readLine();
								fileDataCount++;
								throw new MachineDownCustomFault(msgString[8]
										+ " is not a registered machine number");
							}

						} catch (MachineDownCustomFault mdc) {
							line = br.readLine();
							failureCounter++;
							mdc.printStackTrace();
							fLogger.fatal("Custom Fault Exception "
									+ mdc.getMessage());
							String insertfaultQueryea = "insert into MachineDown_fault_details (File_Name,"
									+ "Message_String,Failure_Counter,Failure_Cause) values(";

							insertfaultQueryea = insertfaultQueryea + "'"
									+ fileName + "'" + "," + "'"
									+ msgStringlocal + "'" + "," + "'"
									+ failureCounter + "'" + "," + "'"
									+ mdc.getMessage() + "'" + ")"

							;
							try (Connection connection3 = connFactory
									.getConnection_tracebility();
									PreparedStatement statement3 = connection3
											.prepareStatement(insertfaultQueryea);) {
								statement3.executeUpdate(insertfaultQueryea);

							} catch (Exception e) {
								e.printStackTrace();

							}
						}

					}

					catch (IOException e) {
						line = br.readLine();
						errorFile = new File(errorFolderPath,
								processingFile.getName());

						if (errorFile.exists())
							errorFile.delete();
						boolean moveStatus = processingFile.renameTo(errorFile);

						if (moveStatus)
							iLogger.info("WISE:MachineDownInterface - "
									+ "File "
									+ processingFile.getName()
									+ " is moved successfully from processing to error folder");
						fLogger.fatal("WISE:MachineDownInterface - "
								+ processingFile.getName() + " :Exception :"
								+ e);
					}

					catch (Exception e) {
						line = br.readLine();
						errorFile = new File(errorFolderPath,
								processingFile.getName());

						if (errorFile.exists())
							errorFile.delete();
						boolean moveStatus = processingFile.renameTo(errorFile);

						if (moveStatus)
							iLogger.info("WISE:MachineDownInterface - "
									+ "File "
									+ processingFile.getName()
									+ " is moved successfully from processing to error folder");
						fLogger.fatal("WISE:MachineDownInterface - "
								+ processingFile.getName() + " :Exception :"
								+ e);
					}
				}

				if (failureStatus == 0) {
					// move the file to archived folder
					if (runningNumber > 1) {

						try {
							insertInterfaceLogDetails(fileName,
									fileDataCount);

						}

						catch (Exception e) {
							fLogger.fatal("WISE:MachineDownInterface - "
									+ processingFile.getName()
									+ ": Exception :" + e);
						}

					}
//					archivedFile = new File(archivedFolderPath,
//							processingFile.getName());
//
//					if (archivedFile.exists())
//						archivedFile.delete();
//					boolean moveStatus = processingFile.renameTo(archivedFile);
//				
//					if (moveStatus)
//						iLogger.info("WISE:MachineDownInterface - "
//								+ "File "
//								+ processingFile.getName()
//								+ " is moved successfully from processing to archived folder");
				}
			} catch (Exception e) {

				e.printStackTrace();
				errorFile = new File(errorFolderPath, processingFile.getName());

				if (errorFile.exists())
					errorFile.delete();
				boolean moveStatus = processingFile.renameTo(errorFile);

				if (moveStatus)
					iLogger.info("WISE:MachineDownInterface - "
							+ "File "
							+ processingFile.getName()
							+ " is moved successfully from processing to error folder");
				fLogger.fatal("WISE:MachineDownInterface - "
						+ processingFile.getName() + " :Exception :" + e);
			} finally {
				if (br != null)
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block

						errorFile = new File(errorFolderPath,
								processingFile.getName());

						if (errorFile.exists())
							errorFile.delete();
						boolean moveStatus = processingFile.renameTo(errorFile);

						if (moveStatus)
							iLogger.info("WISE:MachineDownInterface - "
									+ "File "
									+ processingFile.getName()
									+ " is moved successfully from processing to error folder");
						fLogger.fatal("WISE:MachineDownInterface - "
								+ processingFile.getName() + " :Exception :"
								+ e);
					}

			}

		}

		return status;

	}

	@Override
	public String WriteMachineDownData(MachineDownEntity machineDownEntity,
			ConnectMySQL connFactory) throws ParseException {
		Logger fLogger = FatalLoggerClass.logger;
		Logger iLogger = InfoLoggerClass.logger;
		String Status = "FAILURE";

		String insertQuery = "insert into MachineDown (PartitionKey,"
				+ "B1CallId,CreatedDate,CreatedTime,AssignmentDate,"
				+ "AssignmentTime,ServiceCall,Description,Status,"
				+ "CustomerName,MobileNumber,MachineNumber,Model,"
				+ "ServiceType,CallType,CallSubType,Dealer,AssignedEngName,"
				+ "MachineStatus,CallHMR,Remarks,LastUpdated) values(";

		insertQuery = insertQuery + "'" + machineDownEntity.getPartitionKey()
				+ "'" + "," + "'" + machineDownEntity.getB1CallId() + "'" + ","
				+ "STR_TO_DATE(" + "'" + machineDownEntity.getCreatedDate()
				+ "'" + ", '%d/%m/%Y')" + "," + "'"
				+ machineDownEntity.getCreatedTime() + "'" + ","
				+ "STR_TO_DATE(" + "'" + machineDownEntity.getAssignmentDate()
				+ "'" + ", '%d/%m/%Y')" + "," + "'"
				+ machineDownEntity.getAssignmentTime() + "'" + "," + "'"
				+ machineDownEntity.getServiceCall() + "'" + "," + "'"
				+ machineDownEntity.getDescription() + "'" + "," + "'"
				+ machineDownEntity.getStatus() + "'" + "," + "'"
				+ machineDownEntity.getCustomerName() + "'" + "," + "'"
				+ machineDownEntity.getMobileNumber() + "'" + "," + "'"
				+ machineDownEntity.getMachineNumber() + "'" + "," + "'"
				+ machineDownEntity.getModel() + "'" + "," + "'"
				+ machineDownEntity.getServiceType() + "'" + "," + "'"
				+ machineDownEntity.getCallType() + "'" + "," + "'"
				+ machineDownEntity.getCallSubType() + "'" + "," + "'"
				+ machineDownEntity.getDealer() + "'" + "," + "'"
				+ machineDownEntity.getAssignedEngName() + "'" + "," + "'"
				+ machineDownEntity.getMachineStatus() + "'" + "," + "'"
				+ machineDownEntity.getCallHMR() + "'" + "," + "'"
				+ machineDownEntity.getRemarks() + "'" + "," + "STR_TO_DATE("
				+ "'" + machineDownEntity.getLastUpdated() + "'"
				+ ", '%d-%m-%Y %H:%i:%s')" + ")"

		;

		System.out
				.println("WISE:MachineDownInterface - machineDownEntity insertQuery : "
						+ insertQuery);
		try (Connection connection = connFactory.getConnection_tracebility();
				PreparedStatement statement = connection
						.prepareStatement(insertQuery);) {
			statement.executeUpdate(insertQuery);
			Status = "SUCCESS";

		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("WISE:MachineDownInterface - machineDownEntity insertQuery : "
							+ insertQuery);
			fLogger.fatal("WISE:MachineDownInterface - machineDownEntity insertQuery : "
					+ insertQuery);
			fLogger.fatal("WISE:MachineDownInterface -updateDB()::issue while updating DB "
					+ e.getMessage());
			Status = "FAILURE";
		}
		return Status;
	}

	@Override
	public String insertInterfaceLogDetails(String fName, int totalCount) {

		String Status = "FAILURE";

		String machineDownEaLogQuery = "select * from MachineDown_ea_log_details where "
				+ "File_Name='" + fName + "'";

		try (Connection connection = connFactory.getConnection_tracebility();
				PreparedStatement statement = connection
						.prepareStatement(machineDownEaLogQuery);
				ResultSet rs = statement.executeQuery(machineDownEaLogQuery);) {

			if (rs.next()) {

				String sql = "UPDATE MachineDown_ea_log_details "
						+ "SET No_Of_Records = '" + totalCount
						+ "' WHERE File_Name= '" + fName + "'";
				try (Connection connection1 = connFactory
						.getConnection_tracebility();
						PreparedStatement statement1 = connection
								.prepareStatement(sql);

				) {
					statement.executeUpdate(sql);
					Status = "SUCCESS";
				} catch (Exception e) {
					e.printStackTrace();
					Status = "FAILURE";
				}

			} else {
				String insertQueryea = "insert into MachineDown_ea_log_details (File_Name,"
						+ "Process,Processed_Timestamp,No_Of_Records) values(";

				insertQueryea = insertQueryea + "'" + fName + "'" + "," + "'"
						+ "MachineDown" + "'" + "," + "'"
						+ new Timestamp(new Date().getTime()) + "'" + "," + "'"
						+ totalCount + "'" + ")"

				;
				try (Connection connection2 = connFactory
						.getConnection_tracebility();
						PreparedStatement statement2 = connection
								.prepareStatement(insertQueryea);) {
					statement.executeUpdate(insertQueryea);
					Status = "SUCCESS";
				} catch (Exception e) {
					e.printStackTrace();
					Status = "FAILURE";
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			Status = "FAILURE";
		}
		return Status;
	}

}
