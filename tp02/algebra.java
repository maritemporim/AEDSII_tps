import java.util.Scanner;

class algebra {
    static Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        int qtdVariaveis = entrada.nextInt();

        while (qtdVariaveis > 0) {
            boolean[] valores = new boolean[qtdVariaveis];

            for (int i = 0; i < qtdVariaveis; i++) {
                valores[i] = entrada.next("[0-9]").charAt(0) != '0';
            }

            String linha = entrada.nextLine();
            String expressao = montarExpressao(linha, valores);

            for (int i = expressao.length() - 1; i > 0; i--) {
                if (expressao.charAt(i) == '(') {
                    String operador = pegarOperador(expressao, i);
                    boolean[] argumentos = pegarArgumentos(expressao, i);

                    i -= operador.length();

                    expressao = substituir(expressao, i, calcular(operador, argumentos) ? "1" : "0");
                }
            }

            System.out.println(expressao);

            qtdVariaveis = entrada.nextInt();
        }
    }

    static String montarExpressao(String linha, boolean[] valores) {
        StringBuilder novaExpressao = new StringBuilder();

        for (int i = 0; i < linha.length(); i++) {
            char letra = linha.charAt(i);
            if (letra != ' ') {
                if (letra >= 'A' && letra <= 'Z') {
                    novaExpressao.append(valores[letra - 'A'] ? '1' : '0');
                } else {
                    novaExpressao.append(letra);
                }
            }
        }

        return novaExpressao.toString();
    }

    static String pegarOperador(String expressao, int indice) {
        StringBuilder operador = new StringBuilder();

        do {
            indice--;
            operador.append(expressao.charAt(indice));
        } while (indice > 0 && expressao.charAt(indice - 1) >= 'a' && expressao.charAt(indice - 1) <= 'z');

        return operador.reverse().toString();
    }

    static boolean[] pegarArgumentos(String expressao, int indice) {
        StringBuilder numeros = new StringBuilder();

        while (indice < expressao.length() - 1 && expressao.charAt(indice + 1) != ')') {
            indice++;
            if (expressao.charAt(indice) == '0' || expressao.charAt(indice) == '1') {
                numeros.append(expressao.charAt(indice));
            }
        }

        String textoValores = numeros.toString();
        boolean[] argumentos = new boolean[textoValores.length()];

        for (int i = 0; i < textoValores.length(); i++) {
            argumentos[i] = textoValores.charAt(i) == '1';
        }

        return argumentos;
    }

    static String substituir(String expressao, int posicao, String novoValor) {
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < posicao; i++) {
            resultado.append(expressao.charAt(i));
        }

        for (int i = posicao; i < expressao.length(); i++) {
            if (expressao.charAt(i) == ')') {
                resultado.append(novoValor);
                i++;
                while (i < expressao.length()) {
                    resultado.append(expressao.charAt(i));
                    i++;
                }
            }
        }

        return resultado.toString();
    }

    static boolean calcular(String operador, boolean[] argumentos) {
        boolean resposta = false;

        switch (operador) {
            case "not":
                resposta = !argumentos[0];
                break;

            case "or":
                for (int i = 0; i < argumentos.length; i++) {
                    if (argumentos[i]) {
                        resposta = true;
                        i = argumentos.length;
                    }
                }
                break;

            case "and":
                resposta = true;
                for (int i = 0; i < argumentos.length; i++) {
                    if (!argumentos[i]) {
                        resposta = false;
                        i = argumentos.length;
                    }
                }
                break;
        }

        return resposta;
    }
}
