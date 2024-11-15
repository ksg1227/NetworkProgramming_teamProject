package client.handler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientScheduleHandler extends ClientFeatureHandler{
    public ClientScheduleHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        super(serverInput, serverOutput);
    }

    @Override
    public void run() {

    }
}
