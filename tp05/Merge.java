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

    // saida
    public String toString() {
        return "=> " + id + " ## " + name + " ## " + releaseDate + " ## " + estimatedOwners + " ## " +
                String.format(java.util.Locale.US, "%.2f", price) + " ## " +
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

public class Merge {
    static final String CSV = "/tmp/games.csv"; // caminho do arquivo base
    static final String MATRICULA = "885948"; // usada no arquivo de log
    static long comparacoes = 0;
    static long movimentacoes = 0;

    public static void main(String[] args) {
        // carrega todos os jogos do CSV num mapa (id -> jogo)
        Map<Integer, Game> porId = carregarCSV(CSV);

        List<Game> subset = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String s;
            while ((s = in.readLine()) != null) {
                s = s.trim();
                if (s.equals("FIM")) break;
                if (s.isEmpty()) continue;
                try {
                    int id = Integer.parseInt(s);
                    Game g = porId.get(id);
                    if (g != null) subset.add(g);
                } catch (NumberFormatException ignore) {}
            }
        } catch (IOException ignore) {}

        // lista pra array
        Game[] arr = subset.toArray(new Game[0]);

        long ini = System.nanoTime();
        if (arr.length > 1) mergeSort(arr, 0, arr.length - 1);
        long fim = System.nanoTime();

        // 5 mais caros
        System.out.println("| 5 preços mais caros |");
        for (int i = arr.length - 1; i >= Math.max(0, arr.length - 5); i--) {
            System.out.println(arr[i].toString());
        }

        System.out.println();
        // 5 mais baratos
        System.out.println("| 5 preços mais baratos |");
        for (int i = 0; i < Math.min(5, arr.length); i++) {
            System.out.println(arr[i].toString());
        }

        // cria o log
        double ms = (fim - ini) / 1_000_000.0;
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(MATRICULA + "_mergesort.txt"), StandardCharsets.UTF_8))) {
            pw.println(MATRICULA + "\t" + comparacoes + "\t" + movimentacoes + "\t" + String.format(java.util.Locale.US, "%.3f", ms));
        } catch (IOException ignore) {}
    }

    // implementação padrão do mergesort
    static void mergeSort(Game[] a, int l, int r) {
        if (l >= r) return;
        int m = (l + r) >>> 1;
        mergeSort(a, l, m);
        mergeSort(a, m + 1, r);
        merge(a, l, m, r);
    }

    // junta as duas metades ordenadas
    static void merge(Game[] a, int l, int m, int r) {
        int n1 = m - l + 1, n2 = r - m;
        Game[] L = new Game[n1];
        Game[] R = new Game[n2];
        System.arraycopy(a, l, L, 0, n1);
        System.arraycopy(a, m + 1, R, 0, n2);
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            comparacoes++;
            if (menorOuIgual(L[i], R[j])) {
                a[k++] = L[i++]; movimentacoes++;
            } else {
                a[k++] = R[j++]; movimentacoes++;
            }
        }
        while (i < n1) { a[k++] = L[i++]; movimentacoes++; }
        while (j < n2) { a[k++] = R[j++]; movimentacoes++; }
    }

    // compara dois jogos: primeiro pelo preço, depois pelo id
    static boolean menorOuIgual(Game x, Game y) {
        if (x.getPrice() < y.getPrice()) return true;
        if (x.getPrice() > y.getPrice()) return false;
        comparacoes++; // desempate
        return x.getId() <= y.getId();
    }

    // lê e carrega o CSV pro mapa
    static Map<Integer, Game> carregarCSV(String caminho) {
        Map<Integer, Game> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            br.readLine(); // ignoro o cabeçalho
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

    // separa a linha do CSV respeitando campos com vírgulas dentro de aspas
    static String[] quebrarLinhaCSV(String linha) {
        List<String> elementos = new ArrayList<>();
        boolean entreAspas = false;
        StringBuilder atual = new StringBuilder();
        for (int j = 0; j < linha.length(); j++) {
            char c = linha.charAt(j);
            if (c == '"') entreAspas = !entreAspas;
            else if (c == ',' && !entreAspas) { elementos.add(atual.toString().trim()); atual.setLength(0); }
            else atual.append(c);
        }
        elementos.add(atual.toString().trim());
        return elementos.toArray(new String[0]);
    }

    // data
    static String formatarData(String dadoBruto) {
        if (dadoBruto == null || dadoBruto.isEmpty()) return "01/01/0000";
        Map<String, String> meses = new HashMap<>();
        meses.put("Jan","01"); meses.put("Feb","02"); meses.put("Mar","03"); meses.put("Apr","04");
        meses.put("May","05"); meses.put("Jun","06"); meses.put("Jul","07"); meses.put("Aug","08");
        meses.put("Sep","09"); meses.put("Oct","10"); meses.put("Nov","11"); meses.put("Dec","12");
        try {
            dadoBruto = dadoBruto.replace("\"","").trim();
            String[] p = dadoBruto.split(" ");
            String dia="01", mes="01", ano="0000";
            if (p.length==3){ dia=p[1].replace(",",""); mes=meses.getOrDefault(p[0],"01"); ano=p[2]; }
            else if (p.length==2){ mes=meses.getOrDefault(p[0],"01"); ano=p[1]; }
            else if (p.length==1){ ano=p[0]; }
            return String.format("%02d/%s/%s", Integer.parseInt(dia), mes, ano);
        } catch (Exception e) { return "01/01/0000"; }
    }

    // transforma lista em array limpando colchetes, aspas etc
    static String[] separarLista(String bruto) {
        if (bruto == null || bruto.isBlank()) return new String[0];
        bruto = bruto.replace("[","").replace("]","").replace("'","").replace("\"","").trim();
        if (bruto.isEmpty()) return new String[0];
        String[] v = bruto.split(",");
        for (int i = 0; i < v.length; i++) v[i] = v[i].trim();
        return v;
    }

    // conversões simples
    static int convInt(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
    static float convFloat(String s) {
        try { return Float.parseFloat(s.trim().replace(",", ".")); } catch (Exception e) { return 0f; }
    }
}
