package bolomagic.in.AdaptorAndParse;

public class HomeGridParse {

    String id;
    String icon_url;
    String game_name;
    String game_developer;

    public HomeGridParse(String id, String icon_url, String game_name, String game_developer) {
        this.id = id;
        this.icon_url = icon_url;
        this.game_name = game_name;
        this.game_developer = game_developer;
    }

    public String getId() {
        return id;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public String getGame_name() {
        return game_name;
    }

    public String getGame_developer() {
        return game_developer;
    }
}
