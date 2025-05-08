import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import namedEntities.LOCATION;
import namedEntities.NamedEntity;
import utils.Connection;


public class LatYLong implements Serializable {
    public void extractCandidates(List<NamedEntity> namedEntitites) {
        List<LOCATION> candidates = new ArrayList<>();

        for (NamedEntity namedEntity : namedEntitites) {
            if (namedEntity.getClass().equals(LOCATION.class)) {
                candidates.add((LOCATION) namedEntity);
            }
        }
        namedEntitites.removeIf(namedEntity -> namedEntity.getClass().equals(LOCATION.class));

        List<LOCATION> provincesCandidates = new ArrayList<>();
        try {
            provincesCandidates = fetchApiData(candidates);
        } catch (Exception e) {
            e.printStackTrace();
        }
        namedEntitites.addAll(provincesCandidates);
    }

    public static List<LOCATION> fetchApiData(List<LOCATION> candidates)
            throws MalformedURLException, IOException, Exception {

            String url = "https://apis.datos.gob.ar/georef/api/provincias";
            Connection connection = new Connection();
            HttpURLConnection urlConnection = connection.urlConnection(url);
            String data = connection.fetchDataUrl(urlConnection);

            List<LOCATION> provinceList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("provincias");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject provinceObject = jsonArray.getJSONObject(i);
                String province = provinceObject.getString("nombre");
                JSONObject centroideObject = provinceObject.getJSONObject("centroide");
                float lon = centroideObject.getFloat("lon");
                float lat = centroideObject.getFloat("lat");
                LOCATION location = new LOCATION(province, Arrays.asList("OTHER"), lat, lon);
                provinceList.add(location);
            }

            List<LOCATION> provinceCandidates = new ArrayList<>();

            for (LOCATION candidate : candidates) {
                for (LOCATION province : provinceList) {
                    if (province.getId().equals(candidate.getId())) {
                        candidate.setLatitud(province.getLatitud());
                        candidate.setLongitud(province.getLongitud());
                        provinceCandidates.add(candidate);
                    }
                }
            }

            return provinceCandidates;
        }
}
