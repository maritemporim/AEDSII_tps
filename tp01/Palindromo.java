import java.util.Scanner;

public class Palindromo {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in); //inicializando o scanner 
        String letras = sc.nextLine(); //salvando a palavra 
        String str; // criando nova string
 
        while(!comparar(letras, "FIM")){ // enquanto a comparação na função comparar entre letras e a palavra "fim" for false
            str = trocar(letras); // programa chamando a função trocar
            if(comparar(letras, str)){ // se a comparação entre a string letras e a string str for true
                System.out.println("SIM"); // o sistema imprime sim
            }
            else{ // se não
                System.out.println("NAO"); // o sistema imprime "não"
            }
            letras = sc.nextLine(); // lendo a proxima palavra 
        }
        sc.close(); // fechamento do scanner

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
    
    public static String trocar(String letras){ // função trocar para inverter as palavras
        String str = ""; // para a string não ser inicializada vazia
        int tam = letras.length(); // pegando o tamanho do palavra para a condição do for
        for(int i = tam-1; i >= 0; i--){
            str += letras.charAt(i); // completa o str com os caracteres invertidos de letras por meio do charAt
        }
        return str; // retorna o str
    }
}

    




        