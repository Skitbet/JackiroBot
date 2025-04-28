package gay.skitbet.jackiro.utils;

public enum JackiroModule {
    MUSIC("Music", "Allow server members to listen to music in VC!"),
    UTILITIES("Utilities", "Bot utilies like /info and more!");

    private final String displayName;
    private final String description;

    JackiroModule(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
