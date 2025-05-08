package namedEntities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilsNamedEntities implements Serializable {

    private List<String> Topics;
    private List<String> Categories;
    private List<String> Heuristics;

    public UtilsNamedEntities() {
        Topics = new ArrayList<String>();
        Topics = Arrays.asList("POLITICS", "SPORTS", "ECONOMICS", "HEALTH", "TECHNOLOGY", "CULTURE", "SCIENCE",
                "ENTERTAINMENT", "OTHER");
        Categories = new ArrayList<String>();
        Categories = Arrays.asList("PERSON", "LOCATION", "ORGANIZATION", "OTHER");
        Heuristics = new ArrayList<String>();
        Heuristics = Arrays.asList("CapitalizedWordHeuristic", "AcronymHeuristic", "ProvincesHeuristic",
                "ModifiedCapitalizedHeuristic");
    }

    public List<String> getTopics() {
        return Topics;
    }

    public List<String> getCategories() {
        return Categories;
    }

    public List<String> getHeuristics() {
        return Heuristics;
    }
}