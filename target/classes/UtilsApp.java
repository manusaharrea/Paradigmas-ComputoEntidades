import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

import java.io.Serializable;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import feed.Article;

import namedEntities.LOCATION;
import namedEntities.NamedEntity;
import namedEntities.ORGANIZATION;
import namedEntities.OTHER;
import namedEntities.PERSON;
import namedEntities.UtilsNamedEntities;
import scala.Tuple2;
import utils.Config;
import utils.DiccionaryData;
import utils.FeedsData;
import utils.JSONParser;

@SuppressWarnings("unchecked")

public class UtilsApp implements Serializable{
    private List<Article> allArticles;

    public UtilsApp() {
        allArticles = new ArrayList<>();
    }

    public void buildArticles(Config config, List<FeedsData> feedsDataArray) {
        System.out.println("Printing feed(s)...\n");
        Boolean found = false;
        for (FeedsData feedData : feedsDataArray) {
            for (Integer i = 0; i < config.getFeedKey().size(); i++) {
                if (feedData.getLabel().equals(config.getFeedKey().get(i))) {
                    System.out.println("Fetching feed " + feedData.getLabel() + "\n");
                    try {
                        String feedXML = feed.FeedParser.fetchFeed(feedData.getUrl());
                        List<Article> articles = feed.FeedParser.parseXML(feedXML);
                        allArticles.addAll(articles);
                        found = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (!found) {
            System.out.println("Feed key not found\n");
        }
    }

    public void printArticles(Config config, List<FeedsData> feedsDataArray) {
        try{
            //creamos un file con los datos de los articulos en el directorio creado por maven
            PrintWriter out = new PrintWriter("target/classes/filename.txt");
            String result = "";
            for (Article article : allArticles) {
                if (config.getPrintFeed()) {
                    article.print();
                }
                result = result.concat(article.getTitle());
                result = result.concat(article.getDescription());
            }
            out.print(result);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JavaRDD<NamedEntity> computedNamedEntities(Config config, JavaRDD<String> lines, JavaSparkContext sparkContext) {
        
        JavaRDD<NamedEntity> namedEntitiesJDD = null;

        if (config.getComputeNamedEntities()) {

            try{
                UtilsNamedEntities listUtils = new UtilsNamedEntities();

                // aplicamos la heuristica seleccionada por el usuario a los datos
                JavaRDD<String> candidatesRDD = applyHeuristic(config, listUtils, lines);

                // recolectamos los datos de candidates en una lista de Strings debido al lazy evaluation de spark


                List<DiccionaryData> dictionaryList = new ArrayList<DiccionaryData>();
                try {
                    System.out.println("Loading diccionary data...\n");
                    dictionaryList = JSONParser.parseJsonDiccionaryData(System.getProperty("user.dir") + "/target/classes/data/dictionary.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            
                final Broadcast<List<DiccionaryData>> broadcastDictionary = sparkContext.broadcast(dictionaryList);
                List<DiccionaryData> dictionary = broadcastDictionary.value();

                namedEntitiesJDD = candidatesRDD.map(candidate -> applyDictionary(config, listUtils, candidate, dictionary));
                namedEntitiesJDD = reduceCandidates(namedEntitiesJDD);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  namedEntitiesJDD;

    }

    public JavaRDD<String> applyHeuristic(Config config, UtilsNamedEntities listUtils,JavaRDD<String> lines){
        
        JavaRDD<String> candidatesRDD = null;
        if (listUtils.getHeuristics().contains(config.getHeuristic())) {
            int index = listUtils.getHeuristics().indexOf(config.getHeuristic());
            System.out.println("Fetching heuristic " + listUtils.getHeuristics().get(index) + "\n");
            String classPath = "namedEntities.heuristics." + listUtils.getHeuristics().get(index);

            // a cada elemento del RDD lines le aplicamos la heuristica a travez de transformacion flatmap
            // para obtener multiples outputs, con cada elemento que cumpla con la heuristica
            // esos elementos estaran en la lista de Strings de candidatesRDD
            candidatesRDD = lines.flatMap(line -> {
                System.out.println("Applying heuristic...\n");
                Class<?> clazz = Class.forName(classPath);
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Method heuristicMethod = clazz.getMethod("extractCandidates", String.class);
                Object result = heuristicMethod.invoke(instance, line);
                return ((List<String>) result).iterator();
            });
        }
        return candidatesRDD;
    }
    
    public NamedEntity applyDictionary(Config config, UtilsNamedEntities listUtils, String candidate, List<DiccionaryData> dictionary){
        for (DiccionaryData dictionaryEntry : dictionary) {
            List<String> keywords = dictionaryEntry.getKeywords();
            if (keywords.contains(candidate)) {
                List<String> topics = new ArrayList<String>();
                boolean unknownTopics = false;
                for (String topic : dictionaryEntry.getTopics()) {
                    if (listUtils.getTopics().contains(topic)) {
                        topics.add(topic);
                    } else {
                        unknownTopics = true;
                    }
                    if (unknownTopics && !topics.contains("OTHER")) {
                        topics.add("OTHER");
                    }
        
                    if (dictionaryEntry.getTopics().equals(null)) {
                        topics.add("OTHER");
                    }
                    switch (dictionaryEntry.getCategory()) {
                        case "PERSON":
                            if (config.getHeuristic().equals("AcronymHeuristic")) {
                                return new PERSON(candidate, topics, 0);
                            } else {
                                return new PERSON(dictionaryEntry.getLabel(), topics, 0);
                            }
                            
                            case "LOCATION":
                            if (config.getHeuristic().equals("AcronymHeuristic")) {
                                return new LOCATION(candidate, topics, 0, 0);
                            } else {
                                return new LOCATION(dictionaryEntry.getLabel(), topics, 0, 0);
                            }

                        case "ORGANIZATION":
                        if (config.getHeuristic().equals("AcronymHeuristic")) {
                            return new ORGANIZATION(candidate, topics, "");
                        } else {
                                return new ORGANIZATION(dictionaryEntry.getLabel(), topics, "");
                            }

                        default:
                        if (config.getHeuristic().equals("AcronymHeuristic")) {
                                return new OTHER(candidate, topics);
                            } else {
                                return new OTHER(dictionaryEntry.getLabel(), topics);
                            }
                    }
                }
            } 
        }

        return new OTHER(candidate, Arrays.asList("OTHER"));
    }

    public JavaRDD<NamedEntity> reduceCandidates(JavaRDD<NamedEntity> candidatesRDD){
        JavaPairRDD<String, NamedEntity> reducedCandidates = candidatesRDD.mapToPair(candidate -> {
            return new Tuple2<String, NamedEntity>(candidate.getId(),candidate);
        }).reduceByKey((c1,c2) -> {
            int amount = c1.getAmount() + c2.getAmount();
            c1.setAmount(amount);
            return c1;
        });
        return reducedCandidates.map(x -> x._2());
    }

    public void printStats(Config config, List<NamedEntity> entidadesNombradas) {
        if (config.getStatsFormat() != null) {
            if (config.getStatsFormat().equals("cat")) {
                System.out.println("-".repeat(80));
                System.out.println("Stats format: " + config.getStatsFormat() + "\n");

                UtilsNamedEntities utilsNE = new UtilsNamedEntities();
                HashMap<String, List<NamedEntity>> categorias = new HashMap<String, List<NamedEntity>>();

                for (String cat : utilsNE.getCategories()) {
                    List<NamedEntity> list = new ArrayList<NamedEntity>();
                    categorias.put(cat, list);
                }

                for (NamedEntity entidadNombrada : entidadesNombradas) {
                    categorias.get(entidadNombrada.getClass().getSimpleName()).add(entidadNombrada);
                }

                for (String cat : utilsNE.getCategories()) {
                    System.out.println("Category: " + cat);
                    for (NamedEntity entidadNombrada : categorias.get(cat)) {
                        entidadNombrada.print();
                    }
                    System.out.println("-".repeat(80));
                }
            } else if (config.getStatsFormat().equals("topic")) {
                System.out.println("-".repeat(80));
                System.out.println("Stats format: " + config.getStatsFormat() + "\n");

                UtilsNamedEntities utils = new UtilsNamedEntities();
                HashMap<String, List<NamedEntity>> topics = new HashMap<String, List<NamedEntity>>();

                for (String topic : utils.getTopics()) {
                    List<NamedEntity> lista = new ArrayList<NamedEntity>();
                    topics.put(topic, lista);
                }
                for (NamedEntity entidadNombrada : entidadesNombradas) {
                    for (Integer i = 0; i < entidadNombrada.getTopic().size(); i++) {
                        topics.get(entidadNombrada.getTopic().get(i)).add(entidadNombrada);
                    }
                }
                for (String topic : utils.getTopics()) {
                    System.out.println("Topic: " + topic);
                    for (NamedEntity entidadNombrada : topics.get(topic)) {
                        entidadNombrada.print();
                    }
                    System.out.println("-".repeat(80));
                }
            }
        }
    }

    private static Integer isInListNamedEntity(String id, List<NamedEntity> namedEntities, String cat) {
        for (NamedEntity namedEntity : namedEntities) {
            if (namedEntity.getId().equals(id)
                    && (namedEntity.getClass().getSimpleName().equals(cat) || cat.equals("ANY"))) {
                return namedEntities.indexOf(namedEntity);
            }
        }
        return -1;
    }

    public void printHelp(List<FeedsData> feedsDataArray) {

        System.out.println("Usage: make run ARGS=\"[OPTION]\"");

        System.out.println("Options:");

        System.out.println("  -h, --help: Show this help message and exit");

        System.out.println("  -cd, --custom-data <dataPath>:       Set a filepath to a local file");

        System.out.println("                                       for processing");

        System.out.println("  -f, --feed <feedKey>:                Fetch and process the feed with");

        System.out.println("                                       the specified key");

        System.out.println("                                       Available feed keys are: ");

        for (FeedsData feedData : feedsDataArray) {

            System.out.println("                                       " + feedData.getLabel());

        }

        System.out.println("  -ne, --named-entity <heuristicName>: Use the specified heuristic to extract");

        System.out.println("                                       named entities");

        System.out.println("                                       Available heuristic names are: ");

        System.out.println("                                       _\"CapitalizedWordHeuristic\": search named");

        System.out.println("                                       entities based on capitalization");

        System.out.println("                                       _\"AcronymHeuristic\": search named entities");

        System.out.println("                                       based on acronyms");

        System.out.println("                                       _\"ProvincesHeuristic\": search named");

        System.out.println("                                       entities that are provinces from");

        System.out.println("                                       Argentina");

        System.out.println("                                       _\"ModifiedCapitalizedHeuristic\": small");

        System.out.println("                                       variaton of CapitalizedWordHeuristic");

        System.out.println("  -pf, --print-feed:                   Print the fetched feed");

        System.out.println("  -sf, --stats-format <format>:        Print the stats in the specified format");

        System.out.println("                                       Available formats are: ");

        System.out.println("                                       cat: Category-wise stats");

        System.out.println("                                       topic: Topic-wise stats");

    }
}