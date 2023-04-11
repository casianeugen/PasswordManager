package com.example.passwordmanager;

import java.security.SecureRandom;

public class GeneratePassword {
    String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String lowerCase = "abcdefghijklmnopqrstuvwxyz";
    String numbers = "0123456789";
    String symbols = "!@#$%^&*()_+-=[]{}\\|;:'\",.<>/?";
    SecureRandom random;
    StringBuilder passwordBuilder;

    String seekBarGenerate(int length, boolean allowLower, boolean allowUpper,
                           boolean allowNumbers, boolean allowSymbols) {
        random = new SecureRandom();
        passwordBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(generatePassword(allowLower, allowUpper, allowNumbers, allowSymbols).length());
            passwordBuilder.append(generatePassword(allowLower, allowUpper, allowNumbers, allowSymbols).charAt(index));
        }
        return passwordBuilder.toString();
    }

    String easyToSayGenerate(int length, boolean allowLower, boolean allowUpper,
                             boolean allowNumbers, boolean allowSymbols) {
        random = new SecureRandom();
        passwordBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random
                    .nextInt(generatePassword(allowLower, allowUpper, allowNumbers, allowSymbols)
                            .length());
            passwordBuilder
                    .append(generatePassword(allowLower, allowUpper, allowNumbers, allowSymbols)
                            .charAt(index));
        }
        return passwordBuilder.toString();
    }

    String easyToReadGenerate(int length, boolean allowLower, boolean allowUpper,
                              boolean allowNumbers, boolean allowSymbols) {
        random = new SecureRandom();
        passwordBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(generatePassword(allowLower, allowUpper, allowNumbers, allowSymbols).length());
            passwordBuilder
                    .append(generatePassword(allowLower, allowUpper, allowNumbers, allowSymbols)
                            .charAt(index));
        }
        // replace ambiguous characters with easier-to-read alternatives
        String password = passwordBuilder.toString();
        password = password.replace("0", "o");
        password = password.replace("O", "o");
        password = password.replace("l", "L");
        password = password.replace("I", "i");

        return password;
    }

    String generatePassword(boolean allowLower, boolean allowUpper,
                            boolean allowNumbers, boolean allowSymbols) {
        StringBuilder password = new StringBuilder();
        if (allowLower)
            password.append(lowerCase);
        if (allowUpper)
            password.append(upperCase);
        if (allowNumbers)
            password.append(numbers);
        if (allowSymbols)
            password.append(symbols);

        return password.toString();
    }
}
