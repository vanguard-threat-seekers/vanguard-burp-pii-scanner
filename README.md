# PiiScanner Extension

A **PiiScanner Extension** é uma extensão para o Burp Suite desenvolvida para detectar informações de identificação pessoal (PII), especificamente CPFs do Brasil, em requisições e respostas HTTP. Esta extensão utiliza o Montoya API e implementa validações para garantir que CPFs válidos e inválidos sejam registrados, auxiliando na identificação de possíveis exposições de dados sensíveis.

## Índice
- [Características](#características)
- [Instalação](#instalação)
- [Uso](#uso)
- [Detalhes Técnicos](#detalhes-técnicos)
- [Contribuição](#contribuição)
- [Licença](#licença)

## Características
- **Interceptação HTTP**: Monitora o tráfego HTTP de requisições e respostas.
- **Detecção de CPFs**: Utiliza uma expressão regular para identificar CPFs em formato padrão (`XXX.XXX.XXX-XX`) ou sequências de 11 dígitos.
- **Validação de CPFs**: Implementa uma função para validar a estrutura e os dígitos verificadores de CPFs, garantindo que apenas CPFs válidos sejam detectados e registrados.

## Instalação

### Pré-requisitos
- **Burp Suite**: Certifique-se de ter o Burp Suite instalado.
- **JDK 11+**: A extensão é desenvolvida em Java, então é necessário ter o JDK 11 ou superior.
- **IntelliJ IDEA**: Este projeto é otimizado para ser desenvolvido no IntelliJ IDEA, mas outros IDEs compatíveis com Java podem ser usados.

### Passos para Instalação
1. Clone este repositório:
   
   ```bash
   git clone https://github.com/vanguard-threat-seekers/vanguard-burp-pii-scanner.git
   cd vanguard-burp-pii-scanner
3. Abra o projeto no IntelliJ IDEA e configure o SDK para a versão do JDK instalada.
4. Compile o projeto, gerando o arquivo .jar.
5. No Burp Suite, vá até Extender > Extensions e adicione o .jar da extensão:
  - Clique em Add.
  - Selecione o arquivo .jar compilado.
5. A extensão será carregada automaticamente e estará pronta para uso.

## Uso
### Funcionalidade de Detecção e Validação
A extensão opera interceptando requisições e respostas HTTP, buscando por possíveis CPFs em seu conteúdo. Ao detectar uma sequência de CPF, a extensão:
- Valida a sequência para assegurar que ela representa um CPF legítimo.
- Exibe uma mensagem de log no Burp Suite indicando se foi encontrado um CPF válido ou inválido.

### Logs
Os resultados das detecções serão exibidos no painel de logs do Burp Suite, com uma mensagem indicando:
- Valid CPF found - CPF válido detectado.
- Invalid CPF found - CPF inválido detectado.

### Exemplos de saída
Abaixo estão exemplos de mensagens de log:
- PII Detected: Valid CPF found - 123.456.789-09
- PII Detected: Invalid CPF found - 111.111.111-11

## Detalhes Técnicos

### Estrutura do Projeto
- Pacote `com.vanguard.burp.pii`: Contém a classe `PiiScanner`, que implementa a extensão.
- Classe `PiiScanner`: Responsável por interceptar requisições e respostas HTTP, detectar CPFs, validar CPFs e registrar as informações.

### Principais Componentes

1. **Regex para CPF:**
   
   - O padrão `CPF_PATTERN` utiliza expressões regulares para detectar CPFs em formato `XXX.XXX.XXX-XX` ou como uma sequência de 11 dígitos.
3. **Validação de CPF:**
   
   - A função `isValidCpf` verifica se o CPF possui 11 dígitos, não é uma sequência repetitiva (como `111.111.111-11`) e calcula os dígitos verificadores de acordo com o algoritmo brasileiro.
4. **Interceptação HTTP:**
   
   - O `HttpHandler` define os métodos `handleHttpRequestToBeSent` e `handleHttpResponseReceived` para processar requisições e respostas HTTP e verificar a presença de CPFs no corpo da requisição/resposta.

### Fluxo de Validação de CPF

1. O CPF detectado é extraído do corpo da requisição/resposta HTTP.
2. A função `isValidCpf` remove caracteres não numéricos e calcula os dígitos verificadores.
3. Se o CPF for válido, é registrado como "Valid CPF found"; caso contrário, "Invalid CPF found".

### Exemplo de Código
Abaixo, um trecho de código com a função de validação:

```bash
public boolean isValidCpf(String cpf) {
    cpf = cpf.replaceAll("[^\\d]", "");

    if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
        return false;
    }

    int sum = 0;
    for (int i = 0; i < 9; i++) {
        sum += (cpf.charAt(i) - '0') * (10 - i);
    }
    int firstCheckDigit = 11 - (sum % 11);
    if (firstCheckDigit == 10 || firstCheckDigit == 11) firstCheckDigit = 0;

    sum = 0;
    for (int i = 0; i < 10; i++) {
        sum += (cpf.charAt(i) - '0') * (11 - i);
    }
    int secondCheckDigit = 11 - (sum % 11);
    if (secondCheckDigit == 10 || secondCheckDigit == 11) secondCheckDigit = 0;

    return cpf.charAt(9) == (char) ('0' + firstCheckDigit) && cpf.charAt(10) == (char) ('0' + secondCheckDigit);
}
    
