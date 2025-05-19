/*
 * CR321-20220712-Vidya SagarM-CombineToFotaReport to combine required fields to wisetraceability schema .
 * CR353 - 20221214 - Mahesh Sahu - Live Link Premium changes
 * CR366 - 20221214 - Laxhmi Prashad - Alerts closure
 * CR407 20230315 SootMonitorReport mahesh
 * JCB6337 : Dhiraj Kumar : 20230405 : Tenancy Bridge population logic into WISE
 * CR352 : 20230505 : Dhiraj K : Retrofitment Changes
 * CR417 : 20230523 : Mahesh Sahu : DTC report masking specific DTC's
 * ME100008333 : 20230704 : Dhiraj K : Test method added for Hibernate connection
 * CR416 : 20230713 : Prasanna lakshmi : TxnData Download for Log Packet on Eng server
 * CR416 : 20230724 : Prasanna lakshmi : PTPackect Download for Log Packet on Eng server
 * CR432 : 20230726 : Prasanna lakshmi : GeoFence/TimeFence API Changes
 * CR419 :Santosh : 20230714 :Aemp Changes
 * CR397 : 20230831 : Dhiraj Kumar : StageV VD development changes
 * CR434 : 20231006 : Dhiraj Kumar : MOSP Software Id Report Changes
 * 20231127 CR452-AirtelApiIntegration-prasad
 * CR448 : 20231127 : prasad : Model Wise Distribution on the Overview Map 
 * CR424 : 20231127 : prasad : Genset CPCB4+ for Stage V machines 
 * JCB6622 : 20240805 : Dhiraj : Email flooding issue
 *
 */

package com.wipro.mcoreapp.webservice;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import remote.wise.AutoReportScheduler.AutoReportReprocessService;
import remote.wise.AutoReportScheduler.AutoReportService;
import remote.wise.EAintegration.handler.ExtendedWarrantyService;
import remote.wise.service.implementation.ETLfactDataJsonService;
import remote.wise.service.webservice.*;

import com.wipro.MachineBlocking.GetMachineRenewalStatus;
import com.wipro.mda.ReprocessDAIngestionService;

/**
 * <h1>RestApplication</h1>
 * <p>
 * Base Class that extends JAX-RS Application class for defining the list of REST services that will be mapped to the application 'WISE'
 *  
 * @author Rajani Nagaraju
 * @version 1.0
 * @since   2015-07-15
 *
 */
//CR308 : DHIRAJ K : 20220426 : Beyond Warranty closures from the web potal for MA users
//CR344 : VidyaSagarM : 20220817 : UnknownDTCFetchData from mysql 
//CR366 : Prasad :20230111 : Auto Closure Service Alerts 
@ApplicationPath("/WISE")
public class RestApplication extends Application  
{
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();

	/**
	 * Constructor that adds the list of REST Services to the Singleton set
	 */
	public RestApplication()
	{
		singletons.add(new AlertSubscription());
		singletons.add(new SubscriberContact());

		singletons.add(new HelloWorldResource());
		singletons.add(new SMSListernerService());
		singletons.add(new HAJsendSMSpktService());
		singletons.add(new DayAggMigrationService());
		//DF20160502 @Roopa Adding model service tenancy service in the generic REST Application
		singletons.add(new AccountTenancyService());
		singletons.add(new ProfileCodeService());

		singletons.add(new AssetMigration());
		//DFID20160506 @Kishore Adding City service for fetching the city id and city name by passing the state ID
		singletons.add(new CityRESTService());

		//DF20160725 @Roopa Adding dynamic table creation service for AMH, AMD and AMDE

		singletons.add(new DynamicAMH_AMD_AMDE_TableCreationRESTService());

		//DF20160725 @Roopa Adding dynamic table purge service for AMH, AMD and AMDE
		singletons.add(new DynamicAMH_AMD_AMDE_PurgeRESTService());


		//DF20160725 @Roopa Adding AlertDetailsRESTService which returns red, yellow, service due, overdue and individual alert count
		singletons.add(new AlertDetailsRESTService());

		//DF20160725 @Roopa Adding AlertDashBoardRESTService which returns  Active alerts

		singletons.add(new AlertDashBoardRESTService());


		//DF20160823 @Roopa Adding AlertReprocessRESTService which re process all the alerts from the alert packet details table
		singletons.add(new AlertReprocessRESTService());

		//DF20160826 @Suresh 
		singletons.add(new DayAggMigrationService());
		singletons.add(new AssetServiceScheduleRestService());
		singletons.add(new UpdateAssetEventAddressRest());

		//DF20170208 @Ajay for Asset event address updation(Logic change-> Get all the records for the given day)
		singletons.add(new AELocationService());

		//DF20170314 @Roopa for New ETL service to fetch data from TAssetMonTable
		singletons.add(new ETLfactDataJsonService());

		//********** Services for NON-DBMS implementations **********
		//DF20170717 @Ajay Creating new NON-DMS customer customer
		singletons.add(new CustomerInfoService());
		//DF20170609 @SU334449 for validating Customer details from Customer Master
		singletons.add(new ValidateCustomerDetailsRESTService());
		//DF20170609 @SU334449 for fetching DealerECC_Code details from Customer Master for non-DBMS users
		singletons.add(new CustomersUnderECCDealerRESTService());
		//DF20170628 @SU334449 Get List of all ECC Dealers
		singletons.add(new ECCDealersRESTService());
		//DF20170629 @SU334449 Get List of all Service Name and DBMS Part Code for the VIN number
		singletons.add(new VINServiceDetailsRESTService());
		//DF20170704 @SU334449 Invoking Service History Implementation
		singletons.add(new ServiceHistoryDetailsRESTService());
		//********** End of Services for NON-DBMS implementations **********

		//********* Services for SAARC Changes implementations ***********
		//DF20170607 @SU334449 for Region Service implementation
		singletons.add(new RegionService());
		//********** End of Services for SAARC Changes implementations *********

		//DF20170713 @KO369761 Script to store summary service data in a new fleet_summary_chart_tempdata table 
		singletons.add(new FleetSummaryDetailsRESTService());
		//DF20170811 @SU334449 Service to download Monitoring Parameters
		singletons.add(new DownloadMonitoringParameterRESTService());


		//Df20171011 @Roopa Schedular service to unlock the user account(who has used invalid password  > 5 times)
		singletons.add(new UserAccountUnlockRESTService());

		//DF20171108 KO369761 - Service to close SOS alerts after 24 hours.
		singletons.add(new SOSAlertClosureRestService());

		//Df20171129 @Koti VIsualization Dashboard service's for BHL model
		singletons.add(new BHLChartandPeriodRESTService());
		singletons.add(new BHLLifeReportRESTService());
		singletons.add(new BHLDateReportRESTService());

		//Df20171129 @Roopa VIsualization Dashboard service's for Excavator model
		singletons.add(new ExcavatorDashboardRESTService());
		singletons.add(new StartAndEndEngineRunHrsRESTService());

		//DF20171206 @koti REST Service for updating static hashmap(Map Overview Cache)
		singletons.add(new MapOverviewCacheRESTService());
		singletons.add(new CountryCodeRESTService());

		//By maniratnam ::: for sms Log details
		singletons.add(new SmsLogDetailsService());

		//DF20180202 @KO369761 REST Services for LiveLink Renewal Machine Services.
		singletons.add(new AssetRenewalRESTService());
		singletons.add(new DealerLLIRenewalReportService());
		singletons.add(new LLIRenewalAdminReportService());
		//DF20180202 @KO369761 : to block the vins which renewal date is expired.
		singletons.add(new BlockLLSubscriptionExpiredVINService());

		//********** WISE & MOOL DA Integration Implementations **********
		//DF20180105 @SU334449 - REST Service to re-process the Ownership and Profile Details from Moolda_Fault table
		singletons.add(new ReprocessDAIngestionService());

		//Df20180117 @Roopa Multiple BP code changes
		singletons.add(new MultipleBPcodeMappingRESTService());
		//DF20180309 @Mani retrofitinstallation
		singletons.add(new RetrofitInstallationservice());
		singletons.add(new GetDecryptedUserIdService());
		//DF20180313 @Mani :: To get countrycode for accountcode
		singletons.add(new CountryCodeforAccountCodeService());

		//Kiran: for API gateway calls
		singletons.add(new APIGateWayRestCall());

		//DF20180316 @Roopa MADashboard Changes
		singletons.add(new MADashBoardDetailsRESTService());

		//DF20180403 @Roopa to get Linked account code's for the given mapping code
		singletons.add(new LinkedAccountCodeService());


		//DF20180326 @Mani :: Notification Subscriber report.
		singletons.add(new SubscriberReportService());
		
		//DF20180316 @KO369761 SOS Alert Functionality.
		singletons.add(new SOSAlertCategoryRestService());
		singletons.add(new SOSAlertsRestService());
		
		//DF20180316 @KO369761 Service for Interface Reprocess functionality from UI.
		singletons.add(new EAReprocessRESTService());
		
		//DF20180316 @KO369761 Service for sending dealer ECC details(NON DBMS Sale from UI).
		singletons.add(new SellerDetailsRESTService());
		
		singletons.add(new ApplicationAlertClosureRESTService());
		singletons.add(new FotaTrackService());
		
		//DF20180628 - Rajani Nagaraju - Implementation for Auto Reporting
        singletons.add(new AutoReportService());
        singletons.add(new AutoReportReprocessService());
        
		//DF20180316 @KO369761 Service for get fuel utilization details for week period.
		singletons.add(new FuelUtilizationDetailsRESTService());
		
		//DF20180720 - KO369761 - Rest service for deleting user token ids.
		singletons.add(new UserTokenIdsRESTService());

		//DF20180816-KO369761 - GeoCoding service for Non-Indian machines
		singletons.add(new GeocodingSAARCService());
	
		//DF20181004-KO369761-REST Service for deleting duplicate fault details records
		singletons.add(new DeleteDuplicateFaultDetailsRecordsRESTService());
		//DF20181012 :: MA369757 :: Rest service for deleting the  15mins old CSRF tokens 
		singletons.add(new CsrfTokenDeletionService());
		
		//DF20181203 - KO369761 - Rest application created for get user details.
		singletons.add(new UserDetailsRESTService());
		
		//DF20181207- REST Service for update CMH for new Rolled off machines.
		singletons.add(new UpdateCMHForMachinesService());

		//DF20181116 :: MA369757 :: Service to get dealer outlets
		singletons.add(new DealerOutletMappingService());
		
		//DF20181213 - KO369761 - REST Service for Genset trends data.
		singletons.add(new GensetTrendsDataService());
		//DF20181213 - MA369757 :: Service to set 20 mins old CMH offset to NULL
		singletons.add(new CmhRefreshOffsetService());
		
		//DF20190308 - AB369654 :: REST Service for FleetSummary Details
		singletons.add(new FleetSummaryServiceRESTService());
		
		//DF20190220 - Z1007653 :: Service to get Interface list for Interface functionality
		singletons.add(new InterfaceListRestService());
		//DF20190305 - Z1007653 :: Service to send weekly interface details to user, using shell scripts
		singletons.add(new SendInterfaceDetailsAsEmailService());
		//DF20190315 - AB369654 :: Service to close services from MOOL DA Reports.
		singletons.add(new ServiceClouserReportUpdateRESTService());
		//DF20190315 - AB369654 :: Service to close services from UI
		singletons.add(new ServiceClosureRESTService());
		//DF20190315 - AB369654 :: Service to close services from KAfka BackLog file.
		singletons.add(new ServiceClouserKafkaBacklogService());
		
		//DF20190422--Added for Pseudo Tenancy Access by Yogee
		singletons.add(new PseudoAccessListService());
		//DF20190705 Avinash Machine Group Code Merge From SIT TO PROD
		singletons.add(new MachineGroupAsset());
		
		//DF20190711 - AB369654 :: REST Service TO DOWNLOAD ACTIVE service event
		singletons.add(new ServiceReportDownloadRestService());
		
		//DF20190716 - AB369654 :: Service to get CMH for BOT.
		singletons.add(new HMRDetailsService());
		
		
		//DF20190816-Abhishek::To fetch account code based on tenancyIdList
		singletons.add(new TenancyToAccountCodeRESTService());
		
		//DF20190823-Abhishek::To fetch Asset Machine Group name based on loginId. 
		singletons.add(new AssetGroupNameRestService());
		
		//DF20190913 - AB369654 :: service to fetch machine count for new MADashboard. 
		singletons.add(new MADMAchineCountRESTService());
				
		//DF20190913 - AB369654 :: service to fetch Profile List for new MADashboard. 
		singletons.add(new MADProfileListRESTService());
				
		//DF20190913 - AB369654 :: service to fetch Red Alert Count for new MADashboard.  
		singletons.add(new MADREDAlertRESTService());
				
		//DF20190913 - AB369654 :: service to fetch Service Due Overdue count for new MADashboard.  
		singletons.add(new MAServiceDueOverDueCountRESTService());
				
		//DF20190913 - AB369654 :: service to fetch LLI Renewal list for new MADashboard.  
		singletons.add(new MADLLIRenewalRESTService());		
		
		//DF20190816-Abhishek::To fetch titan parameter for engine utilization. 
		singletons.add(new TitanMachineParameterRestService());
		
		//DF20191125-Abhishek::To fetch machine group related to particular user ID. 
		singletons.add(new UserDetailsHelperRestService());
		
		//DF20191223-Abhishek::To update the Extended Warranty services. 
		singletons.add(new ExtendedWarrentyRESTService());
		
		//DF20191223-Abhishek::To read the extended Warranty input files.
		singletons.add(new ExtendedWarrantyService());
				
		//DF20191223-Abhishek::To update the table daliy.
		singletons.add(new ExtendedWarantyDailyCheckService());
		
		//DF20191224-Mamatha::To set customer as MA Customer. 
		singletons.add(new AccountTypeCustomerToMACustomerRESTService());
		
		//DF20200214-Mamatha::To delete Deault table having size more than 40 GB. 
		singletons.add(new TruncateTAssetMonDataJCBdefaultService());
		singletons.add(new SessionMapService());
		//Ramu B: to fetch the Rolled off machine data on to maps
		singletons.add(new RolledOffMachinesOnMapService());
		singletons.add(new DataFromComRepOemTableRESTService());
		singletons.add(new FetchAssetVinFromAssetMachineRESTService());
		singletons.add(new CommunicationReportEnhancedRESTService());
		singletons.add(new MaAccountTenancyIdListRESTService());
		//Ramu B added on 20200714
		singletons.add(new GenericModelService());
		singletons.add(new GenericProfileService() );
		singletons.add(new GenericModelService());		
		singletons.add(new GenericModelCodeService());
		singletons.add(new PopulateUnderUtilizedMachinesRESTService());
		singletons.add(new UnderUtilizedMachinesDataRESTService());
		singletons.add(new AlertSeverityReportRESTService());
		singletons.add(new MaAccountTenancyIdListRESTService());
		singletons.add(new MaAccountCodesListRESTService());
		singletons.add(new UserDetailsServiceRESTService());
		singletons.add(new MaAssetVinListRESTService());
		singletons.add(new GetMachineRenewalStatus());
		singletons.add(new PopulateUserNotLoggedRESTService());
		singletons.add(new UserNotLoggedRESTService());
		singletons.add(new VinAsNickNameRESTService());
		singletons.add(new UnmergeBpCodeRESTService());
		//Yaseswini : 20210119 : NonCommunicatingMachinesReportService is to update or provide details of non communicating machines 
		singletons.add(new NonCommunicatingMachinesReportService());
		singletons.add(new DOutLetMapping());
		singletons.add(new AccountCodeListForTenancyRESTService());
		//singletons.add(new UpdateClearBacklogFlagRESTService());
		singletons.add(new UpdateClearBacklogTracebilityDataRestService());
		singletons.add(new DisconnectedVINSFromVFRESTService());
		singletons.add(new UnblockVINsServiceRESTService());
		singletons.add(new VinDisconnectedStatusRESTService());
		singletons.add(new RetrofitmentService());//CR352.n
		
		
		
		//singletons.add(new VinNoFromGroupIdRESTService());
		//Shajesh: Awake ID -100000924: 20220405 : FW Live link Portal - XP machine group : UnComment the service for displaying the xp machine group in LLP
		singletons.add(new VinNoFromGroupIdRESTService());
		//Zakir : 20201123 : DealerOutletService is to provide Dealer and their Outlet location details
		singletons.add(new DealerOutletService());
		singletons.add(new GroupDetailsRESTService());
		
		
		//CR212 - 20210713 - Rajani Nagaraju - Extended Warranty Changes
		singletons.add(new ExtendedWarrantyEnablerBatchService());
		//TaskID 1434 - JCB4618 - Rajani Nagaraju - Pending Tenancy Auto creation
				singletons.add(new PendingTenancyBatchService());
		
		//CR204 Avinash Xavier Machine Down Interface
		singletons.add(new MachineDownService());
		singletons.add(new TESTService());
		
		
		
		//CR204 Avinash Xavier Machine Down Interface
		singletons.add(new MachineDownService());
		//TaskID 1481 - JCB4908 - Rajani Nagaraju - contact creation failure - New Implementation
		singletons.add(new AssetSaleFromD2CRestService());
		singletons.add(new AssetGateoutRestService());
		singletons.add(new SiteNameAssociationForMACustomerRestService());
		singletons.add(new ReProcessVINDetailsToUpdateRedisRestService());
		
		singletons.add(new TimeFenceService());
		singletons.add(new GeoFenceService());
		singletons.add(new WhatsAppUpdateService());
		singletons.add(new BreakfastReport());
		singletons.add(new MachineLiveTracking());
		
		singletons.add(new GeofenceReport());
		singletons.add(new TimefenceReport());
		singletons.add(new WhatsAppReport());
		
		singletons.add(new MappingLLFlagForVINRestService());
		singletons.add(new TenancyTypeService());
		//CR308.sn
		singletons.add(new ReturnMAFlagService());
		//CR308.en
		//CR321.sn
		singletons.add(new CombineTotraceablilityFotaReportTableService());
		//CR321.en
		//CR321V2.sn
		singletons.add(new CombineTotraceablilityFotaReportTableServiceV2());
		//CR321V2.en
		//CR344.sn
		singletons.add(new UnknownDTCReportService());
		//CR344.en
		singletons.add(new TestServiceV2());
		
		singletons.add(new VinCHMRService()); //
		
		singletons.add(new LLPremiumService()); //CR353.n
		//singletons.add(new UpdateServiceAlertsRESTService()); //CR366.n
		//singletons.add(new ReprocessWeatherDataService()); 	//JCB6284.n
		singletons.add(new WrapAssetServiceScheduleImpRestService()); //CR373.n
		singletons.add(new UpdateServiceAlertsRESTService()); //CR366.n
		singletons.add(new SootMonitorService()); //CR407.n
		singletons.add(new TenancyBridgeService()); //JCB6337.n
		singletons.add(new ReverseGeoCodingMMI());	//mmi.n  mapmyIndia poc changes
		singletons.add(new DTCAlertReportService()); 	//CR417.n
		singletons.add(new TestServiceV3());// ME100008333.n
		singletons.add(new LogDownloadDataService());// CR416.n
		//CR419.sn 
		singletons.add(new AempClientService());  
		singletons.add(new AempDisableClientService());
		singletons.add(new AempEnableClientService());
		singletons.add(new AempViewClientService());
		singletons.add(new AempRegisteredClientService());
		singletons.add(new AempApprovelClient());
		singletons.add(new AempApproveDisApproveClient());
		singletons.add(new AempSAInvocationHistoryService());
		//CR419.en
		singletons.add(new DownloadPTPacket());// CR416.n
		singletons.add(new GeoFenceServiceV4());//CR432.n
		singletons.add(new TimeFenceServiceV4());//CR432.n
		
		singletons.add(new InsertIntoContactActivityLog());//CR429.n
		singletons.add(new VDStageVService());//CR397.n
		
		singletons.add(new TestAMSTable());
		singletons.add(new GeoFenceServiceV5()); //CR439.n
		singletons.add(new DynamicMOSPWeekWiseTableService()); //CR434.n
		singletons.add( new GensetService()); //CR424.n
		
		singletons.add( new ExcavatorData());
		singletons.add( new AirtelApiIntegration()); //CR452.n
		
		singletons.add( new FuelUtilizationDetailRESTServiceV1());
		
		singletons.add( new ModelWiseDistribution()); //CR448.n
		
		
		singletons.add(new DataForUnallocatedMachineRestService());//CR462.n
		
		singletons.add(new OfflineAlertSeverityReportRESTServiceCsv());//ME100011411.n
		singletons.add(new ForgotPasswordExtendedService());//JCB6622.n
		singletons.add(new RemoteDiagnosticsRestService());//CR517
		singletons.add(new DealerDataAndSMSNotificationsRestService());//CR498.n
		singletons.add(new CommRepHOIntoXlsFile());//Prasanna:20250519:HO comm report
		
	}

	@Override
	public Set<Class<?>> getClasses() {
		return empty;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
