package namedEntities;

import java.util.List;

public class PERSON extends NamedEntity{
    private Integer age;

    public PERSON(String id, List<String> List, int age) {
        super(id, List);
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public void print() {
        System.out.println("\t" + id + " (" + amount + ")" + ", age:" + age);
    }
}