import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

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
        return "=> " + id + " ## " + name + " ## " + releaseDate + " ## " + estimatedOwners + " ## " + String.format("%.2f", price) + " ## " +
                Arrays.toString(supportedLanguages).replaceAll("^\\[|\\]$", "") + " ## " +
                ((int)mediaScore) + " ## " + 
                ((float)achievements) + " ## " + ((int)userScore) + " ## " + 
                Arrays.toString(publishers) + " ## " +
                Arrays.toString(developers) + " ## " +
                Arrays.toString(categories) + " ## " +
                Arrays.toString(genres) + " ## " +
                Arrays.toString(tags) + " ##";
    }
}

class ListaGame {
    private Game[] array;
    private int n;

    public ListaGame() { this(1000); }
    public ListaGame(int cap) { array = new Game[cap]; n = 0; }

    public void inserirInicio(Game x) throws Exception {
        if (n >= array.length) throw new Exception("Erro ao inserir");
        for (int i = n; i > 0; i--) array[i] = array[i-1];
        array[0] = x;
        n++;
    }

    public void inserir(Game x, int pos) throws Exception {
        if (n >= array.length) throw new Exception("Erro ao inserir");
        if (pos < 0 || pos > n) throw new Exception("Posição inválida");
        for (int i = n; i > pos; i--) array[i] = array[i-1];
        array[pos] = x;
        n++;
    }

    public void inserirFim(Game x) throws Exception {
        if (n >= array.length) throw new Exception("Erro ao inserir");
        array[n++] = x;
    }

    public Game removerInicio() throws Exception {
        if (n == 0) throw new Exception("Erro ao remover");
        Game resp = array[0];
        for (int i = 0; i < n-1; i++) array[i] = array[i+1];
        array[--n] = null;
        return resp;
    }

    public Game remover(int pos) throws Exception {
        if (n == 0) throw new Exception("Erro ao remover");
        if (pos < 0 || pos >= n) throw new Exception("Posição inválida");
        Game resp = array[pos];
        for (int i = pos; i < n-1; i++) array[i] = array[i+1];
        array[--n] = null;
        return resp;
    }

    public Game removerFim() throws Exception {
        if (n == 0) throw new Exception("Erro ao remover");
        Game resp = array[--n];
        array[n] = null;
        return resp;
    }

    public int tamanho() { return n; }

    public Game get(int i) { return array[i]; }
}

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "/tmp/games.csv";
        Map<Integer, Game> porId = carregarJogos(caminhoArquivo);
        ListaGame lista = new ListaGame(5000);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String s = in.readLine();
                if (s == null) return;
                s = s.trim();
                if (s.equals("FIM")) break;
                int id = converterParaInt(s);
                Game g = porId.get(id);
                if (g != null) lista.inserirFim(g);
            }

            String linhaN = in.readLine();
            int nCmd = converterParaInt(linhaN);

            for (int k = 0; k < nCmd; k++) {
                String cmdLine = in.readLine();
                if (cmdLine == null) break;
                cmdLine = cmdLine.trim();
                String[] t = cmdLine.split("\\s+");
                String op = t[0];

                try {
                    switch (op) {
                        case "II": {
                            int id = converterParaInt(t[1]);
                            Game g = porId.get(id);
                            if (g != null) lista.inserirInicio(g);
                            break;
                        }
                        case "IF": {
                            int id = converterParaInt(t[1]);
                            Game g = porId.get(id);
                            if (g != null) lista.inserirFim(g);
                            break;
                        }
                        case "I*": {
                            int pos = converterParaInt(t[1]);
                            int id = converterParaInt(t[2]);
                            Game g = porId.get(id);
                            if (g != null) lista.inserir(g, pos);
                            break;
                        }
                        case "RI": {
                            Game r = lista.removerInicio();
                            System.out.println("(R) " + r.getName());
                            break;
                        }
                        case "RF": {
                            Game r = lista.removerFim();
                            System.out.println("(R) " + r.getName());
                            break;
                        }
                        case "R*": {
                            int pos = converterParaInt(t[1]);
                            Game r = lista.remover(pos);
                            System.out.println("(R) " + r.getName());
                            break;
                        }
                    }
                } catch (Exception e) {}
            }

            for (int i = 0; i < lista.tamanho(); i++) {
                System.out.println(lista.get(i));
            }

        } catch (Exception erro) {
            erro.printStackTrace();
        }
    }

    private static Map<Integer, Game> carregarJogos(String caminhoArquivo) {
        Map<Integer, Game> porId = new HashMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(caminhoArquivo));
            br.readLine();
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] s = quebrarLinhaCSV(linha);
                if (s.length < 14) continue;
                Game g = new Game();
                g.setId(converterParaInt(s[0]));
                g.setName(s[1]);
                g.setReleaseDate(formatarData(s[2]));
                g.setEstimatedOwners(converterParaInt(s[3]));
                g.setPrice(converterParaFloat(s[4]));
                g.setSupportedLanguages(separarLista(s[5]));
                g.setMediaScore(converterParaFloat(s[6]));
                g.setAchievements(converterParaInt(s[7]));
                g.setUserScore(converterParaFloat(s[8]));
                g.setPublishers(separarLista(s[9]));
                g.setDevelopers(separarLista(s[10]));
                g.setCategories(separarLista(s[11]));
                g.setGenres(separarLista(s[12]));
                g.setTags(separarLista(s[13]));
                porId.put(g.getId(), g);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (br != null) br.close(); } catch (Exception ignore) {}
        }
        return porId;
    }

    public static String[] quebrarLinhaCSV(String linha) {
        List<String> elementos = new ArrayList<>();
        boolean entreAspas = false;
        StringBuilder elementoAtual = new StringBuilder();
        for (int j = 0; j < linha.length(); j++) {
            char c = linha.charAt(j);
            if (c == '"') {
                entreAspas = !entreAspas;
            } else if (c == ',' && !entreAspas) {
                elementos.add(elementoAtual.toString().trim());
                elementoAtual.setLength(0);
            } else {
                elementoAtual.append(c);
            }
        }
        elementos.add(elementoAtual.toString().trim());
        return elementos.toArray(new String[0]);
    }

    public static String formatarData(String dadoBruto) {
        if (dadoBruto == null || dadoBruto.isEmpty()) return "01/01/0000";
        Map<String, String> meses = new HashMap<>();
        meses.put("Jan", "01"); meses.put("Feb", "02"); meses.put("Mar", "03"); meses.put("Apr", "04");
        meses.put("May", "05"); meses.put("Jun", "06"); meses.put("Jul", "07"); meses.put("Aug", "08");
        meses.put("Sep", "09"); meses.put("Oct", "10"); meses.put("Nov", "11"); meses.put("Dec", "12");
        try {
            dadoBruto = dadoBruto.replace("\"", "").trim();
            String[] partes = dadoBruto.split(" ");
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
        } catch (Exception e) {
            return "01/01/0000";
        }
    }

    public static String[] separarLista(String dadoBruto) {
        if (dadoBruto == null || dadoBruto.trim().isEmpty()) return new String[0];
        dadoBruto = dadoBruto.replace("[", "").replace("]", "").replace("'", "").replace("\"", "").trim();
        if (dadoBruto.isEmpty()) return new String[0];
        String[] elementos = dadoBruto.split(",");
        for (int k = 0; k < elementos.length; k++) elementos[k] = elementos[k].trim();
        return elementos;
    }

    public static int converterParaInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    public static float converterParaFloat(String s) {
        try { return Float.parseFloat(s.trim()); } catch (Exception e) { return 0.0f; }
    }
}
