import java.util.Random;
import java.util.Scanner;

public class Aleatorio {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); // inicializando scanner
        Random gerador = new Random(); 
        gerador.setSeed(4); 

        while (true) {
            String linha = sc.nextLine(); //lendo a primeira palavra
            if (comparar(linha, "FIM")) break; // comparando as duas palavras por meio da função comparar, se a palavra for igual a "FIM" o sistema acaba
            char l1 = (char) ('a' + (Math.abs(gerador.nextInt()) % 26)); // sorteia a primeira letra aleatória
            char l2 = (char) ('a' + (Math.abs(gerador.nextInt()) % 26)); // sorteia a segunda letra aleatória

            String novo = ""; //inicializando variavel
            for (int i = 0; i < linha.length(); i++) {
                char caracter = linha.charAt(i); //carcater da posição atual de i
                if (caracter == l1) { // se caracter for igual a primeira letra sorteada
                    novo += l2; //substitui pela l2
                } else { // se não
                    novo += caracter; // mantem o caracter otiginal
                }
            }

            System.out.println(novo); //imprime o novo
        }
        sc.close(); //finalizando scanner
    }

     public static boolean comparar(String a, String b){ // função para comparar duas strings
            if(a.length() != b.length()) return false; // se elas tiverem tamanho diferente ja retorna false

            else{ // se não
                for(int i = 0; i < a.length(); i++){
                    if(a.charAt(i) != b.charAt(i)){ // compara caracter por caracter
                        return false;}
                }
                return true;
            }
        }
}

