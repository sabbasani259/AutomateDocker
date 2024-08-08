package remote.wise.EAintegration.Qhandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class KafkaConsumerLag 
{
	public int getConsumerLag(String topicName, String groupName)
	{
		int consumerLag =0 ;
		try 
		{
			String executeShellScript="/user/JCBLiveLink/JobScheduling/ShellScripts/KafkaConsumerLag.sh";
			ProcessBuilder pb = new ProcessBuilder("/bin/sh",executeShellScript,topicName, groupName);
			pb.redirectErrorStream(true);
			Process p = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) 
			{
				if(line!=null)
					consumerLag = Integer.parseInt(line);
			}
		} 

		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		return consumerLag;
	}
}
