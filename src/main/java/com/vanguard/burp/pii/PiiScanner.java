package com.vanguard.burp.pii;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.Registration;
import burp.api.montoya.http.handler.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PiiScanner implements BurpExtension {

    private MontoyaApi api;
    private Registration registration;

    // Regex para detectar CPF formatado (XXX.XXX.XXX-XX) ou sequência de 11 dígitos
    private static final Pattern CPF_PATTERN = Pattern.compile("(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})|\\b\\d{11}\\b");

    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName("PiiScanner Extension");
        api.logging().logToOutput("PiiScanner Extension Initialized");

        // Define o handler para interceptar requisições e respostas HTTP
        HttpHandler handler = new HttpHandler() {
            @Override
            public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
                scanForPii(httpRequestToBeSent.bodyToString());
                return RequestToBeSentAction.continueWith(httpRequestToBeSent);
            }

            @Override
            public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
                scanForPii(httpResponseReceived.bodyToString());
                return ResponseReceivedAction.continueWith(httpResponseReceived);
            }
        };
        // Registra o handler para interceptar o tráfego HTTP
        this.registration = api.http().registerHttpHandler(handler);
    }

    public void scanForPii(String responseBody) {
        Matcher matcher = CPF_PATTERN.matcher(responseBody);

        while (matcher.find()) {
            String foundCpf = matcher.group();
            if (isValidCpf(foundCpf)) api.logging().logToOutput("PII Detected: Valid CPF found - " + foundCpf);
            else api.logging().logToOutput("PII Detected: Invalid CPF found - " + foundCpf);
        }
    }
    public boolean isValidCpf(String cpf) {
        // Remove os caracteres não numéricos
        cpf = cpf.replaceAll("[^\\d]", "");

        if (cpf.length() != 11) {
            return false; // CPF precisa ter 11 dígitos
        }

        // Verifica CPFs falsos como 111.111.111.11, 222.222.222.22, etc.
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int firstCheckDigit = 11 - (sum % 11);
        if (firstCheckDigit == 10 || firstCheckDigit == 11) {
            firstCheckDigit = 0;
        }

        // Calcula o segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        int secondCheckDigit = 11 - (sum % 11);
        if (secondCheckDigit == 10 || secondCheckDigit == 11) {
            secondCheckDigit = 0;
        }

        // Verifica se os dois dígitos verificadores estão corretos
        return cpf.charAt(9) == (char) ('0' + firstCheckDigit) && cpf.charAt(10) == (char) ('0' + secondCheckDigit);
    }
}
