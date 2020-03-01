package mirthandmalice.util;

import com.codedisaster.steamworks.SteamID;

import java.util.Comparator;

public class LobbyData {
    public SteamID id;
    public String name;
    public int ascension;
    public boolean isPublic;
    public boolean hostIsMirth;
    public boolean isValid = true;

    public void invalidate(String newName)
    {
        id = null;
        this.name = newName;
        isPublic = false;
        isValid = false;
        ascension = 0;
    }

    public static class LobbyDataComparer implements Comparator<LobbyData>
    {
        @Override
        public int compare(LobbyData o1, LobbyData o2) {
            if (o1.isPublic ^ o2.isPublic)
            {
                return o1.isPublic ? -1 : 1;
            }
            else
            {
                return o1.name.compareTo(o2.name);
            }
        }

        @Override
        public boolean equals(Object obj) {
            return obj.getClass().equals(this.getClass());
        }
    }
}
