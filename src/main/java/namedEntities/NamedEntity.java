package namedEntities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Lista de Topics: POLITICS, SPORTS, ECONOMY, HEALTH, TECHNOLGY, CULTURE, SCIENCE, ENTERTAINMENT, OTHER

public class NamedEntity implements Serializable {
    protected String id;
    protected int amount;
    private List<String> Topic;

    public NamedEntity(String id, List<String> List) {
        this.id = id;
        amount = 1;
        Topic = new ArrayList<String>();
        Topic.addAll(List);
    }

    public String getId() {
        return id;
    }

    public Integer getAmount() {
        return amount;
    }

    public List<String> getTopic() {
        return Topic;
    }

    public void addAmount() {
        amount++;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    public void print() {
        System.out.println("\t" + id + "(" + amount + ")");
    }
}
