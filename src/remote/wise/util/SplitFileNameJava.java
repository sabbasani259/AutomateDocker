package remote.wise.util;

public class SplitFileNameJava {
	public static void main(String[] args) {
		SplitFileName("/data/Content_Server/LLECU_FW_03.00.01.bin.001");
	}
	
	public static void SplitFileName(String fileNameToSplit) {
//		String string = "/data/Content_Server/LLECU_FW_03.00.01.bin.001";
		String string2[] = fileNameToSplit.split("/");
		String filename = string2[3];
		//System.out.println(filename);
		//System.out.println(filename.substring(filename.lastIndexOf(".")+1));
		//System.out.println((filename.substring((filename.lastIndexOf("_")+1),filename.lastIndexOf(".bin"))));
	}
}
