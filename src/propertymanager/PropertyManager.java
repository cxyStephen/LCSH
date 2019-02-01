package propertymanager;


import java.util.HashMap;

public class PropertyManager {
    Language language;
    Region region;

    HashMap<String, String> props;

    public PropertyManager(Language l, Region r) {
        //read xml
        //load hashmap
    }

    public String version() {
        return "4.0.0";
    }
}
