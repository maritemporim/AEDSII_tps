import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

class Game {
    // atributos
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

    // getters e setters
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
}

public class Binario {
    static final String CSV = "/tmp/games.csv"; // caminho do arquivo
    static final String MATRICULA = "885948"; // matrícula pra gerar o log
    static long comparacoes = 0; // contador de comparações da busca binária

    public static void main(String[] args) {
        // le o CSV e guarda todos os jogos num mapa id
        Map<Integer, Game> porId = carregarCSV(CSV);
        List<Game> vetor = new ArrayList<>(); 
        List<String> consultas = new ArrayList<>(); 

        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String s;
            while ((s = in.readLine()) != null) {
                s = s.trim();
                if (s.equals("FIM")) break;
                if (s.isEmpty()) continue;
                try {
                    int id = Integer.parseInt(s);
                    Game g = porId.get(id);
                    if (g != null) vetor.add(g);
                } catch (NumberFormatException ignore) {}
            }

            // ordena o vetor pelo nome do jogo (pra poder fazer busca binária)
            vetor.sort((a, b) -> {
                int c = a.getName().compareToIgnoreCase(b.getName());
                if (c != 0) return c;
                return Integer.compare(a.getId(), b.getId());
            });

            while ((s = in.readLine()) != null) {
                s = s.trim();
                if (s.equals("FIM")) break;
                if (!s.isEmpty()) consultas.add(s);
            }
        } catch (IOException ignore) {}

        // fazendo as buscas e contando tempo e comparações
        long ini = System.nanoTime();
        for (String q : consultas) {
            boolean ok = buscaBinariaPorNome(vetor, q);
            System.out.println(ok ? " SIM" : " NAO");
        }
        long fim = System.nanoTime();

        // tempo em ms
        double ms = (fim - ini) / 1_000_000.0;

        // cri o arquivo de log com matrícula, tempo e comparações
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(MATRICULA + "_binaria.txt"), StandardCharsets.UTF_8))) {
            pw.println(MATRICULA + "\t" + String.format(Locale.US, "%.3f", ms) + "\t" + comparacoes);
        } catch (IOException ignore) {}
    }

    // busca binária
    static boolean buscaBinariaPorNome(List<Game> v, String alvo) {
        int l = 0, r = v.size() - 1;
        while (l <= r) {
            int m = (l + r) >>> 1;
            String name = v.get(m).getName();
            comparacoes++;
            int c = alvo.compareToIgnoreCase(name);
            if (c == 0) return true; // achou
            if (c < 0) r = m - 1; // procura na esquerda
            else l = m + 1; // procura na direita
        }
        return false; // não achou
    }

    // lê o CSV e transforma cada linha num objeto Game
    static Map<Integer, Game> carregarCSV(String caminho) {
        Map<Integer, Game> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho, StandardCharsets.UTF_8))) {
            br.readLine(); // pula o cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] t = quebrarLinhaCSV(linha);
                if (t.length < 14) continue;
                Game g = new Game();
                g.setId(convInt(t[0]));
                g.setName(t[1]);
                g.setReleaseDate(formatarData(t[2]));
                g.setEstimatedOwners(convInt(t[3]));
                g.setPrice(convFloat(t[4]));
                g.setSupportedLanguages(separarLista(t[5]));
                g.setMediaScore(convFloat(t[6]));
                g.setAchievements(convInt(t[7]));
                g.setUserScore(convFloat(t[8]));
                g.setPublishers(separarLista(t[9]));
                g.setDevelopers(separarLista(t[10]));
                g.setCategories(separarLista(t[11]));
                g.setGenres(separarLista(t[12]));
                g.setTags(separarLista(t[13]));
                map.put(g.getId(), g);
            }
        } catch (IOException ignore) {}
        return map;
    }

    static String[] quebrarLinhaCSV(String linha) {
        List<String> partes = new ArrayList<>();
        boolean quote = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') quote = !quote;
            else if (c == ',' && !quote) {
                partes.add(cur.toString().trim());
                cur.setLength(0);
            } else cur.append(c);
        }
        partes.add(cur.toString().trim());
        return partes.toArray(new String[0]);
    }

    // converte data
    static String formatarData(String raw) {
        if (raw == null || raw.isEmpty()) return "01/01/0000";
        raw = raw.replace("\"", "").trim();
        Map<String,String> m = new HashMap<>();
        m.put("Jan","01"); m.put("Feb","02"); m.put("Mar","03"); m.put("Apr","04");
        m.put("May","05"); m.put("Jun","06"); m.put("Jul","07"); m.put("Aug","08");
        m.put("Sep","09"); m.put("Oct","10"); m.put("Nov","11"); m.put("Dec","12");
        try {
            String[] p = raw.split(" ");
            String dia = "01", mes = "01", ano = "0000";
            if (p.length == 3) { dia = p[1].replace(",", ""); mes = m.getOrDefault(p[0], "01"); ano = p[2]; }
            else if (p.length == 2) { mes = m.getOrDefault(p[0], "01"); ano = p[1]; }
            else if (p.length == 1) { ano = p[0]; }
            return String.format("%02d/%s/%s", Integer.parseInt(dia), mes, ano);
        } catch (Exception e) { return "01/01/0000"; }
    }

    // tira colchetes e aspas e separa a lista de strings
    static String[] separarLista(String s) {
        if (s == null) return new String[0];
        s = s.replace("[", "").replace("]", "").replace("'", "").replace("\"", "").trim();
        if (s.isEmpty()) return new String[0];
        String[] arr = s.split(",");
        for (int i = 0; i < arr.length; i++) arr[i] = arr[i].trim();
        return arr;
    }

    // Conversão simples pra int e float
    static int convInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
    static float convFloat(String s) {
        try { return Float.parseFloat(s.trim().replace(",", ".")); } catch (Exception e) { return 0f; }
    }
}
