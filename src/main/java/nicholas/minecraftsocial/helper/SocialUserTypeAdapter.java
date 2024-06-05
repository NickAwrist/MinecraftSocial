package nicholas.minecraftsocial.helper;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import nicholas.minecraftsocial.SocialUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class SocialUserTypeAdapter extends TypeAdapter<SocialUser> {

    @Override
    public void write(JsonWriter out, SocialUser user) throws IOException {
        out.beginObject();
        out.name("uuid").value(user.getUuid().toString());
        out.name("username").value(user.getUsername());
        out.name("friendsList").beginArray();
        for (UUID uuid : user.getFriendsList()) {
            out.value(uuid.toString());
        }
        out.endArray();
        out.name("incomingRequests").beginArray();
        for (UUID uuid : user.getIncomingRequests()) {
            out.value(uuid.toString());
        }
        out.endArray();
        out.name("outgoingRequests").beginArray();
        for (UUID uuid : user.getOutgoingRequests()) {
            out.value(uuid.toString());
        }
        out.endArray();
        out.endObject();
    }

    @Override
    public SocialUser read(JsonReader in) throws IOException {
        UUID uuid = null;
        String username = null;
        ArrayList<UUID> friendsList = new ArrayList<>();
        ArrayList<UUID> incomingRequests = new ArrayList<>();
        ArrayList<UUID> outgoingRequests = new ArrayList<>();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "uuid":
                    uuid = UUID.fromString(in.nextString());
                    break;
                case "username":
                    username = in.nextString();
                    break;
                case "friendsList":
                    in.beginArray();
                    while (in.hasNext()) {
                        friendsList.add(UUID.fromString(in.nextString()));
                    }
                    in.endArray();
                    break;
                case "incomingRequests":
                    in.beginArray();
                    while (in.hasNext()) {
                        incomingRequests.add(UUID.fromString(in.nextString()));
                    }
                    in.endArray();
                    break;
                case "outgoingRequests":
                    in.beginArray();
                    while (in.hasNext()) {
                        outgoingRequests.add(UUID.fromString(in.nextString()));
                    }
                    in.endArray();
                    break;
            }
        }
        in.endObject();

        Player player = Bukkit.getPlayer(uuid);
        return new SocialUser(player, friendsList, incomingRequests, outgoingRequests);
    }
}
