package remote.wise.service.implementation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.logging.log4j.Logger;

import remote.wise.businessobject.AssetDetailsBO;
import remote.wise.exception.CustomFault;
import remote.wise.log.InfoLogging.InfoLoggerClass;
import remote.wise.service.datacontract.RolledOffMachinesReqContract;
import remote.wise.service.datacontract.RolledOffMachinesRespContract;
import remote.wise.util.CommonUtil;
import remote.wise.util.IstGmtTimeConversion;
import remote.wise.service.webservice.UserDetailsHelperRestService;
//import remote.wise.util.WiseLogger;

/**
 * @author kprabhu5
 *
 */
public class RolledOffMachinesImpl {

	//public static WiseLogger infoLogger = WiseLogger.getLogger("RolledOffMachinesImpl:","info");

	private String serialNumber;
	private String IMEINumber;
	private String simNumber;
	private String machineName;
	private int assetGroupId;
	private int assetTypeId;
	private int engineTypeId;

	private String profileName;
	private String modelName;
	private String engineName;

	private String latitude;
	private String longitude;
	//DefectId:20140922 new Search Tab @Suprava
	private String regDate;

	//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
	private String fuelLevel;
	private String lastReportedTime;
	private String enginehours;
	//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer

	// DF20200429 - Zakir : Adding comment variable for new column option in New
	// machines tab
	private String comment;
	//CR481-20240731-Sai Divya-Status,proposedFWVersion,fWVersion columns are added to NMT
	private String status;
	private String proposedFWVersion;
	private String fWVersion;
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProposedFWVersion() {
		return proposedFWVersion;
	}

	public void setProposedFWVersion(String proposedFWVersion) {
		this.proposedFWVersion = proposedFWVersion;
	}

	public String getfWVersion() {
		return fWVersion;
	}

	public void setfWVersion(String fWVersion) {
		this.fWVersion = fWVersion;
	}
	
	// Zakir: Adding getters and setters for Comment
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
	
	/**
	 * @return the regDate
	 */
	public String getRegDate() {
		return regDate;
	}
	/**
	 * @param regDate the regDate to set
	 */
	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the iMEINumber
	 */
	public String getIMEINumber() {
		return IMEINumber;
	}
	/**
	 * @param iMEINumber the iMEINumber to set
	 */
	public void setIMEINumber(String iMEINumber) {
		IMEINumber = iMEINumber;
	}
	/**
	 * @return the simNumber
	 */
	public String getSimNumber() {
		return simNumber;
	}
	/**
	 * @param simNumber the simNumber to set
	 */
	public void setSimNumber(String simNumber) {
		this.simNumber = simNumber;
	}

	/**
	 * @return the machineName
	 */
	public String getMachineName() {
		return machineName;
	}
	/**
	 * @param machineName the machineName to set
	 */
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	/**
	 * @return the assetGroupId
	 */
	public int getAssetGroupId() {
		return assetGroupId;
	}
	/**
	 * @param assetGroupId the assetGroupId to set
	 */
	public void setAssetGroupId(int assetGroupId) {
		this.assetGroupId = assetGroupId;
	}
	/**
	 * @return the assetTypeId
	 */
	public int getAssetTypeId() {
		return assetTypeId;
	}
	/**
	 * @param assetTypeId the assetTypeId to set
	 */
	public void setAssetTypeId(int assetTypeId) {
		this.assetTypeId = assetTypeId;
	}
	/**
	 * @return the engineTypeId
	 */
	public int getEngineTypeId() {
		return engineTypeId;
	}
	/**
	 * @param engineTypeId the engineTypeId to set
	 */
	public void setEngineTypeId(int engineTypeId) {
		this.engineTypeId = engineTypeId;
	}
	/**
	 * @return the profileName
	 */
	public String getProfileName() {
		return profileName;
	}
	/**
	 * @param profileName the profileName to set
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}
	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	/**
	 * @return the engineName
	 */
	public String getEngineName() {
		return engineName;
	}
	/**
	 * @param engineName the engineName to set
	 */
	public void setEngineName(String engineName) {
		this.engineName = engineName;
	}


	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}



	/**
	 * @return the fuelLevel
	 */
	public String getFuelLevel() {
		return fuelLevel;
	}
	/**
	 * @param fuelLevel the fuelLevel to set
	 */
	public void setFuelLevel(String fuelLevel) {
		this.fuelLevel = fuelLevel;
	}
	/**
	 * @return the lastReportedTime
	 */
	public String getLastReportedTime() {
		return lastReportedTime;
	}
	/**
	 * @param lastReportedTime the lastReportedTime to set
	 */
	public void setLastReportedTime(String lastReportedTime) {
		this.lastReportedTime = lastReportedTime;
	}
	/**
	 * @return the enginehours
	 */
	public String getEnginehours() {
		return enginehours;
	}
	/**
	 * @param enginehours the enginehours to set
	 */
	public void setEnginehours(String enginehours) {
		this.enginehours = enginehours;
	}
	/**
	 * method to get rolled off machines for login id or tenancy id list
	 * @param reqObj
	 * @return
	 * @throws CustomFault
	 */
	public List<RolledOffMachinesRespContract> getRolledOffMachines(RolledOffMachinesReqContract reqObj) throws CustomFault{
		List<RolledOffMachinesRespContract> responseList = new ArrayList<RolledOffMachinesRespContract>();

		Logger iLogger = InfoLoggerClass.logger;
		String roleName="";
		if(reqObj.getTenancyIdList()== null || reqObj.getTenancyIdList().size()<1){
			iLogger.info("Please provide Tenancy Id List !!!");
			throw new CustomFault("Please provide Tenancy Id List !!!");		
		}
		//DF20171011: KO369761 - Security Check added for input text fields.
		if(reqObj.getVin()!=null){
			CommonUtil util = new CommonUtil();
			String isValidinput = util.inputFieldValidation(reqObj.getVin());
			if(!isValidinput.equals("SUCCESS")){
				throw new CustomFault(isValidinput);
			}
		}
		UserDetailsHelperRestService userDeatilHelper=new UserDetailsHelperRestService();
		roleName=userDeatilHelper.getUserRole(reqObj.getLoginId());
		iLogger.info("Role name in RolledOffMachinesImpl :  "+roleName);
		
		AssetDetailsBO assetDetailsBo = new AssetDetailsBO();
		RolledOffMachinesRespContract responseObj = null;
		List<RolledOffMachinesImpl> implList = assetDetailsBo.getRolledOffMachines(reqObj.getLoginId(), reqObj.getTenancyIdList(),reqObj.getVin(),reqObj.getToDate(),reqObj.getFromDate());


		//Df20160811 @Roopa for download new machines as excel changes

		try{
			if(reqObj.getVin()!=null && reqObj.getVin().equalsIgnoreCase("ExcelDownload")){
				try {
					Properties prop1 = new Properties();


					prop1.load(getClass().getClassLoader().getResourceAsStream("remote/wise/resource/properties/configuration.properties"));

					String downloadNEWVINS="";
					if (prop1.getProperty("deployenvironment").equalsIgnoreCase("SIT")) {
						downloadNEWVINS = prop1.getProperty("DownloadNewVINS_SIT");

					} 
					else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("DEV")) {
						downloadNEWVINS = prop1.getProperty("DownloadNewVINS_DEV");

					}
					else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("QA")) {
						downloadNEWVINS = prop1.getProperty("DownloadNewVINS_QA");

					} 
					else if (prop1.getProperty("deployenvironment").equalsIgnoreCase("PROD")) {
						downloadNEWVINS = prop1.getProperty("DownloadNewVINS_PROD");

					}

			     String FileName = downloadNEWVINS +"Non_CommunicatedVINList"+".xls";
				//String FileName="D:/DownloadExcel/"+"Non_CommunicatedVINList"+".xls";
				String DIR = downloadNEWVINS;
				//String DIR="D:/DownloadExcel/";

					// delete all existing files
					File file = new File(DIR);
					String[] myFiles;
					if (file.isDirectory()) {
						myFiles = file.list();
						for (int k = 0; k < myFiles.length; k++) {
							File myFile = new File(file, myFiles[k]);
							if (myFile.getName().equalsIgnoreCase("Non_CommunicatedVINList"+".xls")) {
								iLogger.info("file name getting deleted : "
										+ myFile.getName());
								myFile.delete();
							}

						}
					}
					int cellIndex=-1;
					WorkbookSettings ws = new WorkbookSettings();
					WritableWorkbook workbook = Workbook.createWorkbook(new File(
							FileName), ws);

					WritableSheet workSheet = null;
					workSheet = workbook.createSheet("Non_Communicated_VIN's_Report", 0);

					SheetSettings sh = workSheet.getSettings();
					WritableFont normalFont = new WritableFont(
							WritableFont.createFont("Calibri"),
							WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD,
							false, UnderlineStyle.NO_UNDERLINE);
					WritableCellFormat normalFormat1 = new WritableCellFormat(
							normalFont);

					normalFormat1.setAlignment(jxl.format.Alignment.JUSTIFY);
					normalFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);

					normalFormat1.setBorder(jxl.format.Border.ALL,
							jxl.format.BorderLineStyle.THIN,
							jxl.format.Colour.BLACK);

					if (implList !=null && implList.size()>0) {
						iLogger.info("Non communicated Machines excel generation start---------");

						WritableCellFormat normalFormat = new WritableCellFormat(
								normalFont);
						normalFormat.setAlignment(jxl.format.Alignment.CENTRE);
						normalFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
						normalFormat.setBackground(jxl.format.Colour.GRAY_25);
						normalFormat.setWrap(true);
						normalFormat.setBorder(jxl.format.Border.ALL,
								jxl.format.BorderLineStyle.THIN,
								jxl.format.Colour.BLACK);

						WritableCellFormat normalFormat3 = new WritableCellFormat(
								normalFont);
						normalFormat3.setAlignment(jxl.format.Alignment.CENTRE);			
						normalFormat3.setVerticalAlignment(VerticalAlignment.CENTRE);
						normalFormat3.setWrap(true);
						normalFormat3.setBorder(jxl.format.Border.ALL,
								jxl.format.BorderLineStyle.THIN,
								jxl.format.Colour.BLACK);


						// first two columns justified,rest centered
						workSheet.setColumnView(0, 10);
						workSheet.setColumnView(1, 20);
						workSheet.setColumnView(2, 15);
						workSheet.setColumnView(8, 40);
						workSheet.addCell(new jxl.write.Label(++cellIndex, 0, "Registration Date",
								normalFormat));
						workSheet.addCell(new jxl.write.Label(++cellIndex, 0, "PIN",
								normalFormat));
						workSheet.addCell(new jxl.write.Label(++cellIndex, 0, "IMEI",
								normalFormat));
						workSheet.addCell(new jxl.write.Label(++cellIndex, 0, "SIM",
								normalFormat));
						
						//DF20200429 - Zakir : Adding new column
						workSheet.addCell(new jxl.write.Label(++cellIndex, 0, "Comment",
								normalFormat));
						//CR481-20240731-Sai Divya-Status,proposedFWVersion,fWVersion columns are added to NMT
//						workSheet.addCell(new jxl.write.Label(++cellIndex, 0, "status",
//								normalFormat));
//						workSheet.addCell(new jxl.write.Label(++cellIndex, 0, "proposedFWVersion",
//								normalFormat));
//						workSheet.addCell(new jxl.write.Label(++cellIndex, 0, "fWVersion",
//								normalFormat));
						//2021-03-18:Shajesh : add owner in NMT
						if(roleName.equalsIgnoreCase("JCB Admin")  || roleName.equalsIgnoreCase("Super Admin"))
						workSheet.addCell(new jxl.write.Label(++cellIndex, 0, "Owner",
								normalFormat));
						//2021-03-18 : Shajesh : addition of dealer name in NMT
						if(roleName.equalsIgnoreCase("JCB Admin")  || roleName.equalsIgnoreCase("Super Admin")||roleName.equalsIgnoreCase("JCB HO")||roleName.equalsIgnoreCase("JCB RO"))
						workSheet.addCell(new jxl.write.Label(++cellIndex, 0, "Dealer",
								normalFormat));

						int p = 0;
						for(int i=0;i<implList.size();i++){
							int k=-1;
							workSheet.addCell(new jxl.write.Label(++k, p + 1,
									IstGmtTimeConversion.stringGMTToIST(implList.get(i).getRegDate()), normalFormat1));

							workSheet.addCell(new jxl.write.Label(++k, p + 1,
									implList.get(i).getSerialNumber().split("~")[0], normalFormat3));

							workSheet.addCell(new jxl.write.Label(++k, p + 1,
									implList.get(i).getIMEINumber(), normalFormat3));

							workSheet.addCell(new jxl.write.Label(++k, p + 1,
									implList.get(i).getSimNumber(), normalFormat3));
							
							//DF20200429 - Zakir - Adding new column
							workSheet.addCell(new jxl.write.Label(++k, p + 1,
									implList.get(i).getComment(), normalFormat3));
							//CR481-20240731-Sai Divya-Status,proposedFWVersion,fWVersion columns are added to NMT
//							workSheet.addCell(new jxl.write.Label(++k, p + 1,
//									implList.get(i).getfWVersion(), normalFormat3));
//							workSheet.addCell(new jxl.write.Label(++k, p + 1,
//									implList.get(i).getProposedFWVersion(), normalFormat3));
//							workSheet.addCell(new jxl.write.Label(++k, p + 1,
//									implList.get(i).getStatus(), normalFormat3));
							if(roleName.equalsIgnoreCase("JCB Admin")  || roleName.equalsIgnoreCase("Super Admin"))
							workSheet.addCell(new jxl.write.Label(++k, p + 1,
									implList.get(i).getSerialNumber().split("~")[1], normalFormat3));
							//2021-03-18 : Shajesh : addition of dealer name in NMT
							if(roleName.equalsIgnoreCase("JCB Admin")  || roleName.equalsIgnoreCase("Super Admin")||roleName.equalsIgnoreCase("JCB HO")||roleName.equalsIgnoreCase("JCB RO"))
							workSheet.addCell(new jxl.write.Label(++k, p + 1,
									implList.get(i).getSerialNumber().split("~")[2], normalFormat3));
							p++;
						}
						iLogger.info("Non communicated Machines excel generation end---------");

					}
					else{
						workSheet.setColumnView(0, 15);
						workSheet.addCell(new jxl.write.Label(0, 0,
								"NO DATA FOUND", normalFormat1));	
					}
					workbook.write();
					workbook.close();

				} catch (Exception e) {
					e.printStackTrace();

				}

			}
			else{

				if(implList !=null){
					RolledOffMachinesImpl implObj =null;
					Iterator<RolledOffMachinesImpl> iterList = implList.iterator();
					while(iterList.hasNext())
					{
						implObj = iterList.next();
						responseObj = new RolledOffMachinesRespContract();
						responseObj.setSerialNumber(implObj.getSerialNumber());
						responseObj.setSimNumber(implObj.getSimNumber());
						responseObj.setIMEINumber(implObj.getIMEINumber());
						responseObj.setAssetGroupId(implObj.getAssetGroupId());
						responseObj.setProfileName(implObj.getProfileName());
						responseObj.setAssetTypeId(implObj.getAssetTypeId());
						responseObj.setModelName(implObj.getModelName());
						responseObj.setEngineTypeId(implObj.getEngineTypeId());
						responseObj.setEngineName(implObj.getEngineName());
						responseObj.setMachineName(implObj.getMachineName());
						responseObj.setLatitude(implObj.getLatitude());
						responseObj.setLongitude(implObj.getLongitude());
						responseObj.setRegDate(implObj.getRegDate());

						//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
						responseObj.setLastReportedTime(implObj.getLastReportedTime());
						responseObj.setEnginehours(implObj.getEnginehours());
						responseObj.setFuelLevel(implObj.getFuelLevel());
						//DF20141010 - Rajani Nagaraju - Adding new tab for non communicated machines for Dealer and Customer
						//DF20200429 - Zakir - Adding comment
						responseObj.setComment(implObj.getComment());
						//CR481-20240731-Sai Divya-Status,proposedFWVersion,fWVersion columns are added to NMT
//						responseObj.setfWVersion(implObj.getfWVersion());
//						responseObj.setProposedFWVersion(implObj.getProposedFWVersion());
//						responseObj.setStatus(implObj.getStatus());
						//DF20200429 - Zakir - Adding comment
						responseList.add(responseObj);
					}

				}

			}
		}
		catch (Exception e) {
			e.printStackTrace();

		}


		return responseList;
	}
}
