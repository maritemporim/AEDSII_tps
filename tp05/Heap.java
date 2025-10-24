import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

class Game {

    // campos do jogo 
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

    // formato de saída 
    public String toString() {
        return "=> " + id + " ## " + name + " ## " + releaseDate + " ## " + estimatedOwners + " ## " +
               String.format(java.util.Locale.US, "%.2f", price) + " ## " +
               Arrays.toString(supportedLanguages) + " ## " +
               ((int)mediaScore) + " ## " +
               ((float)achievements) + " ## " + ((int)userScore) + " ## " +
               Arrays.toString(publishers) + " ## " +
               Arrays.toString(developers) + " ## " +
               Arrays.toString(categories) + " ## " +
               Arrays.toString(genres) + " ## " +
               Arrays.toString(tags) + " ##";
    }
}

public class Heap {
    // caminho do CSV e matricula 
    static final String CAMINHO_CSV = "/tmp/games.csv";
    static final String MATRICULA = "885948";

    // contadores pro log
    static long comparacoes = 0;
    static long movimentacoes = 0;

    public static void main(String[] args) {
        // carrega o CSV em um mapa por id
        Map<Integer, Game> porId = carregarCSV(CAMINHO_CSV);

        // le da entrada padrão os IDs até o final e guardo os jogos selecionados
        List<Game> selecionados = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String s;
            while ((s = in.readLine()) != null) {
                s = s.trim();
                if (s.equals("FIM")) break;
                if (s.isEmpty()) continue;
                Integer id = tryParseInt(s);
                if (id != null) {
                    Game g = porId.get(id);
                    if (g != null) selecionados.add(g); // só adiciona se existir no csv
                }
            }
        } catch (IOException e) { //não tratamneto de erro
        }

        // ordenação com o heapsort
        long ini = System.nanoTime();
        heapSort(selecionados);
        long fim = System.nanoTime();

        // impreção
        for (Game g : selecionados) System.out.println(g.toString());

        // log 
        double ms = (fim - ini) / 1_000_000.0;
        escreverLog(MATRICULA + "_heapsort.txt",
                MATRICULA + "\t" + comparacoes + "\t" + movimentacoes + "\t" + String.format(Locale.US, "%.3f", ms));
    }

    // devolve um mapa id -> Game
    static Map<Integer, Game> carregarCSV(String caminho) {
        Map<Integer, Game> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho, StandardCharsets.UTF_8))) {
            br.readLine(); // pula o cabeçalho
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] t = quebrarLinhaCSV(linha); // separa respeitando aspas
                if (t.length < 14) continue; 

                Game g = new Game();

                // preenche os campos do objeto Game
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
        } catch (IOException e) {
            // se não achar /tmp/games.csv o mapa volta vazio
        }
        return map;
    }

    // tratamento 
    static String[] quebrarLinhaCSV(String linha) {
        List<String> partes = new ArrayList<>();
        boolean quote = false;
        StringBuilder cur = new StringBuilder();
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == '"') quote = !quote;          
            else if (c == ',' && !quote) {    
                partes.add(cur.toString());
                cur.setLength(0);
            } else cur.append(c);
        }
        partes.add(cur.toString());
        for (int i = 0; i < partes.size(); i++) partes.set(i, partes.get(i).trim());
        return partes.toArray(new String[0]);
    }

    // conversão de datas
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
            if (p.length == 3) {
                dia = p[1].replace(",", "");
                mes = m.getOrDefault(p[0], "01");
                ano = p[2];
            } else if (p.length == 2) {
                mes = m.getOrDefault(p[0], "01");
                ano = p[1];
            } else if (p.length == 1) ano = p[0];
            return String.format("%02d/%s/%s", Integer.parseInt(dia), mes, ano);
        } catch (Exception e) {
            return "01/01/0000"; 
        }
    }

    // tratamento - tira colchetes/aspas e separa por virgula
    static String[] separarLista(String s) {
        if (s == null) return new String[0];
        s = s.replace("[", "").replace("]", "").replace("'", "").replace("\"", "").trim();
        if (s.isEmpty()) return new String[0];
        String[] arr = s.split(",");
        for (int i = 0; i < arr.length; i++) arr[i] = arr[i].trim();
        return arr;
    }

    // parsing numerico
    static Integer tryParseInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return null; }
    }
    static int convInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
    static float convFloat(String s) {
        try { return Float.parseFloat(s.trim()); } catch (Exception e) { return 0.0f; }
    }

    // heapsort
    static void heapSort(List<Game> a) {
        int n = a.size();
        // Faço heapify bottom-up
        for (int i = n/2 - 1; i >= 0; i--) heapify(a, n, i);
        // troca o topo com o fim e reduz o heap
        for (int fim = n - 1; fim > 0; fim--) {
            swap(a, 0, fim);
            heapify(a, fim, 0);
        }
    }

    static void heapify(List<Game> a, int heapSize, int i) {
        while (true) {
            int maior = i;
            int esq = 2*i + 1;
            int dir = 2*i + 2;

            // escolho o maior entre pai/esq/dir usando minha comparação
            if (esq < heapSize && greater(a.get(esq), a.get(maior))) maior = esq;
            if (dir < heapSize && greater(a.get(dir), a.get(maior))) maior = dir;

            if (maior != i) {
                swap(a, i, maior); // quando troco, conto 3 movimentações
                i = maior; // continuo descendo
            } else break;
        }
    }

    static boolean greater(Game a, Game b) {
        comparacoes++;
        if (a.getEstimatedOwners() != b.getEstimatedOwners())
            return a.getEstimatedOwners() > b.getEstimatedOwners();
        comparacoes++;
        return a.getId() > b.getId();
    }

    // troca básica e contabiliza movimentações (3 por swap)
    static void swap(List<Game> a, int i, int j) {
        if (i == j) return;
        Game tmp = a.get(i);
        a.set(i, a.get(j));
        a.set(j, tmp);
        movimentacoes += 3;
    }

    // grava o arquivo de log na pasta atual
    static void escreverLog(String nomeArquivo, String linha) {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(nomeArquivo), StandardCharsets.UTF_8))) {
            pw.println(linha);
        } catch (IOException e) {
            // se não conseguir escrever o log, só segue
        }
    }
}
