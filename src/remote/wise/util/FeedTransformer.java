package remote.wise.util;

import java.util.ArrayList;

import com.google.gson.Gson;

public class FeedTransformer {
	public static String ConvertToJson(ArrayList<ResponseObject> feedData) {
		String feeds = null;
		Gson gson = new Gson();
		feeds = gson.toJson(feedData);
		return feeds;
	}
}