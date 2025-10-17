import java.util.Scanner;

public class inversao {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        while(true){
            String a = sc.nextLine();
            if(comparar(a, "FIM")) break;
            String b = inverter(a);
            System.out.println(b);
        }

        sc.close();
    }

    public static String inverter(String a){
        String b = "";
        for(int i = 0; i < a.length(); i++){
            b += a.charAt(a.length() - i-1); }
        return b;
        }

    public static boolean comparar(String a, String b){
        if(a.length() != b.length()) { return false; }
        else{
            for(int i = 0; i < a.length(); i++){
                if(a.charAt(i) != b.charAt(i)) { return false; }
            }

            return true;
        }
    }
}
        
        
    