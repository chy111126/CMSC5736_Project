package cuhk.cse.cmsc5736project.objects;

/**
 * Created by TCC on 12/10/2017.
 */

public class Dessert {

    private String name;
    private String description;

    public Dessert() {
    }

    public Dessert(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}