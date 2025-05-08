package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import namedEntities.UtilsNamedEntities;

public class UserInterface implements Serializable {

    private HashMap<String, String> optionDict;

    private List<Option> options;

    public UserInterface() {
        options = new ArrayList<Option>();
        options.add(new Option("-cd", "--custom-data", 1));
        options.add(new Option("-h", "--help", 0));
        options.add(new Option("-f", "--feed", 1));
        options.add(new Option("-ne", "--named-entity", 1));
        options.add(new Option("-pf", "--print-feed", 0));
        options.add(new Option("-sf", "--stats-format", 1));

        optionDict = new HashMap<String, String>();
    }

    public Config handleInput(String[] args, List<FeedsData> feedsDataArray) {
        for (Integer i = 0; i < args.length; i++) {
            for (Option option : options) {
                if (option.getName().equals(args[i]) || option.getLongName().equals(args[i])) {
                    if (option.getnumValues() == 0) {
                        optionDict.put(option.getName(), null);
                    } else {
                        if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                            optionDict.put(option.getName(), args[i + 1]);
                            if (option.getName().equals("-f")) {
                                for (FeedsData feed : feedsDataArray) {
                                    for (Integer j = i + 2; j < args.length && !args[j].startsWith("-"); j++) {
                                        if (feed.getLabel().equals(args[j])) {
                                            optionDict.put(option.getName(),
                                                    optionDict.get(option.getName()) + "," + args[j]);
                                        }
                                    }
                                }
                            }
                            i++;
                        } else {
                            if (option.getName().equals("-cd")) {
                                optionDict.put(option.getName(), null);

                            } else if (option.getName().equals("-sf")) {
                                optionDict.put(option.getName(), "cat");

                            } else if (option.getName().equals("-ne")) {
                                optionDict.put(option.getName(), null);

                            } else if (option.getName().equals("-f")) {
                                for (FeedsData feed : feedsDataArray) {
                                    optionDict.put(option.getName(),
                                            optionDict.get(option.getName()) + "," + feed.getLabel());
                                }
                            } else {
                                System.out.println("Invalid configuration inputs");
                                System.exit(1);
                            }
                        }
                    }
                }
            }
        }

        Boolean customData = optionDict.containsKey("-cd");
        Boolean printFeed = optionDict.containsKey("-pf");
        Boolean computeNamedEntities = optionDict.containsKey("-ne");
        Boolean printHelp = optionDict.containsKey("-h");

        String dataPath = optionDict.get("-cd");
        String heuristic = optionDict.get("-ne");

        if(customData){
            if(dataPath == null){
                System.out.println("Invalid custom data, proceeding with default feeds data\n");
            } else {
                System.out.println("Using custom data\n");
                return new Config(dataPath, printFeed, computeNamedEntities, printHelp, null, heuristic, "cat");
            }
        }


        List<String> feedKey = new ArrayList<>();
        try{
            String[] separateFeeds = optionDict.get("-f").split(",");
            for (String feed : separateFeeds) {
                feedKey.add(feed);
            }
        } catch (NullPointerException e){
            System.out.println("Missing feeds, lease provide feeds data");
            System.exit(1);
        }


        String sf = optionDict.get("-sf");

        UtilsNamedEntities listUtils = new UtilsNamedEntities();
        if (computeNamedEntities) {
            if (heuristic == null || !listUtils.getHeuristics().contains(heuristic)) {
                computeNamedEntities = false;
                printFeed = true;
            }
        }


        if (optionDict.containsKey("-sf")) {
            if (sf == null) {
                return new Config(dataPath, printFeed, computeNamedEntities, printHelp, feedKey, heuristic, "cat");
            } else if ((sf.equals("cat"))) {
                return new Config(dataPath, printFeed, computeNamedEntities, printHelp, feedKey, heuristic, "cat");
            } else if (sf.equals("topic")) {
                return new Config(dataPath, printFeed, computeNamedEntities, printHelp, feedKey, heuristic, "topic");
            } else {
                System.out.println("Invalid inputs");
                System.exit(1);
                return null;
            }
        } else {
            return new Config(dataPath, printFeed, computeNamedEntities, printHelp, feedKey, heuristic, null);
        }

    }
}