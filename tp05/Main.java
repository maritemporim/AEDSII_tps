import java.io.*;
import java.util.*;

public class Main {

    static final String CAMINHO_CSV = "/tmp/games.csv"; 
    static final String MATRICULA   = "885948";

    static long comparacoes = 0; // contabilizar no log

    public static void main(String[] args) {
        long inicio = System.currentTimeMillis();

        // leitura de ids
        List<Integer> ids = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_CSV))) {
            br.readLine(); // cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] t = parseCSV(linha);
                if (t.length > 0) {
                    Integer id = tryParseInt(t[0]);
                    if (id != null) ids.add(id);
                }
            }
        } catch (IOException e) {
            System.err.println("erro");
            return;
        }

        Collections.sort(ids);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            String s;
            while ((s = in.readLine()) != null) {
                s = s.trim();
                if (s.equals("FIM")) break; // encerra o programa

                Integer alvo = tryParseInt(s);
                if (alvo == null) continue; // ignora lixo

                boolean achou = buscaBinaria(ids, alvo);
                System.out.println(achou ? "SIM" : "NAO");
            }
        } catch (IOException e) {
            // silencioso
        }

        long fim = System.currentTimeMillis();
        long tempoMs = fim - inicio;

        String linhaLog = MATRICULA + "\t" + tempoMs + "\t" + comparacoes + System.lineSeparator();
        try (FileWriter fw = new FileWriter(MATRICULA + "_binaria.txt")) {
            fw.write(linhaLog);
        } catch (IOException e) {
            System.err.println("erro");
        }
    }

    // -------- binária contando comparações --------
    static boolean buscaBinaria(List<Integer> v, int x) {
        int esq = 0, dir = v.size() - 1;
        while (esq <= dir) {
            int meio = (esq + dir) >>> 1;
            int val = v.get(meio);

            comparacoes++;                 // comparação de igualdade
            if (val == x) return true;

            comparacoes++;                 // comparação de ordem
            if (val < x) esq = meio + 1;
            else         dir = meio - 1;
        }
        return false;
    }

    static Integer tryParseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return null; }
    }

    static String[] parseCSV(String linha) {
        List<String> out = new ArrayList<>();
        boolean aspas = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') aspas = !aspas;
            else if (c == ',' && !aspas) { out.add(sb.toString()); sb.setLength(0); }
            else sb.append(c);
        }
        out.add(sb.toString());
        return out.toArray(new String[0]);
    }
}
