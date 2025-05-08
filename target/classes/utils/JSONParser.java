package utils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser implements Serializable {

    static public List<FeedsData> parseJsonFeedsData(String jsonFilePath) throws IOException {
        String jsonData = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        List<FeedsData> feedsList = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(jsonData);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String label = jsonObject.getString("label");
            String url = jsonObject.getString("url");
            String type = jsonObject.getString("type");
            feedsList.add(new FeedsData(label, url, type));
        }
        return feedsList;
    }

    static public List<DiccionaryData> parseJsonDiccionaryData(String jsonFilePath) throws IOException {
        String jsonData = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        List<DiccionaryData> diccionaryList = new ArrayList<>();

        JSONArray jsonArray = new JSONArray(jsonData);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String label = jsonObject.getString("label");
            String category = jsonObject.getString("Category");
            JSONArray jsonTopics = (JSONArray) jsonObject.get("Topics");
            List<String> topics = new ArrayList<String>();
            for (int j = 0; j < jsonTopics.length(); j++) {
                topics.add(jsonTopics.getString(j));
            }
            JSONArray jsonKeywords = (JSONArray) jsonObject.get("keywords");
            List<String> keywords = new ArrayList<String>();
            for (int j = 0; j < jsonKeywords.length(); j++) {
                keywords.add(jsonKeywords.getString(j));
            }
            diccionaryList.add(new DiccionaryData(label, category, topics, keywords));
        }

        return diccionaryList;
    }
}
