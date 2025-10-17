import java.util.*;

public class anagrama {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            String linha = sc.nextLine();

            if (teste(linha, "FIM")) break;

            String[] partes = linha.split(" - ");
            String a = partes[0].toLowerCase();
            String b = partes[1].toLowerCase();

            if (anag(a, b)) {
                System.out.println("SIM");
            } else {
                System.out.println("N\u00C3O");
            }
        }
        sc.close();
    }

    public static boolean anag(String a, String b) {
        if (a.length() != b.length()) return false;

        int[] count = new int[256];
        for (int i = 0; i < a.length(); i++) {
            count[a.charAt(i)]++;
            count[b.charAt(i)]--;
        }

        for (int c : count) {
            if (c != 0) return false;
        }
        return true;
    }

    public static boolean teste(String a, String x) {
        if (a.length() != x.length()) return false;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != x.charAt(i)) return false;
        }
        return true;
    }
}
