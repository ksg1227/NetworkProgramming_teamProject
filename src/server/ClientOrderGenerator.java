package server;

public class ClientOrderGenerator {
    private static int order = 0;

    public static int getClientOrder() {
        order++;
        return order;
    }
}
