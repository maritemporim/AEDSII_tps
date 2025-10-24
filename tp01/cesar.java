meudeus import java.util.Scanner;
import java.util.*;

public class cesar {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in); //inicializando o scanner 
        String palavra = MyIO.readLine(); // lendo a palavra incial
        while(!comparar(palavra, "FIM")){ // chama a função comparar, toda vez que o usuario digitar uma palavra diferente de "fim"
            String str = cifra(palavra); // chama a função para codificar a palavra
            MyIO.println(str); // printa a cifra
            palavra = MyIO.readLine(); // leitura da proxima palavra
        }
       sc.close();
    }

    public static String cifra(String a){ // função para codificação de acordo com a cifra de cesar
        String b = ""; // incialização da string
        for(int i = 0; i < a.length(); i++){
            b += (char)(a.charAt(i) + 3); // conversando para char e adicionando mais 3 de acordo com a tabela ascii
        }
        return b; // retorna a palavra codificada
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
