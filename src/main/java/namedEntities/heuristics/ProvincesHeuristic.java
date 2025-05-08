package namedEntities.heuristics;

import java.net.HttpURLConnection;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Connection;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;

public class ProvincesHeuristic implements Serializable {

    public List<String> extractCandidates(String text) {
        List<String> candidates = new ArrayList<>();

        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("\\p{M}", "");

        Pattern pattern = Pattern.compile("[A-Z][a-z]+(?:\\s[A-Z][a-z]+)*");

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            candidates.add(matcher.group());
        }

        List<String> provincesCandidates = new ArrayList<>();
        try {
            provincesCandidates = fetchApiData(candidates);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return provincesCandidates;
    }

    public static List<String> fetchApiData(List<String> candidates)
            throws MalformedURLException, IOException, Exception {

        //build connection to url and fetch data
        String url = "https://apis.datos.gob.ar/georef/api/provincias";
        Connection connection = new Connection();
        HttpURLConnection urlConnection = connection.urlConnection(url);
        String data = connection.fetchDataUrl(urlConnection);

        List<String> provinceList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(data);
        JSONArray jsonArray = jsonObject.getJSONArray("provincias");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject provinceObject = jsonArray.getJSONObject(i);
            String province = provinceObject.getString("nombre");
            provinceList.add(province);
        }

        List<String> provinceCandidates = new ArrayList<>();

        for (String candidate : candidates) {
            for (String province : provinceList) {
                if (province.equals(candidate)) {
                    provinceCandidates.add(candidate);
                }
            }
        }

        return provinceCandidates;

    }
}
