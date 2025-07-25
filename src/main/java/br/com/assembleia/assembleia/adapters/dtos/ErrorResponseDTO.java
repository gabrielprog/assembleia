package br.com.assembleia.assembleia.adapters.dtos;

public record ErrorResponseDTO(int status, String message, long timestamp) {
    public static ErrorResponseDTO of(int status, String message) {
        return new ErrorResponseDTO(status, message, System.currentTimeMillis());
    }
}
