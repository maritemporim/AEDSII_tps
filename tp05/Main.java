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

public class Main {
    public static void main(String[] args) {
        String caminhoArquivo = "/tmp/games.csv";
        List<Game> listaDeJogos = new ArrayList<>();
        BufferedReader leitorDeArquivo = null;

        try {
            leitorDeArquivo = new BufferedReader(new FileReader(caminhoArquivo));

            leitorDeArquivo.readLine(); 

            String linhaDoCSV;
            while ((linhaDoCSV = leitorDeArquivo.readLine()) != null) {
                String[] dadosSeparados = quebrarLinhaCSV(linhaDoCSV);
                if (dadosSeparados.length < 14) continue; 

                Game novoJogo = new Game();
                
                novoJogo.setId(converterParaInt(dadosSeparados[0]));
                novoJogo.setName(dadosSeparados[1]);
                novoJogo.setReleaseDate(formatarData(dadosSeparados[2]));
                novoJogo.setEstimatedOwners(converterParaInt(dadosSeparados[3]));
                novoJogo.setPrice(converterParaFloat(dadosSeparados[4]));
                novoJogo.setSupportedLanguages(separarLista(dadosSeparados[5]));
                novoJogo.setMediaScore(converterParaFloat(dadosSeparados[6])); 
                novoJogo.setAchievements(converterParaInt(dadosSeparados[7])); 
                novoJogo.setUserScore(converterParaFloat(dadosSeparados[8]));
                novoJogo.setPublishers(separarLista(dadosSeparados[9]));
                novoJogo.setDevelopers(separarLista(dadosSeparados[10]));
                novoJogo.setCategories(separarLista(dadosSeparados[11]));
                novoJogo.setGenres(separarLista(dadosSeparados[12]));
                novoJogo.setTags(separarLista(dadosSeparados[13]));

                listaDeJogos.add(novoJogo);
            }
        } catch (Exception erro) {
            erro.printStackTrace();
        } finally {
            try { if (leitorDeArquivo != null) leitorDeArquivo.close(); } catch (Exception erro) {}
        }

        try (BufferedReader entradaUsuario = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                String idRecebido = entradaUsuario.readLine();
                if (idRecebido.equals("FIM")) break;

                int idBusca = converterParaInt(idRecebido);
                boolean achou = false;

                for (Game item : listaDeJogos) {
                    if (item.getId() == idBusca) {
                        System.out.println(item);
                        achou = true;
                        break;
                    }
                }

                if (!achou) {
                    System.out.println("Jogo com ID " + idBusca + " não encontrado.");
                }
            }
        } catch (Exception erro) {
            erro.printStackTrace();
        }
    }

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
            dadoBruto = dadoBruto.replace("\"", "").trim();
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
}


public class ArvoreBinaria {
	private No raiz; 
	public ArvoreBinaria() {
		raiz = null;
	}

	public boolean pesquisar(int x) {
		return pesquisar(x, raiz);
	}


	private boolean pesquisar(int x, No i) {
      boolean resp;
		if (i == null) {
         resp = false;

      } else if (x == i.elemento) {
         resp = true;

      } else if (x < i.elemento) {
         resp = pesquisar(x, i.esq);

      } else {
         resp = pesquisar(x, i.dir);
      }
      return resp;
    }
}
