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
####A extensão opera interceptando requisições e respostas HTTP, buscando por possíveis CPFs em seu conteúdo. Ao detectar uma sequência de CPF, a extensão:

- Valida a sequência para assegurar que ela representa um CPF legítimo.
- Exibe uma mensagem de log no Burp Suite indicando se foi encontrado um CPF válido ou inválido.
