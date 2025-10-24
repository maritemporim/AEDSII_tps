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

int converteParaInt(const char* s);
float converteParaFloat(const char* s);
char* formatarData(const char* entradaBruta);
char** extrairLista(const char* entradaBruta, int* contador);
char** quebrarCSV(const char* linha, int* contadorCampos);
void imprimirInfoJogo(const Game* info); 
void liberarMemoriaJogo(Game* info); 
char* limparEspacos(const char* str);

static long long QS_comparacoes = 0;
static long long QS_movimentacoes = 0;

static int dataParaChave(const char* ddmmyyyy) {
    if (!ddmmyyyy || strlen(ddmmyyyy) != 10) return 0;
    int d = (ddmmyyyy[0]-'0')*10 + (ddmmyyyy[1]-'0');
    int m = (ddmmyyyy[3]-'0')*10 + (ddmmyyyy[4]-'0');
    int y = (ddmmyyyy[6]-'0')*1000 + (ddmmyyyy[7]-'0')*100 + (ddmmyyyy[8]-'0')*10 + (ddmmyyyy[9]-'0');
    return y*10000 + m*100 + d;
}

static int cmpGamePtr(Game* const* a, Game* const* b) {
    QS_comparacoes++;
    int ka = dataParaChave((*a)->releaseDate);
    int kb = dataParaChave((*b)->releaseDate);
    if (ka < kb) return -1;
    if (ka > kb) return 1;
    QS_comparacoes++;
    if ((*a)->id < (*b)->id) return -1;
    if ((*a)->id > (*b)->id) return 1;
    return 0;
}

static void swapPtr(Game** a, Game** b) {
    if (a == b) return;
    Game* t = *a; *a = *b; *b = t;
    QS_movimentacoes += 3;
}

static int partitionPtr(Game** v, int lo, int hi) {
    Game** pivot = &v[hi];
    int i = lo;
    for (int j = lo; j < hi; j++) {
        if (cmpGamePtr(&v[j], pivot) <= 0) {
            swapPtr(&v[i], &v[j]);
            i++;
        }
    }
    swapPtr(&v[i], &v[hi]);
    return i;
}

static void quicksortPtr(Game** v, int lo, int hi) {
    if (lo < hi) {
        int p = partitionPtr(v, lo, hi);
        quicksortPtr(v, lo, p - 1);
        quicksortPtr(v, p + 1, hi);
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
    for (int k = 0; strLimpa[k] != '\0'; k++) if (strLimpa[k] == ',') { strLimpa[k] = '.'; break; }
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
        strncpy(nova_str, str + inicio, fim - inicio);
        nova_str[fim - inicio] = '\0';
        return nova_str;
    }
    return strdup("");
}

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
    sprintf(data_final, "%s/%s/%s", diaStr, mesStr, anoStr);
    return data_final;
}

char** extrairLista(const char* entradaBruta, int* contador) {
    *contador = 0;
    if (entradaBruta == NULL || entradaBruta[0] == '\0') return NULL;
    char temp[4096]; size_t tamanho = strlen(entradaBruta); int j = 0;
    for (size_t k = 0; k < tamanho; k++)
        if (entradaBruta[k] != '[' && entradaBruta[k] != ']' && entradaBruta[k] != '\'' && entradaBruta[k] != '"')
            temp[j++] = entradaBruta[k];
    temp[j] = '\0';

    char* strLimpa = limparEspacos(temp);
    if (strLimpa[0] == '\0') { free(strLimpa); return NULL; }

    int qtd_elementos = 1;
    for (int k = 0; strLimpa[k] != '\0'; k++) if (strLimpa[k] == ',') qtd_elementos++;

    char** lista = (char**)malloc(qtd_elementos * sizeof(char*));
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

void liberarMemoriaJogo(Game* info) {
    if (info->name) free(info->name);
    if (info->releaseDate) free(info->releaseDate);
    for (int j = 0; j < info->numSupportedLanguages; j++) free(info->supportedLanguages[j]);
    free(info->supportedLanguages);
    for (int j = 0; j < info->numPublishers; j++) free(info->publishers[j]);
    free(info->publishers);
    for (int j = 0; j < info->numDevelopers; j++) free(info->developers[j]);
    free(info->developers);
    for (int j = 0; j < info->numCategories; j++) free(info->categories[j]);
    free(info->categories);
    for (int j = 0; j < info->numGenres; j++) free(info->genres[j]);
    free(info->genres);
    for (int j = 0; j < info->numTags; j++) free(info->tags[j]);
    free(info->tags);
}

int main() {
    const char* caminhoArquivo = "/tmp/games.csv";
    Game* listaJogos = NULL;
    int totalJogos = 0, tamanhoLista = 100;

    listaJogos = (Game*)malloc(tamanhoLista * sizeof(Game));
    if (!listaJogos) return 1;

    FILE* arq = fopen(caminhoArquivo, "r");
    if (!arq) { free(listaJogos); return 1; }

    char linhaDados[4096];
    if (!fgets(linhaDados, sizeof(linhaDados), arq)) { fclose(arq); free(listaJogos); return 1; }

    while (fgets(linhaDados, sizeof(linhaDados), arq)) {
        size_t tamLinha = strlen(linhaDados);
        if (tamLinha > 0 && linhaDados[tamLinha - 1] == '\n') linhaDados[tamLinha - 1] = '\0';

        int contCampos; char** camposCSV = quebrarCSV(linhaDados, &contCampos);
        if (contCampos >= 14) {
            if (totalJogos >= tamanhoLista) {
                tamanhoLista *= 2;
                Game* temp = (Game*)realloc(listaJogos, tamanhoLista * sizeof(Game));
                if (!temp) {
                    for (int j = 0; j < totalJogos; j++) liberarMemoriaJogo(&listaJogos[j]);
                    free(listaJogos);
                    for (int j = 0; j < contCampos; j++) if (camposCSV[j]) free(camposCSV[j]);
                    free(camposCSV);
                    fclose(arq);
                    return 1;
                }
                listaJogos = temp;
            }

            Game* g = &listaJogos[totalJogos];
            g->id = converteParaInt(camposCSV[0]);
            g->name = strdup(camposCSV[1]); 
            g->releaseDate = formatarData(camposCSV[2]);
            g->estimatedOwners = converteParaInt(camposCSV[3]);
            g->price = converteParaFloat(camposCSV[4]);
            g->supportedLanguages = extrairLista(camposCSV[5], &g->numSupportedLanguages);
            g->mediaScore = converteParaFloat(camposCSV[6]); 
            g->achievements = converteParaInt(camposCSV[7]); 
            g->userScore = converteParaFloat(camposCSV[8]); 
            g->publishers = extrairLista(camposCSV[9], &g->numPublishers);
            g->developers = extrairLista(camposCSV[10], &g->numDevelopers);
            g->categories = extrairLista(camposCSV[11], &g->numCategories);
            g->genres = extrairLista(camposCSV[12], &g->numGenres);
            g->tags = extrairLista(camposCSV[13], &g->numTags);
            totalJogos++;
        }

        for (int j = 0; j < contCampos; j++) if (camposCSV[j]) free(camposCSV[j]);
        free(camposCSV);
    }
    fclose(arq);

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
        if (!fgets(idBusca, sizeof(idBusca), stdin)) break;
        size_t tamBusca = strlen(idBusca);
        if (tamBusca > 0 && idBusca[tamBusca - 1] == '\n') idBusca[tamBusca - 1] = '\0';
        if (strcmp(idBusca, "FIM") == 0) break;
        int id = converteParaInt(idBusca);
        for (int j = 0; j < totalJogos; j++) {
            if (listaJogos[j].id == id) {
                if (nsubset >= cap) {
                    cap *= 2;
                    Game** tmp = (Game**)realloc(subset, cap * sizeof(Game*));
                    if (!tmp) { free(subset); for (int k=0;k<totalJogos;k++) liberarMemoriaJogo(&listaJogos[k]); free(listaJogos); return 1; }
                    subset = tmp;
                }
                subset[nsubset++] = &listaJogos[j];
                break;
            }
        }
    }

    clock_t ini = clock();
    if (nsubset > 1) quicksortPtr(subset, 0, nsubset - 1);
    clock_t fim = clock();

    for (int i = 0; i < nsubset; i++) imprimirInfoJogo(subset[i]);

    double tempo_ms = (double)(fim - ini) / CLOCKS_PER_SEC * 1000.0;
    FILE* log = fopen("885948_quicksort.txt", "w");
    if (log) {
        fprintf(log, "885948\t%lld\t%lld\t%.3f\n", QS_comparacoes, QS_movimentacoes, tempo_ms);
        fclose(log);
    }

    free(subset);
    for (int j = 0; j < totalJogos; j++) liberarMemoriaJogo(&listaJogos[j]);
    free(listaJogos);
    return 0;
}
