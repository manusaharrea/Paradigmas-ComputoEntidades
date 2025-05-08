package utils;

import java.io.Serializable;

public class FeedsData implements Serializable {
    private String label;
    private String url;
    private String type;

    public FeedsData(String label, String url, String type) {
        this.label = label;
        this.url = url;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public void print() {
        System.out.println("Feed: " + label);
        System.out.println("URL: " + url);
        System.out.println("Type: " + type);
    }
}