package utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;


public class Connection implements Serializable {

    public HttpURLConnection urlConnection(String urlToConnect) 
            throws MalformedURLException, IOException, Exception {

            URL url = new URL(urlToConnect);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            // Si todos los grupos usan el mismo user-agent, el servidor puede bloquear las
            // solicitudes.
            // connection.setRequestProperty("User-agent", "lab_paradigmas");
            // Si es pagina 12 usar el de arriba
            connection.setRequestProperty("Grupo29", "lab_paradigmas");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            if (status != 200) {
                throw new Exception("HTTP error code: " + status);
            }

            return connection;
    }

    public String fetchDataUrl(HttpURLConnection connection) 
            throws IOException{

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return content.toString();
    }
    
}
