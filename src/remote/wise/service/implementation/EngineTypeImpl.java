package remote.wise.service.implementation;

import remote.wise.businessobject.AssetDetailsBO;

/** Implementation class to capture the Engine Type Details as Master data from EA system
 * @author Rajani Nagaraju
 *
 */
public class EngineTypeImpl 
{
	public String setEngineType(String engineTypeName, String engineTypeCode)
	{
		String status = "SUCCESS";
		AssetDetailsBO assetDetailsBo = new AssetDetailsBO();
		
		if(engineTypeName==null || engineTypeCode==null || engineTypeName.trim()==null || engineTypeCode.trim()==null)
		{
			status = "FAILURE";
			return status;
		}
		
		String checkEngineTypeName = engineTypeName.replaceAll("\\s","") ;
		String checkEngineTypeCode = engineTypeCode.replaceAll("\\s","") ;
		
		if( (!(checkEngineTypeName.length()>0))|| (!(checkEngineTypeCode.length()>0)))
		{
			status = "FAILURE";
			return status;
		}
		
		status = assetDetailsBo.setEngineType(engineTypeName,engineTypeCode);
		
		return status;
	}
}
