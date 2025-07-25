package br.com.assembleia.assembleia.adapters.dto;

public record ErrorResponseDTO(int status, String message, long timestamp) {
    public static ErrorResponseDTO of(int status, String message) {
        return new ErrorResponseDTO(status, message, System.currentTimeMillis());
    }
}
