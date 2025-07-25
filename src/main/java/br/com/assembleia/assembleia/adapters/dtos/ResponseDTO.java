package br.com.assembleia.assembleia.adapters.dtos;

public record ResponseDTO(int status, String message, long timestamp) {
    public static ResponseDTO of(int status, String message) {
        return new ResponseDTO(status, message, System.currentTimeMillis());
    }
}
