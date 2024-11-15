package dto;

import java.io.Serializable;

public record Packet<T>(ClientState clientState, T body) implements Serializable {
}
