package client.handler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ClientVoteHandler extends ClientFeatureHandler{
    public ClientVoteHandler(ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        super(serverInput, serverOutput);
    }

    @Override
    public void run() {

    }
}
