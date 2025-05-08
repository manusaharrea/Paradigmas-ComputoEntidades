package namedEntities;

import java.util.List;

public class ORGANIZATION extends NamedEntity {
    private String country;

    public ORGANIZATION(String id, List<String> List, String country) {
        super(id, List);
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void print() {
        System.out.println("\t" + id + " (" + amount + ")" + ", located in" + country);
    }
}