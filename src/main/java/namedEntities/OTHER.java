package namedEntities;

import java.util.List;

public class OTHER extends NamedEntity {

    public OTHER(String id, List<String> List) {
        super(id, List);
    }

    public void print() {
        System.out.println("\t" + id + " (" + amount + ")");
    }
}