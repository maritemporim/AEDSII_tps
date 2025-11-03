import java.io.*;
import java.util.*;

class Game {
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

    public String toString() {
        return "=> " + id + " ## " + name + " ## " + releaseDate + " ## " + estimatedOwners + " ## " +
               String.format("%.2f", price) + " ## " + Arrays.toString(supportedLanguages).replaceAll("^\\[|\\]$", "") +
               " ## " + (int) mediaScore + " ## " + (float) achievements + " ## " + (int) userScore + " ## " +
               Arrays.toString(publishers) + " ## " + Arrays.toString(developers) + " ## " +
               Arrays.toString(categories) + " ## " + Arrays.toString(genres) + " ## " + Arrays.toString(tags) + " ##";
    }
}

class No {
    Game elemento;
    No prox;
    No(Game elemento) { this.elemento = elemento; this.prox = null; }
}

class FilaGame {
    private No primeiro;
    private No ultimo;
    private int tamanho;

    public FilaGame() {
        primeiro = ultimo = null;
        tamanho = 0;
    }

    public void enfileirar(Game g) {
        No novo = new No(g);
        if (ultimo == null) {
            primeiro = ultimo = novo;
        } else {
            ultimo.prox = novo;
            ultimo = novo;
        }
        tamanho++;
    }

    public Game desenfileirar() throws Exception {
        if (primeiro == null) throw new Exception("Fila vazia");
        Game resp = primeiro.elemento;
        primeiro = primeiro.prox;
        if (primeiro == null) ultimo = null;
        tamanho--;
        return resp;
    }

    public int tamanho() {
        return tamanho;
    }

    public Game get(int i) {
        No tmp = primeiro;
        for (int k = 0; k < i; k++) tmp = tmp.prox;
        return tmp.elemento;
    }
}

public class Fila {
    public static void main(String[] args) {
        String caminho = "/tmp/games.csv";
        Map<Integer, Game> jogos = carregarCSV(caminho);
        FilaGame fila = new FilaGame();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String linha = br.readLine();
                if (linha == null) return;
                linha = linha.trim();
                if (linha.equals("FIM")) break;
                int id = converterInt(linha);
                Game g = jogos.get(id);
                if (g != null) fila.enfileirar(g);
            }

            String nStr = br.readLine();
            int n = converterInt(nStr);

            for (int i = 0; i < n; i++) {
                String comando = br.readLine();
                if (comando == null) break;
                comando = comando.trim();

                if (comando.startsWith("I")) {
                    int id = converterInt(comando.substring(1).trim());
                    Game g = jogos.get(id);
                    if (g != null) fila.enfileirar(g);
                } else if (comando.equals("R")) {
                    try {
                        Game removido = fila.desenfileirar();
                        System.out.println("(R) " + removido.getName());
                    } catch (Exception e) {}
                }
            }

            int idx = 0;
            for (int i = 0; i < fila.tamanho(); i++) {
                System.out.print("[" + idx + "] ");
                System.out.println(fila.get(i));
                idx++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, Game> carregarCSV(String caminho) {
        Map<Integer, Game> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] s = quebrarCSV(linha);
                if (s.length < 14) continue;
                Game g = new Game();
                g.setId(converterInt(s[0]));
                g.setName(s[1]);
                g.setReleaseDate(formatarData(s[2]));
                g.setEstimatedOwners(converterInt(s[3]));
                g.setPrice(converterFloat(s[4]));
                g.setSupportedLanguages(separarLista(s[5]));
                g.setMediaScore(converterFloat(s[6]));
                g.setAchievements(converterInt(s[7]));
                g.setUserScore(converterFloat(s[8]));
                g.setPublishers(separarLista(s[9]));
                g.setDevelopers(separarLista(s[10]));
                g.setCategories(separarLista(s[11]));
                g.setGenres(separarLista(s[12]));
                g.setTags(separarLista(s[13]));
                map.put(g.getId(), g);
            }
        } catch (Exception e) {}
        return map;
    }

    public static String[] quebrarCSV(String linha) {
        List<String> campos = new ArrayList<>();
        boolean entreAspas = false;
        StringBuilder atual = new StringBuilder();
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') entreAspas = !entreAspas;
            else if (c == ',' && !entreAspas) {
                campos.add(atual.toString().trim());
                atual.setLength(0);
            } else atual.append(c);
        }
        campos.add(atual.toString().trim());
        return campos.toArray(new String[0]);
    }

    public static String formatarData(String s) {
        if (s == null || s.isEmpty()) return "01/01/0000";
        Map<String, String> meses = new HashMap<>();
        meses.put("Jan", "01"); meses.put("Feb", "02"); meses.put("Mar", "03"); meses.put("Apr", "04");
        meses.put("May", "05"); meses.put("Jun", "06"); meses.put("Jul", "07"); meses.put("Aug", "08");
        meses.put("Sep", "09"); meses.put("Oct", "10"); meses.put("Nov", "11"); meses.put("Dec", "12");
        try {
            s = s.replace("\"", "").trim();
            String[] partes = s.split(" ");
            String dia = "01", mes = "01", ano = "0000";
            if (partes.length == 3) {
                dia = partes[1].replace(",", "");
                mes = meses.getOrDefault(partes[0], "01");
                ano = partes[2];
            } else if (partes.length == 2) {
                mes = meses.getOrDefault(partes[0], "01");
                ano = partes[1];
            } else if (partes.length == 1) {
                ano = partes[0];
            }
            return String.format("%02d/%s/%s", Integer.parseInt(dia), mes, ano);
        } catch (Exception e) { return "01/01/0000"; }
    }

    public static String[] separarLista(String s) {
        if (s == null || s.isEmpty()) return new String[0];
        s = s.replace("[", "").replace("]", "").replace("\"", "").replace("'", "");
        String[] partes = s.split(",");
        for (int i = 0; i < partes.length; i++) partes[i] = partes[i].trim();
        return partes;
    }

    public static int converterInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    public static float converterFloat(String s) {
        try { return Float.parseFloat(s.trim()); } catch (Exception e) { return 0f; }
    }
}
