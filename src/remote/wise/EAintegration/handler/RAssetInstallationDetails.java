package remote.wise.EAintegration.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import remote.wise.EAintegration.EAclient.AssetInstallationDetailsClient;
import remote.wise.EAintegration.Qhandler.AssetInstallationDetailsQHandler;
import remote.wise.EAintegration.dataContract.AssetInstallationDetailsInputContract;
import remote.wise.log.FatalErrorLogging.FatalLoggerClass;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.util.HibernateUtil;

public class RAssetInstallationDetails {
	public void reprocessAssetInstallationDetails() {
		Logger fLogger = FatalLoggerClass.logger;

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Logger iLogger = InfoLoggerClass.logger;
		// DF20150603 - Rajani Nagaraju - WISE going down issue - Open a new
		// session when the getCurrentSession returns a dirty session(txns which
		// is not yet committed exists)
		if (session.getTransaction().isActive() && session.isDirty()) {
			iLogger.info("Opening a new session");
			session = HibernateUtil.getSessionFactory().openSession();
		}
		session.beginTransaction();
		String messgId = null;
		String serialNumber = null;
		String installationDate = null;
		String dealerCode = null;
		String customerCode = null;
		String response = null;
		String[] messageSplit = null;
		String fileRef;
		String process = "";
		String reprocess = "RAssetInstallationDetails";

		try {
			ErrorMessageHandler messageHandlerObj = new ErrorMessageHandler();
			List<AssetInstallationDetailsInputContract> inputContractList = new LinkedList<AssetInstallationDetailsInputContract>();

			// get the currentDate
			Date currentDate = new Date();
			Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

			// List all the messages scheduled to be reprocessed for the current
			// time - Reprocess the same in the order of defined sequence of
			// processes
			// DF20180226:KO369761 - group by messageString added in the query
			// because its giving multiple rows with same message.
			Query query = session
					.createQuery("select a.messageId, a.messageString, a.fileName, a.process,"
							+ " a.reprocessJobCode from FaultDetails a, JobDetails b where a.reprocessJobCode = b.reprocessJobCode and "
							+ " a.process = b.process and a.reprocessJobCode = 'RAssetInstallationDetails' and a.reprocessTimeStamp <= '"
							+ currentTimestamp
							+ "' "
							+ " group by a.messageString order by b.process, b.sequence, a.reprocessTimeStamp ");

			Iterator itr = query.list().iterator();
			Object[] result = null;

			while (itr.hasNext()) {
				result = (Object[]) itr.next();

				serialNumber = null;
				installationDate = null;
				dealerCode = null;
				customerCode = null;

				String messageString = result[1].toString();
				messageSplit = messageString.split("\\|");
				int paramSize = messageSplit.length;

				if (paramSize > 0)
					serialNumber = messageSplit[0];
				if (paramSize > 1)
					installationDate = messageSplit[1];
				if (paramSize > 2)
					dealerCode = messageSplit[2];
				if (paramSize > 3)
					customerCode = messageSplit[3];

				// Fill the input contract Object
				AssetInstallationDetailsInputContract inputContractObj = new AssetInstallationDetailsInputContract();
				inputContractObj.setSerialNumber(serialNumber);
				inputContractObj.setInstallationDate(installationDate);
				inputContractObj.setDealerCode(dealerCode);
				inputContractObj.setCustomerCode(customerCode);

				if (result[2] != null)
					inputContractObj.setFileRef(result[2].toString());
				if (result[0] != null) {
					inputContractObj.setMessageId(result[0].toString()+"|R");
					messgId = result[0].toString()+"|R";
				}
				if (result[3] != null)
					inputContractObj.setProcess(result[3].toString());
				if (result[4] != null)
					inputContractObj.setReprocessJobCode(result[4].toString());

				inputContractList.add(inputContractObj);
			}

			for (int i = 0; i < inputContractList.size(); i++) {
				/*
				 * AssetInstallationDetailsClient clientObj = new
				 * AssetInstallationDetailsClient(); response =
				 * clientObj.invokeAssetInstallationService
				 * (inputContractList.get(i));
				 */
				// DF20180207 :@Maniratnam ## Reprocess jobs :: Adding reprocess
				// job to kafka topic
				AssetInstallationDetailsQHandler obj = new AssetInstallationDetailsQHandler();
				obj.handleAssetInstallationToKafkaQueue(
						"AssetInstallationQueue", inputContractList.get(i));

				/*
				 * if(response.equalsIgnoreCase("FAILURE")) {
				 * messageHandlerObj.handleErrorMessages(result[0].toString(),
				 * result[1].toString(), result[2].toString(),
				 * result[3].toString(), result[4].toString()); }
				 */

				// DF20140715 - Rajani Nagaraju - Handle Robust Logging
				/*
				 * if(response.split("-").length>1) { response =
				 * response.split("-")[0].trim();
				 * 
				 * }
				 * 
				 * if(response.equalsIgnoreCase("SUCCESS")) {
				 * messageHandlerObj.deleteErrorMessage
				 * (inputContractList.get(i).getMessageId()); }
				 */
			}

			/*
			 * if(! (session.isOpen() )) { session =
			 * HibernateUtil.getSessionFactory().getCurrentSession();
			 * session.getTransaction().begin(); }
			 */

		}

		catch (Exception e) {
			fLogger.fatal("EA ReProcessing: RAssetInstallation: " + messgId
					+ " Fatal Exception :" + e);
		}

		try {
			// DF20180207 :@Maniratnam ## Reprocess jobs :: handling from file
			String reprocessFolderPath = "";

			String messageString = null;
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream(
					"remote/wise/resource/properties/configuration.properties"));
			reprocessFolderPath = prop.getProperty("EA_reprocessingFolderPath");

			String pattern = "AssetInstallation" + "*.txt";

			FileFilter fileFilter = new WildcardFileFilter(pattern);

			File reprocessFolder = new File(reprocessFolderPath);

			File[] reprocessFiles = reprocessFolder.listFiles(fileFilter);

			FileReader fileReader = null;

			BufferedReader bufferedReader = null;

			for (int j = 0; j < reprocessFiles.length; j++) {
				int i = 0;

				// get each file

				File reprocessFile = reprocessFiles[j];
				iLogger.info("RAssetInstallation:: from files with file name:"
						+ reprocessFile);
				if (reprocessFile.getName().split("_").length > 2)

					fileRef = reprocessFile.getName().split("_")[0]
							+ reprocessFile.getName().split("_")[1];

				else

					fileRef = reprocessFile.getName().substring(0,
							reprocessFile.getName().lastIndexOf("."));

				fileReader = new FileReader(reprocessFile);

				bufferedReader = new BufferedReader(fileReader);

				while ((messageString = bufferedReader.readLine()) != null) {

					messageSplit = messageString.split("\\|");

					int paramSize = messageSplit.length;

					if (paramSize > 0)
						serialNumber = messageSplit[0];
					if (paramSize > 1)
						installationDate = messageSplit[1];
					if (paramSize > 2)
						dealerCode = messageSplit[2];
					if (paramSize > 3)
						customerCode = messageSplit[3];

					i++;

					// Fill the input contract Object

					AssetInstallationDetailsInputContract inputContractObj = new AssetInstallationDetailsInputContract();

					inputContractObj.setDealerCode(dealerCode);

					inputContractObj.setCustomerCode(customerCode);

					inputContractObj.setSerialNumber(serialNumber);

					inputContractObj.setFileRef(fileRef);

					inputContractObj.setMessageId("MSG" + fileRef + i + "|R");

					inputContractObj.setProcess(process);

					inputContractObj.setReprocessJobCode(reprocess);

					AssetInstallationDetailsQHandler installationQhandler = new AssetInstallationDetailsQHandler();
					installationQhandler.handleAssetInstallationToKafkaQueue(
							"AssetInstallationQueue", inputContractObj);
				}
				bufferedReader.close();
				reprocessFile.delete();

			}

		} catch (Exception e)

		{

			fLogger.fatal("EA ReProcessing: RAssetInstallation: " + messgId
					+ " Fatal Exception :" + e);

		}

		finally {
			// DF20150508 - Rajani Nagaraju - Addig try catch around commit
			try {
				if (session.isOpen())
					if (session.getTransaction().isActive()) {
						session.getTransaction().commit();
					}
			} catch (Exception e2) {
				fLogger.fatal("Exception :" + e2);
			}

			if (session.isOpen()) {
				session.flush();
				session.close();
			}

		}
	}
}
