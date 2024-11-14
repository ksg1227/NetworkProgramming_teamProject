package client.handler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientChatHandler extends ClientFeatureHandler{
    public ClientChatHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        super(serverInput, serverOutput);
    }

    @Override
    public void run() {

    }
}
