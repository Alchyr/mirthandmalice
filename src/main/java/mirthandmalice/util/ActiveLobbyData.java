package mirthandmalice.util;

public class ActiveLobbyData {
    public String name;
    public int ascension;

    public String hostName;
    public String otherName;

    public boolean hostIsMirth;

    public ActiveLobbyData(String name, int ascension, String hostName, String otherName, boolean hostIsMirth)
    {
        this.name = name;
        this.ascension = ascension;
        this.hostName = hostName;
        this.otherName = otherName;
        this.hostIsMirth = hostIsMirth;
    }
}
