package propertymanager;

public enum Language {
    EN ("English"),
    DE ("Deutsch"),
    ES ("Español"),
    FR ("Français"),
    IT ("Italiano"),
    CS ("Čeština"),
    EL ("ελληνικά"),
    HU ("Magyar"),
    PL ("Polszczyzna"),
    RO ("Română"),
    PT ("Português"),
    TR ("Türkçe"),
    RU ("русский"),
    JA ("日本語"),
    KO ("한국어");

    private final String s;
    Language(String str) {s = str;}
    public String string() {return s;}
}
