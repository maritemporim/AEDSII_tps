#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <ctype.h>
#include <time.h>

typedef struct {
    int id;
    char* name;
    char* releaseDate;
    int estimatedOwners;
    float price;
    char** supportedLanguages;
    int numSupportedLanguages;
    float mediaScore; 
    int achievements;
    float userScore;
    char** publishers;
    int numPublishers;
    char** developers;
    int numDevelopers;
    char** categories;
    int numCategories;
    char** genres;
    int numGenres;
    char** tags;
    int numTags;
} Game;

int   converteParaInt(const char* s);
float converteParaFloat(const char* s);
char* formatarData(const char* entradaBruta);
char** extrairLista(const char* entradaBruta, int* contador);
char** quebrarCSV(const char* linha, int* contadorCampos);
void  imprimirInfoJogo(const Game* info); 
void  liberarMemoriaJogo(Game* info); 
char* limparEspacos(const char* str);


static long long SEL_comparacoes = 0;
static long long SEL_movimentacoes = 0;

// comparação por Name 
static int cmpName(const Game* a, const Game* b) {
    SEL_comparacoes++;
    if (a->name == NULL && b->name == NULL) return (a->id < b->id) ? -1 : (a->id > b->id);
    if (a->name == NULL) return -1;
    if (b->name == NULL) return 1;
    int c = strcmp(a->name, b->name);
    if (c != 0) return c;
    SEL_comparacoes++;
    if (a->id < b->id) return -1;
    if (a->id > b->id) return 1;
    return 0;
}

// swap de ponteiros
static void swapPtr(Game** x, Game** y) {
    if (x == y) return;
    Game* t = *x; *x = *y; *y = t;
    SEL_movimentacoes += 3;
}

// Selection Sort
static void selectionSort(Game** v, int n) {
    for (int i = 0; i < n - 1; i++) {
        int min = i;
        for (int j = i + 1; j < n; j++) {
            if (cmpName(v[j], v[min]) < 0) min = j;
        }
        if (min != i) swapPtr(&v[i], &v[min]);
    }
}

int converteParaInt(const char* s) {
    if (s == NULL) return 0;
    char* strLimpa = limparEspacos(s);
    int resultado = atoi(strLimpa);
    free(strLimpa);
    return resultado;
}

float converteParaFloat(const char* s) {
    if (s == NULL) return 0.0f;
    char* strLimpa = limparEspacos(s);
    for (int k = 0; strLimpa[k] != '\0'; k++) {
        if (strLimpa[k] == ',') { strLimpa[k] = '.'; break; } // vírgula -> ponto
    }
    float resultado = atof(strLimpa);
    free(strLimpa);
    return resultado;
}

char* limparEspacos(const char* str) {
    if (str == NULL) return strdup("");
    size_t tamanho = strlen(str);
    if (tamanho == 0) return strdup("");
    size_t inicio = 0; while (inicio < tamanho && isspace((unsigned char)str[inicio])) inicio++;
    size_t fim = tamanho; while (fim > inicio && isspace((unsigned char)str[fim - 1])) fim--;
    if (fim > inicio) {
        char* nova_str = (char*)malloc(fim - inicio + 1);
        if (!nova_str) return strdup("");
        strncpy(nova_str, str + inicio, fim - inicio);
        nova_str[fim - inicio] = '\0';
        return nova_str;
    } else return strdup("");
}

// conversão de data
char* formatarData(const char* entradaBruta) {
    if (entradaBruta == NULL || entradaBruta[0] == '\0') return strdup("01/01/0000");
    char temp_raw[256];
    size_t tam_bruto = strlen(entradaBruta);
    if (entradaBruta[0] == '"' && entradaBruta[tam_bruto - 1] == '"') {
        strncpy(temp_raw, entradaBruta + 1, tam_bruto - 2);
        temp_raw[tam_bruto - 2] = '\0';
    } else strcpy(temp_raw, entradaBruta);

    char* strLimpa = limparEspacos(temp_raw);
    if (strLimpa[0] == '\0') { free(strLimpa); return strdup("01/01/0000"); }

    char* mesesAbrev[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    char* mesesNum[]   = {"01","02","03","04","05","06","07","08","09","10","11","12"};

    char diaStr[3] = "01", mesStr[3] = "01", anoStr[5] = "0000";
    char* partesData[3]; int num_elementos = 0;
    char* pedaco = strtok(strLimpa, " ");
    while (pedaco != NULL && num_elementos < 3) { partesData[num_elementos++] = strdup(pedaco); pedaco = strtok(NULL, " "); }

    if (num_elementos == 3) {
        for (int j = 0; j < 12; j++) if (strncmp(partesData[0], mesesAbrev[j], 3) == 0) { strcpy(mesStr, mesesNum[j]); break; }
        char* v = strchr(partesData[1], ','); if (v) *v = '\0';
        if (strlen(partesData[1]) > 0) sprintf(diaStr, "%02d", converteParaInt(partesData[1]));
        if (strlen(partesData[2]) == 4) strcpy(anoStr, partesData[2]);
    } else if (num_elementos == 2) {
        for (int j = 0; j < 12; j++) if (strncmp(partesData[0], mesesAbrev[j], 3) == 0) { strcpy(mesStr, mesesNum[j]); break; }
        if (strlen(partesData[1]) == 4) strcpy(anoStr, partesData[1]);
    } else if (num_elementos == 1) {
        if (strlen(partesData[0]) == 4) strcpy(anoStr, partesData[0]);
    }

    for (int j = 0; j < num_elementos; j++) free(partesData[j]);
    free(strLimpa);

    char* data_final = (char*)malloc(11);
    if (!data_final) return strdup("01/01/0000");
    sprintf(data_final, "%s/%s/%s", diaStr, mesStr, anoStr);
    return data_final;
}

// tira colchetes/aspas da lista textual e separa por vírgula
char** extrairLista(const char* entradaBruta, int* contador) {
    *contador = 0;
    if (entradaBruta == NULL || entradaBruta[0] == '\0') return NULL;
    char temp[4096]; size_t tamanho = strlen(entradaBruta); int j = 0;
    for (size_t k = 0; k < tamanho; k++) {
        if (entradaBruta[k] != '[' && entradaBruta[k] != ']' && entradaBruta[k] != '\'' && entradaBruta[k] != '"')
            temp[j++] = entradaBruta[k];
    }
    temp[j] = '\0';

    char* strLimpa = limparEspacos(temp);
    if (strLimpa[0] == '\0') { free(strLimpa); return NULL; }

    int qtd_elementos = 1;
    for (int k = 0; strLimpa[k] != '\0'; k++) if (strLimpa[k] == ',') qtd_elementos++;

    char** lista = (char**)malloc(qtd_elementos * sizeof(char*));
    if (!lista) { free(strLimpa); return NULL; }

    char* pedaco = strtok(strLimpa, ",");
    while (pedaco != NULL) {
        char* item = limparEspacos(pedaco);
        lista[*contador] = item;
        (*contador)++;
        pedaco = strtok(NULL, ",");
    }
    free(strLimpa);
    return lista;
}

// separa a linha do CSV respeitando campos com vírgula dentro de aspas 
char** quebrarCSV(const char* linha, int* contadorCampos) {
    *contadorCampos = 0;
    int maxElementos = 14;
    char** elementos = (char**)malloc(maxElementos * sizeof(char*));
    if (!elementos) return NULL;

    bool emAspas = false;
    int comecoElemento = 0;
    size_t tamanhoLinha = strlen(linha);

    for (size_t k = 0; k <= tamanhoLinha; k++) {
        char caractere = (k == tamanhoLinha) ? ',' : linha[k];
        if (caractere == '"') emAspas = !emAspas;
        else if (caractere == ',' && !emAspas) {
            size_t tamElemento = k - comecoElemento;
            if (*contadorCampos < maxElementos) {
                char* temp_elemento = (char*)malloc(tamElemento + 1);
                if (!temp_elemento) return NULL;
                strncpy(temp_elemento, linha + comecoElemento, tamElemento);
                temp_elemento[tamElemento] = '\0';
                elementos[*contadorCampos] = temp_elemento;
                (*contadorCampos)++;
            }
            comecoElemento = k + 1;
        }
    }
    return elementos;
}

// imprime
void imprimirInfoJogo(const Game* info) {
    printf("=> %d ## %s ## %s ## %d ## %.2f ## ",
            info->id, info->name, info->releaseDate, info->estimatedOwners, info->price);

    printf("[");
    for (int j = 0; j < info->numSupportedLanguages; j++)
        printf("%s%s", info->supportedLanguages[j], (j == info->numSupportedLanguages - 1) ? "" : ", ");
    printf("] ## ");

    printf("%d ## ", (int)info->mediaScore);
    printf("%.1f ## ", (float)info->achievements);
    printf("%d ## ", (int)info->userScore);

    printf("[");
    for (int j = 0; j < info->numPublishers; j++)
        printf("%s%s", info->publishers[j], (j == info->numPublishers - 1) ? "" : ", ");
    printf("] ## ");

    printf("[");
    for (int j = 0; j < info->numDevelopers; j++)
        printf("%s%s", info->developers[j], (j == info->numDevelopers - 1) ? "" : ", ");
    printf("] ## ");

    printf("[");
    for (int j = 0; j < info->numCategories; j++)
        printf("%s%s", info->categories[j], (j == info->numCategories - 1) ? "" : ", ");
    printf("] ## ");

    printf("[");
    for (int j = 0; j < info->numGenres; j++)
        printf("%s%s", info->genres[j], (j == info->numGenres - 1) ? "" : ", ");
    printf("] ## ");

    printf("[");
    for (int j = 0; j < info->numTags; j++)
        printf("%s%s", info->tags[j], (j == info->numTags - 1) ? "" : ", ");
    printf("] ##\n");
}

// desalocação
void liberarMemoriaJogo(Game* info) {
    if (info->name) free(info->name);
    if (info->releaseDate) free(info->releaseDate);
    int j;
    if (info->supportedLanguages) { for (j = 0; j < info->numSupportedLanguages; j++) free(info->supportedLanguages[j]); free(info->supportedLanguages); }
    if (info->publishers)         { for (j = 0; j < info->numPublishers; j++)        free(info->publishers[j]);        free(info->publishers); }
    if (info->developers)         { for (j = 0; j < info->numDevelopers; j++)        free(info->developers[j]);        free(info->developers); }
    if (info->categories)         { for (j = 0; j < info->numCategories; j++)        free(info->categories[j]);        free(info->categories); }
    if (info->genres)             { for (j = 0; j < info->numGenres; j++)            free(info->genres[j]);            free(info->genres); }
    if (info->tags)               { for (j = 0; j < info->numTags; j++)              free(info->tags[j]);              free(info->tags); }
}

int main() {
    const char* caminhoArquivo = "/tmp/games.csv";

    // carrega todos os jogos do CSV para um vetor
    Game* listaJogos = NULL;
    int totalJogos = 0;
    int tamanhoLista = 100;

    listaJogos = (Game*)malloc(tamanhoLista * sizeof(Game));
    if (listaJogos == NULL) return 1;

    FILE* arq = fopen(caminhoArquivo, "r");
    if (arq == NULL) { free(listaJogos); return 1; }

    char linhaDados[4096];

    // pula o cabeçalho
    if (fgets(linhaDados, sizeof(linhaDados), arq) == NULL) { fclose(arq); free(listaJogos); return 1; }
    
    // le e converte cada linha do CSV 
    while (fgets(linhaDados, sizeof(linhaDados), arq) != NULL) {
        size_t tamLinha = strlen(linhaDados);
        if (tamLinha > 0 && linhaDados[tamLinha - 1] == '\n') linhaDados[tamLinha - 1] = '\0';

        int contCampos;
        char** camposCSV = quebrarCSV(linhaDados, &contCampos);

        if (contCampos >= 14) {
            if (totalJogos >= tamanhoLista) {
                tamanhoLista *= 2;
                Game* temp = (Game*)realloc(listaJogos, tamanhoLista * sizeof(Game));
                if (temp == NULL) {
                    for (int j = 0; j < totalJogos; j++) liberarMemoriaJogo(&listaJogos[j]);
                    free(listaJogos);
                    for (int j = 0; j < contCampos; j++) if (camposCSV[j]) free(camposCSV[j]);
                    free(camposCSV);
                    fclose(arq);
                    return 1;
                }
                listaJogos = temp;
            }

            Game* jogoAtual = &listaJogos[totalJogos];
            jogoAtual->id                = converteParaInt(camposCSV[0]);
            jogoAtual->name              = strdup(camposCSV[1]);
            jogoAtual->releaseDate       = formatarData(camposCSV[2]);
            jogoAtual->estimatedOwners   = converteParaInt(camposCSV[3]);
            jogoAtual->price             = converteParaFloat(camposCSV[4]);
            jogoAtual->supportedLanguages= extrairLista(camposCSV[5], &jogoAtual->numSupportedLanguages);
            jogoAtual->mediaScore        = converteParaFloat(camposCSV[6]);
            jogoAtual->achievements      = converteParaInt(camposCSV[7]);
            jogoAtual->userScore         = converteParaFloat(camposCSV[8]);
            jogoAtual->publishers        = extrairLista(camposCSV[9],  &jogoAtual->numPublishers);
            jogoAtual->developers        = extrairLista(camposCSV[10], &jogoAtual->numDevelopers);
            jogoAtual->categories        = extrairLista(camposCSV[11], &jogoAtual->numCategories);
            jogoAtual->genres            = extrairLista(camposCSV[12], &jogoAtual->numGenres);
            jogoAtual->tags              = extrairLista(camposCSV[13], &jogoAtual->numTags);
            totalJogos++;
        }

        for (int j = 0; j < contCampos; j++) if (camposCSV[j]) free(camposCSV[j]);
        free(camposCSV);
    }
    fclose(arq);

    // subset: vetor de ponteiros para os jogos solicitados via stdin (por ID) 
    Game** subset = NULL;
    int nsubset = 0, cap = 64;
    subset = (Game**)malloc(cap * sizeof(Game*));
    if (!subset) {
        for (int j = 0; j < totalJogos; j++) liberarMemoriaJogo(&listaJogos[j]);
        free(listaJogos);
        return 1;
    }

    char idBusca[1024];
    while (true) {
        if (fgets(idBusca, sizeof(idBusca), stdin) == NULL) break;
        size_t tamBusca = strlen(idBusca);
        if (tamBusca > 0 && idBusca[tamBusca - 1] == '\n') idBusca[tamBusca - 1] = '\0';
        if (strcmp(idBusca, "FIM") == 0) break;

        int id = converteParaInt(idBusca);
        for (int j = 0; j < totalJogos; j++) {
            if (listaJogos[j].id == id) {
                if (nsubset >= cap) {
                    cap *= 2;
                    Game** tmp = (Game**)realloc(subset, cap * sizeof(Game*));
                    if (!tmp) { 
                        free(subset); 
                        for (int k=0;k<totalJogos;k++) liberarMemoriaJogo(&listaJogos[k]); 
                        free(listaJogos); 
                        return 1; 
                    }
                subset = tmp;
                }
                subset[nsubset++] = &listaJogos[j];
                break; // acho
            }
        }
    }

    // ordeno por Name (desempata por id) 
    clock_t ini = clock();
    if (nsubset > 1) selectionSort(subset, nsubset);
    clock_t fim = clock();

    // impressão
    for (int i = 0; i < nsubset; i++) imprimirInfoJogo(subset[i]);

    // log
    double tempo_ms = (double)(fim - ini) / CLOCKS_PER_SEC * 1000.0;
    FILE* log = fopen("885948_selecao.txt", "w");
    if (log) {
        fprintf(log, "885948\t%lld\t%lld\t%.3f\n", SEL_comparacoes, SEL_movimentacoes, tempo_ms);
        fclose(log);
    }

    // limpeza 
    free(subset);
    for (int j = 0; j < totalJogos; j++) liberarMemoriaJogo(&listaJogos[j]);
    free(listaJogos);
    return 0;
}
