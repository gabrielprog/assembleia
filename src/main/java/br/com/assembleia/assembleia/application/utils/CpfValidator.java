package br.com.assembleia.assembleia.application.utils;

/**
 * Utilitário para validação de CPF
 */
public class CpfValidator {
    
    /**
     * Valida se um CPF é válido segundo as regras brasileiras
     * 
     * @param cpf CPF a ser validado (pode conter ou não formatação)
     * @return true se o CPF for válido, false caso contrário
     */
    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }
        
        cpf = cpf.replaceAll("[^0-9]", "");
        
        if (cpf.length() != 11) {
            return false;
        }
        
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int primeiroDigitoVerificador = 11 - (soma % 11);
            if (primeiroDigitoVerificador >= 10) {
                primeiroDigitoVerificador = 0;
            }
            
            if (Character.getNumericValue(cpf.charAt(9)) != primeiroDigitoVerificador) {
                return false;
            }
            
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int segundoDigitoVerificador = 11 - (soma % 11);
            if (segundoDigitoVerificador >= 10) {
                segundoDigitoVerificador = 0;
            }
            
            return Character.getNumericValue(cpf.charAt(10)) == segundoDigitoVerificador;
            
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
