package client;

import javax.swing.SwingUtilities;

public class ClientApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientCore clientCore = new ClientCore();
            clientCore.start(); // Start the thread
        });
    }
}