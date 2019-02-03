package propertymanager;

import main.Util;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PropertyManager {

    private static PropertyManager pm = null;

    private Gson gson;
    private Language language;
    private Region region;

    private Map<String, String> props;

    public static PropertyManager getPropertyManager() {
        if (pm == null)
            pm = new PropertyManager(Language.EN, Region.NA);
        return pm;
    }

    private PropertyManager(Language l, Region r) {
        gson = new Gson();
        props = new HashMap<>();

        setLanguage(l);
        setRegion(r);
    }

    public void setLanguage(Language l){
        language = l;

        String json = "";
        try {
            json = Util.readFile("props/" + language.name() + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> map = (Map<String,String>) gson.fromJson(json, props.getClass());

        for(String s : map.keySet())
            props.put(s, map.get(s));
    }

    public void setRegion(Region r) {
        region = r;
    }

    public String get(Prop key) {
        return props.get(key.name());
    }

    public String version() {
        return String.format("v%s.%s.%s",
                get(Prop.major_version),
                get(Prop.minor_version),
                get(Prop.patch_version));
    }

    public String opggUrl() {
        return String.format(get(Prop.opgg_url), region.name().toLowerCase());
    }

    public enum Prop {
        title,
        major_version,
        minor_version,
        patch_version,
        stylesheet,
        icon,
        icon_small,
        copyright,
        website,
        join,
        info,
        loading,
        season_stats,
        game,
        games,
        no_games,
        last_game,
        past_games,
        close,
        win,
        loss,
        streak,
        opgg_url
    }
}
