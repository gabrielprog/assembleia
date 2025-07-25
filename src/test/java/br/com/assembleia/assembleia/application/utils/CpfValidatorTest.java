package br.com.assembleia.assembleia.application.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CPF Validator Business Logic Tests")
class CpfValidatorTest {

    @Test
    @DisplayName("Should return false for null CPF")
    void shouldReturnFalseForNullCpf() {
        assertFalse(CpfValidator.isValid(null));
    }

    @Test
    @DisplayName("Should return false for empty CPF")
    void shouldReturnFalseForEmptyCpf() {
        assertFalse(CpfValidator.isValid(""));
        assertFalse(CpfValidator.isValid("   "));
    }

    @Test
    @DisplayName("Should return false for CPF with wrong length")
    void shouldReturnFalseForCpfWithWrongLength() {
        assertFalse(CpfValidator.isValid("123456789")); // 9 digits
        assertFalse(CpfValidator.isValid("123456789012")); // 12 digits
        assertFalse(CpfValidator.isValid("12345")); // 5 digits
    }

    @ParameterizedTest
    @ValueSource(strings = {"00000000000", "11111111111", "22222222222", "33333333333", "44444444444", "55555555555", "66666666666", "77777777777", "88888888888", "99999999999"})
    @DisplayName("Should return false for CPF with all same digits")
    void shouldReturnFalseForCpfWithAllSameDigits(String cpf) {
        assertFalse(CpfValidator.isValid(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {"11144477735", "111.444.777-35"})
    @DisplayName("Should return true for valid CPF")
    void shouldReturnTrueForValidCpf(String cpf) {
        assertTrue(CpfValidator.isValid(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678901", "123.456.789-01", "98765432345", "987.654.323-45"})
    @DisplayName("Should return false for invalid CPF")
    void shouldReturnFalseForInvalidCpf(String cpf) {
        assertFalse(CpfValidator.isValid(cpf));
    }

    @Test
    @DisplayName("Should handle CPF with formatting characters")
    void shouldHandleCpfWithFormattingCharacters() {
        String unformattedValid = "11144477735";
        String formattedValid = "111.444.777-35";
        String formattedWithSpaces = " 111.444.777-35 ";
        
        assertTrue(CpfValidator.isValid(unformattedValid));
        assertTrue(CpfValidator.isValid(formattedValid));
        assertTrue(CpfValidator.isValid(formattedWithSpaces));
        
        // All should be equivalent
        assertEquals(
            CpfValidator.isValid(unformattedValid),
            CpfValidator.isValid(formattedValid)
        );
    }

    @Test
    @DisplayName("Should return false for CPF with letters")
    void shouldReturnFalseForCpfWithLetters() {
        assertFalse(CpfValidator.isValid("1234567890a"));
        assertFalse(CpfValidator.isValid("abcdefghijk"));
        assertFalse(CpfValidator.isValid("111.444.777-3a"));
    }

    @Test
    @DisplayName("Should validate business logic for specific CPF")
    void shouldValidateBusinessLogicForSpecificCpf() {
        // Test known valid CPF
        String validCpf = "11144477735";
        assertTrue(CpfValidator.isValid(validCpf));
        
        // Test known invalid CPF (wrong check digits)
        String invalidCpf = "11144477736"; // Last digit changed
        assertFalse(CpfValidator.isValid(invalidCpf));
        
        // Test known invalid CPF (wrong first check digit)
        String invalidCpf2 = "11144477745"; // Second to last digit changed
        assertFalse(CpfValidator.isValid(invalidCpf2));
    }

    @Test
    @DisplayName("Should handle special characters correctly")
    void shouldHandleSpecialCharactersCorrectly() {
        String cpfWithSpecialChars = "111@444#777$35";
        String validCpf = "11144477735";
        
        // Should extract only numbers and validate
        assertEquals(
            CpfValidator.isValid(validCpf),
            CpfValidator.isValid(cpfWithSpecialChars)
        );
    }
}
