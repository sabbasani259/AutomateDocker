package remote.wise.handler;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.Logger;

import remote.wise.EAintegration.Qhandler.AssetGateOutQConsumer;
import remote.wise.EAintegration.Qhandler.AssetGroupQConsumer;
import remote.wise.EAintegration.Qhandler.AssetInstallationDetailsQConsumer;
import remote.wise.EAintegration.Qhandler.AssetPersonalityQConsumer;
import remote.wise.EAintegration.Qhandler.AssetTypeQConsumer;
import remote.wise.EAintegration.Qhandler.DealerInfoQConsumer;
import remote.wise.EAintegration.Qhandler.DealerMappingQConsumer;
import remote.wise.EAintegration.Qhandler.EngineTypeQConsumer;
import remote.wise.EAintegration.Qhandler.ExtendedWarrantyQConsumer;
import remote.wise.EAintegration.Qhandler.JCBRollOffQConsumer;
import remote.wise.EAintegration.Qhandler.MasterServiceScheduleQConsumer;
import remote.wise.EAintegration.Qhandler.PrimaryDealerTransferQConsumer;
import remote.wise.EAintegration.Qhandler.SaleFromD2CConsumer;
import remote.wise.EAintegration.Qhandler.ServiceHistoryQConsumer;
import remote.wise.EAintegration.Qhandler.ZonalInformationQConsumer;
import remote.wise.log.InfoLogging.InfoLoggerClass;


public class KafkaConsumerListener implements ServletContextListener {

	private ServletContext context = null;

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
		iLogger.info("SMSHandler:KafkaListener:Servlet Context for Kafka Consumers Destroyed successfully ");
		this.context = null;

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {/*
		// TODO Auto-generated method stub
		Logger iLogger = InfoLoggerClass.logger;
//		iLogger.debug("SMSHandler:KafkaListener:Servlet Context Initialization - START");
		this.context = event.getServletContext();
//		iLogger.debug("SMSHandler:KafkaListener:Servlet Context Initialization - END");

		//Invoke the Listener on SMSQ Topic as a separate thread that runs continuously
		iLogger.info("SMSHandler:KafkaListener:Initialize Listener on SMSQ Topic");
		new SMSQConsumer();

		//Invoke the Listener EmailQ Topic as a separate thread that runs continuously
		iLogger.info("EmailHandler:KafkaListener:Initialize Listener on EmailQ Topic");
		new EmailQConsumer();
		
		//Invoke the Listener SaleFromD2CQ Topic as a separate thread that runs continuously
		iLogger.info("ServiceHistoryHandler:KafkaListener:Initialize Listener on ServiceHistoryQ Topic");
		new ServiceHistoryQConsumer();
		
		//Invoke the Listener JCBRollOffQ Topic as a separate thread that runs continuously
		iLogger.info("JCBRollOffHandler:KafkaListener:Initialize Listener on JCBRollOffQ Topic");
		new JCBRollOffQConsumer();

		//Invoke the Listener AssetGateOutQ Topic as a separate thread that runs continuously
		iLogger.info("AssetGateOutHandler:KafkaListener:Initialize Listener on AssetGateOutQ Topic");
		new AssetGateOutQConsumer();

		//Invoke the Listener AssetGroupQ Topic as a separate thread that runs continuously
		iLogger.info("AssetGroupHandler:KafkaListener:Initialize Listener on AssetGroupQ Topic");
		new AssetGroupQConsumer();

		//Invoke the Listener AssetTypeQ Topic as a separate thread that runs continuously
		iLogger.info("AssetTypeHandler:KafkaListener:Initialize Listener on AssetTypeQ Topic");
		new AssetTypeQConsumer();

		//Invoke the Listener AssetPersonalityQ Topic as a separate thread that runs continuously
		iLogger.info("AssetPersonalityHandler:KafkaListener:Initialize Listener on AssetPersonalityQ Topic");
		new AssetPersonalityQConsumer();

		//Invoke the Listener AssetInstallationDetailsQ Topic as a separate thread that runs continuously
		iLogger.info("AssetInstallationDetailsHandler:KafkaListener:Initialize Listener on AssetInstallationDetailsQ Topic");
		new AssetInstallationDetailsQConsumer();

		//Invoke the Listener DealerInfoQ Topic as a separate thread that runs continuously
		iLogger.info("DealerInfoHandler:KafkaListener:Initialize Listener on DealerInfoQ Topic");
		new DealerInfoQConsumer();

		//Invoke the Listener DealerMappingQ Topic as a separate thread that runs continuously
		iLogger.info("DealerMappingHandler:KafkaListener:Initialize Listener on DealerMappingQ Topic");
		new DealerMappingQConsumer();

		//Invoke the Listener EngineTypeQ Topic as a separate thread that runs continuously
		iLogger.info("EngineTypeHandler:KafkaListener:Initialize Listener on EngineTypeQ Topic");
		new EngineTypeQConsumer(); 

		//Invoke the Listener MasterServiceScheduleQ Topic as a separate thread that runs continuously
		iLogger.info("MasterServiceScheduleHandler:KafkaListener:Initialize Listener on MasterServiceScheduleQ Topic");
		new MasterServiceScheduleQConsumer();

		//Invoke the Listener PrimaryDealerTransferQ Topic as a separate thread that runs continuously
		iLogger.info("PrimaryDealerTransferHandler:KafkaListener:Initialize Listener on PrimaryDealerTransferQ Topic");
		new PrimaryDealerTransferQConsumer();

		//Invoke the Listener SaleFromD2CQ Topic as a separate thread that runs continuously
		iLogger.info("SaleFromD2CHandler:KafkaListener:Initialize Listener on SaleFromD2CQ Topic");
		new SaleFromD2CConsumer();

		//Invoke the Listener ZonalInformationQ Topic as a separate thread that runs continuously
		iLogger.info("ZonalInformationHandler:KafkaListener:Initialize Listener on ZonalInformationQ Topic");
		new ZonalInformationQConsumer();
		
		//DF20191220:Abhishek:: QConsumer for extended warranty.
		//Invoke the Listener ExtendedWarrantyQ Topic as a separate thread that runs continuously
		iLogger.info("ExtendedWarrantyServiceHandler:KafkaListener:Initialize Listener on ExtendedWarrantyServiceQ Topic");
		new ExtendedWarrantyQConsumer();
	*/}

}
