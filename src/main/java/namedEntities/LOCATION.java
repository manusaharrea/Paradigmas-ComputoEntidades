package namedEntities;

import java.util.List;

public class LOCATION extends NamedEntity {
    private float latitud;
    private float longitud;

    public LOCATION(String id, List<String> list, float latitud, float longitud) {
        super(id, list);
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public float getLatitud() {
        return latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLatitud(float num) {
        this.latitud = num;
    }

    public void setLongitud(float num) {
        this.longitud = num;
    }

    public void print() {
        System.out.println("\t" + id + " (" + amount + ")" + ", located at " + "(" + latitud + "," + longitud + ")");
    }
}