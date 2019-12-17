package mirthandmalice.util;

import com.codedisaster.steamworks.SteamAuth;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamUserCallback;

public class SteamUserCallbacks implements SteamUserCallback {
    @Override
    public void onValidateAuthTicket(SteamID steamID, SteamAuth.AuthSessionResponse authSessionResponse, SteamID steamID1) {

    }

    @Override
    public void onMicroTxnAuthorization(int i, long l, boolean b) {

    }
}
