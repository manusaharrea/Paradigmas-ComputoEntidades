import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import utils.Config;
import utils.FeedsData;
import utils.JSONParser;
import utils.UserInterface;
import namedEntities.NamedEntity;

public class App implements Serializable{

    public static void main(String[] args) {
        List<FeedsData> feedsDataArray = new ArrayList<>();
        try {
            System.out.println("Current working directory: " + System.getProperty("user.dir") + "\n");
            feedsDataArray = JSONParser.parseJsonFeedsData(System.getProperty("user.dir") + "/target/classes/data/feeds.json");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        UserInterface ui = new UserInterface();
        Config config = ui.handleInput(args, feedsDataArray);

        run(config, feedsDataArray); 
   
    }

    private static void run(Config config, List<FeedsData> feedsDataArray) {

        
        if (config.getPrintHelp()) {
            UtilsApp utilsApp = new UtilsApp();
            utilsApp.printHelp(feedsDataArray);
        } else {
            if (feedsDataArray == null || feedsDataArray.size() == 0) {
                System.out.println("No feeds data found\n");
                return;
            }

            UtilsApp utilsApp = new UtilsApp();

            String dataPath = config.getDataPath();

            if (config.getDataPath() == null) {
                //build article list
                utilsApp.buildArticles(config, feedsDataArray);

                //print articles
                utilsApp.printArticles(config, feedsDataArray);

                dataPath = "target/classes/filename.txt";
            }
            
            //inciamos una sesion spark
            SparkSession spark = SparkSession.builder()
                        .appName("NamedEntityApp")
                        .getOrCreate();

            //creamos un contexto de spark para poder acceder a nuestro cluster
            JavaSparkContext sparkContext = new JavaSparkContext(spark.sparkContext());
            
            //leemos el archivo de texto en dataPath y lo guardamos en un tipo basico de spark RDD de tipo String ()
            JavaRDD<String> lines = spark.read().textFile(dataPath).javaRDD();

            //obtain named entities from articles
            JavaRDD<NamedEntity> entidadesNombradasRDD = utilsApp.computedNamedEntities(config, lines, sparkContext);
            List<NamedEntity> entidadesNombradas = entidadesNombradasRDD.collect();

            sparkContext.stop();
            spark.stop();

            // Printeamos Stats
            if (entidadesNombradas != null && config.getComputeNamedEntities()) {
                utilsApp.printStats(config, entidadesNombradas);
            }
        }
    }
}
