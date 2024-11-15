package client.handler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientStatisticHandler extends ClientFeatureHandler{
    public ClientStatisticHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        super(serverInput, serverOutput);
    }

    @Override
    public void run() {

    }
}
