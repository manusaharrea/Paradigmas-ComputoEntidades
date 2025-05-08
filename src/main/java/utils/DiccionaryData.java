package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiccionaryData implements Serializable {
    private String label;
    private String category;
    private List<String> topics;
    private List<String> keywords;

    public DiccionaryData(String label, String category, List<String> topics, List<String> keywords) {
        this.label = label;
        this.category = category;
        this.topics = new ArrayList<String>(topics);
        this.keywords = new ArrayList<String>(keywords);
    }

    public String getLabel() {
        return label;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getTopics() {
        return topics;
    }

    public List<String> getKeywords() {
        return keywords;
    }
}