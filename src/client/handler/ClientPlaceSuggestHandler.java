package client.handler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientPlaceSuggestHandler extends ClientFeatureHandler{
    @Override
    public void run() {

    }

    public ClientPlaceSuggestHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        super(serverInput, serverOutput);
    }
}
