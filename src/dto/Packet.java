package dto;

public record Packet<T>(ClientState clientState, T body) {
}
