package utils;

import java.io.Serializable;
import java.util.List;

public class Config implements Serializable {
    private String dataPath;
    private boolean printFeed = false;
    private boolean computeNamedEntities = false;
    private boolean printHelp = false;
    private List<String> feedKey;
    private String heuristic;
    private String statsFormat;

    public Config(String dataPath, boolean printFeed, boolean computeNamedEntities, boolean printHelp, List<String> feedKey,
            String heuristic, String statsFormat) {
        this.dataPath = dataPath;
        this.printFeed = printFeed;
        this.computeNamedEntities = computeNamedEntities;
        this.printHelp = printHelp;
        this.feedKey = feedKey;
        this.heuristic = heuristic;
        this.statsFormat = statsFormat;
    }

    public String getDataPath() {
        return dataPath;
    }

    public boolean getPrintFeed() {
        return printFeed;
    }

    public boolean getPrintHelp() {
        return printHelp;
    }

    public boolean getComputeNamedEntities() {
        return computeNamedEntities;
    }

    public List<String> getFeedKey() {
        return feedKey;
    }

    public String getHeuristic() {
        return heuristic;
    }

    public String getStatsFormat() {
        return statsFormat;
    }
}
