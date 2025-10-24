import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa um registro de jogo e implementa Comparable para
 * ordenar e pesquisar pelo atributo 'name' (chave primária).
 */
class Games implements Comparable<Games> {
    private int id;
    private String name;
    private String releaseDate;
    private int estimatedOwners;
    private float price;
    private String[] supportedLanguages;
    private float mediaScore;
    private int achievements;
    private float userScore;
    private String[] publishers;
    private String[] developers;
    private String[] categories;
    private String[] genres;
    private String[] tags;

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public int getEstimatedOwners() { return estimatedOwners; }
    public void setEstimatedOwners(int estimatedOwners) { this.estimatedOwners = estimatedOwners; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public String[] getSupportedLanguages() { return supportedLanguages; }
    public void setSupportedLanguages(String[] supportedLanguages) { this.supportedLanguages = supportedLanguages; }
    public float getMediaScore() { return mediaScore; }
    public void setMediaScore(float mediaScore) { this.mediaScore = mediaScore; }
    public int getAchievements() { return achievements; }
    public void setAchievements(int achievements) { this.achievements = achievements; }
    public float getUserScore() { return userScore; }
    public void setUserScore(float userScore) { this.userScore = userScore; }
    public String[] getPublishers() { return publishers; }
    public void setPublishers(String[] publishers) { this.publishers = publishers; }
    public String[] getDevelopers() { return developers; }
    public void setDevelopers(String[] developers) { this.developers = developers; }
    public String[] getCategories() { return categories; }
    public void setCategories(String[] categories) { this.categories = categories; }
    public String[] getGenres() { return genres; }
    public void setGenres(String[] genres) { this.genres = genres; }
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }

    @Override
    public int compareTo(Games outro) {
        // Chave primária de pesquisa é o Name
        return this.name.compareTo(outro.name);
    }
    
    public String toString() {
        return "=> " + id + " ## " + safe(name) + " ## " + safe(releaseDate) + " ## " + estimatedOwners + " ## " +
               String.format("%.2f", price) + " ## " +
               joinClean(supportedLanguages) + " ## " +
               ((int) mediaScore) + " ## " +
               achievements + " ## " +
               ((int) userScore) + " ## " +
               joinKeepBrackets(publishers) + " ## " +
               joinKeepBrackets(developers) + " ## " +
               joinKeepBrackets(categories) + " ## " +
               joinKeepBrackets(genres) + " ## " +
               joinKeepBrackets(tags) + " ##";
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private static String joinClean(String[] arr) {
        if (arr == null) return "";
        return Arrays.toString(arr).replaceAll("^\\[|\\]$", "");
    }

    private static String joinKeepBrackets(String[] arr) {
        if (arr == null) return "[]";
        return Arrays.toString(arr);
    }
}

public class Binaria {

    static final String CAMINHO_CSV = "/tmp/games.csv";
    static final String MATRICULA   = "885948";

    static long comparacoes = 0;

    public static void main(String[] args) {
        long inicio = System.currentTimeMillis();

        List<Games> gamesOrdenadosPorNome = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_CSV))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] t = quebrarLinhaCSV(linha);
                if (t.length < 14) continue;

                Games g = new Games();
                g.setId(converterParaInt(t[0]));
                
                // *** PONTO DE CORREÇÃO CRÍTICO: Sanitização completa do nome do jogo ***
                g.setName(stripQuotes(t[1]).trim()); 
                
                g.setReleaseDate(formatarData(t[2]));
                g.setEstimatedOwners(converterParaInt(t[3]));
                g.setPrice(converterParaFloat(t[4]));
                g.setSupportedLanguages(separarLista(t[5]));
                g.setMediaScore(converterParaFloat(t[6]));
                g.setAchievements(converterParaInt(t[7]));
                g.setUserScore(converterParaFloat(t[8]));
                g.setPublishers(separarLista(t[9]));
                g.setDevelopers(separarLista(t[10]));
                g.setCategories(separarLista(t[11]));
                g.setGenres(separarLista(t[12]));
                g.setTags(separarLista(t[13]));

                gamesOrdenadosPorNome.add(g);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo CSV: " + e.getMessage());
            return;
        }

        // Ordenação pela chave primária (Name)
        Collections.sort(gamesOrdenadosPorNome);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            String s;
            while ((s = in.readLine()) != null) {
                String nomeAlvo = s.trim();
                if (nomeAlvo.equals("FIM")) break;

                // A entrada padrão é limpa com trim()
                boolean achou = buscaBinariaNome(gamesOrdenadosPorNome, nomeAlvo);
                
                System.out.println(achou ? "SIM" : "NAO");
            }
        } catch (IOException e) {
            
        }

        long fim = System.currentTimeMillis();
        long tempoMs = fim - inicio;

        String nomeArquivoLog = MATRICULA + "_binaria.txt";
        String linhaLog = MATRICULA + "\t" + tempoMs + "\t" + comparacoes + System.lineSeparator();
        
        try (FileWriter fw = new FileWriter(nomeArquivoLog)) {
            fw.write(linhaLog);
        } catch (IOException e) {
            System.err.println("Erro ao escrever arquivo de log: " + e.getMessage());
        }
    }

    /**
     * Pesquisa Binária na lista de Games usando a String do Name.
     */
    static boolean buscaBinariaNome(List<Games> v, String x) {
        int esq = 0, dir = v.size() - 1;
        while (esq <= dir) {
            int meio = (esq + dir) >>> 1;
            Games gameMeio = v.get(meio);
            
            // Compara o Name do objeto do meio com a String alvo
            int comparacao = gameMeio.getName().compareTo(x); 
            
            comparacoes++;

            if (comparacao == 0) {
                return true;
            } else if (comparacao < 0) {
                esq = meio + 1;
            } else {
                dir = meio - 1;
            }
        }
        return false;
    }

    // Funções Auxiliares de Parsing
    public static String[] quebrarLinhaCSV(String linha) {
        List<String> elementos = new ArrayList<>();
        boolean entreAspas = false;
        StringBuilder elementoAtual = new StringBuilder();

        for (int j = 0; j < linha.length(); j++) {
            char caractere = linha.charAt(j);
            if (caractere == '"') {
                entreAspas = !entreAspas;
            } else if (caractere == ',' && !entreAspas) {
                elementos.add(elementoAtual.toString().trim());
                elementoAtual.setLength(0);
            } else {
                elementoAtual.append(caractere);
            }
        }
        elementos.add(elementoAtual.toString().trim());
        return elementos.toArray(new String[0]);
    }

    public static String formatarData(String dadoBruto) {
        if (dadoBruto == null || dadoBruto.isEmpty()) return "01/01/0000";

        Map<String, String> mesesConvertidos = new HashMap<>();
        mesesConvertidos.put("Jan", "01"); mesesConvertidos.put("Feb", "02"); mesesConvertidos.put("Mar", "03"); mesesConvertidos.put("Apr", "04");
        mesesConvertidos.put("May", "05"); mesesConvertidos.put("Jun", "06"); mesesConvertidos.put("Jul", "07"); mesesConvertidos.put("Aug", "08");
        mesesConvertidos.put("Sep", "09"); mesesConvertidos.put("Oct", "10"); mesesConvertidos.put("Nov", "11"); mesesConvertidos.put("Dec", "12");

        try {
            dadoBruto = stripQuotes(dadoBruto).trim();
            String[] partes = dadoBruto.split(" ");
            String dia = "01", mes = "01", ano = "0000";

            if (partes.length == 3) {
                dia = partes[1].replace(",", "");
                mes = mesesConvertidos.getOrDefault(partes[0], "01");
                ano = partes[2];
            } else if (partes.length == 2) {
                mes = mesesConvertidos.getOrDefault(partes[0], "01");
                ano = partes[1];
            } else if (partes.length == 1) {
                ano = partes[0];
            }

            return String.format("%02d/%s/%s", Integer.parseInt(dia), mes, ano);
        } catch (Exception e) {
            return "01/01/0000";
        }
    }

    public static String[] separarLista(String dadoBruto) {
        if (dadoBruto == null || dadoBruto.trim().isEmpty()) return new String[0];

        dadoBruto = dadoBruto.replace("[", "").replace("]", "").replace("'", "").replace("\"", "").trim();

        if (dadoBruto.isEmpty()) return new String[0];

        String[] elementos = dadoBruto.split(",");
        for (int k = 0; k < elementos.length; k++) {
            elementos[k] = elementos[k].trim();
        }
        return elementos;
    }

    public static int converterParaInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    public static float converterParaFloat(String s) {
        try { return Float.parseFloat(s.trim()); } catch (Exception e) { return 0.0f; }
    }

    private static String stripQuotes(String s) {
        if (s == null) return null;
        return s.replace("\"", "");
    }
}