package feed;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

//Librerias para parsear XML
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import utils.Connection;

public class FeedParser implements Serializable {

    public static List<Article> parseXML(String xmlData) {
        List<Article> articles = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            StringReader reader = new StringReader(xmlData.trim());
            InputSource inputSource = new InputSource(reader);
            Document doc = dBuilder.parse(inputSource);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("item"); // Asume que "item" es el tag que contiene los artículos

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                    String link = eElement.getElementsByTagName("link").item(0).getTextContent();
                    String description = eElement.getElementsByTagName("description").item(0).getTextContent();
                    String pubDate = eElement.getElementsByTagName("pubDate").item(0).getTextContent();
                    // Asume que Article es una clase con un constructor que acepta título, enlace y
                    // descripción
                    Article article = new Article(title, description, link, pubDate);
                    articles.add(article);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return articles;
    }

    public static String fetchFeed(String feedURL) 
            throws MalformedURLException, IOException, Exception {

            //build connection to url and fetch data
            Connection connection = new Connection();
            HttpURLConnection urlConnection = connection.urlConnection(feedURL);
            String data = connection.fetchDataUrl(urlConnection);

            return data;
        }
}
