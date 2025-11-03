#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <ctype.h>

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

int converteParaInt(const char* s) {
    if (s == NULL) return 0;
    while (isspace((unsigned char)*s)) s++;
    int sinal = 1;
    if (*s=='+' || *s=='-'){ if(*s=='-') sinal=-1; s++; }
    int v=0;
    while (isdigit((unsigned char)*s)) { v = v*10 + (*s-'0'); s++; }
    return sinal*v;
}

float converteParaFloat(const char* s) {
    if (s == NULL) return 0.0f;
    char buf[128]; int j=0;
    while (*s && j<120) { if (*s==',') buf[j++]='.'; else if(!isspace((unsigned char)*s)) buf[j++]=*s; s++; }
    buf[j]='\0';
    return (float)atof(buf);
}

static char* strtrimdup(const char* str) {
    if (!str) return strdup("");
    const char *p=str, *q=str+strlen(str);
    while (p<q && isspace((unsigned char)*p)) p++;
    while (q>p && isspace((unsigned char)q[-1])) q--;
    size_t n = (size_t)(q-p);
    char* r = (char*)malloc(n+1);
    if (!r) return strdup("");
    memcpy(r,p,n); r[n]='\0'; return r;
}

char* formatarData(const char* in) {
    if (!in || !*in) return strdup("01/01/0000");
    size_t L = strlen(in);
    char tmp[256];
    if (in[0]=='"' && in[L-1]=='"' && L>=2) { memcpy(tmp, in+1, L-2); tmp[L-2]='\0'; }
    else { strncpy(tmp, in, sizeof(tmp)-1); tmp[sizeof(tmp)-1]='\0'; }
    char* clean = strtrimdup(tmp);
    if (*clean=='\0'){ free(clean); return strdup("01/01/0000"); }
    const char* abv[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    const char* mesnum[]={"01","02","03","04","05","06","07","08","09","10","11","12"};
    char dia[3]="01", mes[3]="01", ano[5]="0000";
    char *tok, *ctx=NULL;
    tok = strtok_r(clean," ",&ctx);
    char* p1 = tok ? strdup(tok):strdup("");
    tok = strtok_r(NULL," ",&ctx);
    char* p2 = tok ? strdup(tok):strdup("");
    tok = strtok_r(NULL," ",&ctx);
    char* p3 = tok ? strdup(tok):strdup("");

    if (*p1 && *p2 && *p3) {
        for (int j=0;j<12;j++) if (strncmp(p1,abv[j],3)==0){ strcpy(mes,mesnum[j]); break; }
        char *v=strchr(p2,','); if (v) *v='\0';
        if (*p2) snprintf(dia, sizeof(dia), "%02d", converteParaInt(p2));
        if (strlen(p3)==4) strcpy(ano,p3);
    } else if (*p1 && *p2) {
        for (int j=0;j<12;j++) if (strncmp(p1,abv[j],3)==0){ strcpy(mes,mesnum[j]); break; }
        if (strlen(p2)==4) strcpy(ano,p2);
    } else if (*p1) {
        if (strlen(p1)==4) strcpy(ano,p1);
    }

    free(p1); free(p2); free(p3); free(clean);
    char* out = (char*)malloc(11); if (!out) return strdup("01/01/0000");
    snprintf(out,11,"%s/%s/%s",dia,mes,ano);
    return out;
}

char** extrairLista(const char* entradaBruta, int* contador) {
    *contador = 0;
    if (!entradaBruta || !*entradaBruta) return NULL;
    char temp[4096]; int j=0;
    for (size_t k=0;k<strlen(entradaBruta) && j<4095;k++){
        char c=entradaBruta[k];
        if (c!='[' && c!=']' && c!='\'' && c!='"') temp[j++]=c;
    }
    temp[j]='\0';
    char* clean = strtrimdup(temp);
    if (*clean=='\0'){ free(clean); return NULL; }
    int qtd=1; for (int k=0; clean[k]; k++) if (clean[k]==',') qtd++;
    char** lista = (char**)malloc(qtd*sizeof(char*));
    if (!lista){ free(clean); return NULL; }
    char *tok, *ctx=NULL; tok = strtok_r(clean,",",&ctx);
    while (tok){ lista[*contador]=strtrimdup(tok); (*contador)++; tok=strtok_r(NULL,",",&ctx); }
    free(clean); return lista;
}

char** quebrarCSV(const char* linha, int* contadorCampos) {
    *contadorCampos = 0;
    int max = 14;
    char** out = (char**)malloc(max*sizeof(char*));
    if (!out) return NULL;
    bool asp = false;
    int ini = 0;
    size_t n = strlen(linha);
    for (size_t k=0; k<=n; k++){
        char ch = (k==n) ? ',' : linha[k];
        if (ch=='"') asp = !asp;
        else if (ch==',' && !asp) {
            size_t tam = k - ini;
            char* tmp = (char*)malloc(tam+1);
            if (!tmp) { for(int i=0;i<*contadorCampos;i++) free(out[i]); free(out); return NULL; }
            strncpy(tmp, linha+ini, tam); tmp[tam]='\0';
            if (*contadorCampos==1 || *contadorCampos==9) {
                size_t L=strlen(tmp);
                if (L>=2 && tmp[0]=='"' && tmp[L-1]=='"') { tmp[L-1]='\0'; char* z=strdup(tmp+1); free(tmp); tmp=z; }
            }
            out[*contadorCampos]=tmp;
            (*contadorCampos)++;
            ini = (int)k+1;
        }
    }
    return out;
}

void imprimirInfoJogo(const Game* info) {
    printf("=> %d ## %s ## %s ## %d ## %.2f ## [", 
        info->id, info->name, info->releaseDate, info->estimatedOwners, info->price);
    for (int j=0;j<info->numSupportedLanguages;j++) {
        printf("%s%s", info->supportedLanguages[j], (j==info->numSupportedLanguages-1)?"":", ");
    }
    printf("] ## %d ## %.1f ## %d ## [", (int)info->mediaScore, (float)info->achievements, (int)info->userScore);
    for (int j=0;j<info->numPublishers;j++) {
        printf("%s%s", info->publishers[j], (j==info->numPublishers-1)?"":", ");
    }
    printf("] ## [");
    for (int j=0;j<info->numDevelopers;j++) {
        printf("%s%s", info->developers[j], (j==info->numDevelopers-1)?"":", ");
    }
    printf("] ## [");
    for (int j=0;j<info->numCategories;j++) {
        printf("%s%s", info->categories[j], (j==info->numCategories-1)?"":", ");
    }
    printf("] ## [");
    for (int j=0;j<info->numGenres;j++) {
        printf("%s%s", info->genres[j], (j==info->numGenres-1)?"":", ");
    }
    printf("] ## [");
    for (int j=0;j<info->numTags;j++) {
        printf("%s%s", info->tags[j], (j==info->numTags-1)?"":", ");
    }
    printf("] ##\n");
}

void liberarMemoriaJogo(Game* info) {
    if (!info) return;
    free(info->name);
    free(info->releaseDate);
    for (int j=0;j<info->numSupportedLanguages;j++) free(info->supportedLanguages[j]);
    free(info->supportedLanguages);
    for (int j=0;j<info->numPublishers;j++) free(info->publishers[j]);
    free(info->publishers);
    for (int j=0;j<info->numDevelopers;j++) free(info->developers[j]);
    free(info->developers);
    for (int j=0;j<info->numCategories;j++) free(info->categories[j]);
    free(info->categories);
    for (int j=0;j<info->numGenres;j++) free(info->genres[j]);
    free(info->genres);
    for (int j=0;j<info->numTags;j++) free(info->tags[j]);
    free(info->tags);
}

typedef struct No {
    Game* g;
    struct No* prox;
} No;

typedef struct {
    No* primeiro;
    No* ultimo;
    int n;
} ListaGame;

void listaInit(ListaGame* L){ L->primeiro=NULL; L->ultimo=NULL; L->n=0; }

void inserirInicio(ListaGame* L, Game* x){
    No* no = (No*)malloc(sizeof(No));
    no->g = x; no->prox = L->primeiro;
    L->primeiro = no;
    if (L->n==0) L->ultimo = no;
    L->n++;
}

void inserirFim(ListaGame* L, Game* x){
    No* no = (No*)malloc(sizeof(No));
    no->g = x; no->prox = NULL;
    if (L->n==0){ L->primeiro = L->ultimo = no; }
    else { L->ultimo->prox = no; L->ultimo = no; }
    L->n++;
}

void inserirPos(ListaGame* L, Game* x, int pos){
    if (pos<0 || pos> L->n-1) return; 
    if (pos==0){ inserirInicio(L,x); return; }
    No* ant = L->primeiro;
    for (int i=0;i<pos-1;i++) ant = ant->prox;
    No* no = (No*)malloc(sizeof(No));
    no->g = x; no->prox = ant->prox;
    ant->prox = no;
    if (no->prox==NULL) L->ultimo = no;
    L->n++;
}

Game* removerInicio(ListaGame* L){
    if (L->n==0) return NULL;
    No* p = L->primeiro;
    Game* r = p->g;
    L->primeiro = p->prox;
    if (L->primeiro==NULL) L->ultimo=NULL;
    free(p);
    L->n--;
    return r;
}

Game* removerFim(ListaGame* L){
    if (L->n==0) return NULL;
    if (L->n==1) return removerInicio(L);
    No* ant = L->primeiro;
    while (ant->prox != L->ultimo) ant = ant->prox;
    Game* r = L->ultimo->g;
    free(L->ultimo);
    ant->prox = NULL;
    L->ultimo = ant;
    L->n--;
    return r;
}

Game* removerPos(ListaGame* L, int pos){
    if (L->n==0 || pos<0 || pos>=L->n) return NULL;
    if (pos==0) return removerInicio(L);
    if (pos==L->n-1) return removerFim(L);
    No* ant = L->primeiro;
    for (int i=0;i<pos-1;i++) ant = ant->prox;
    No* alvo = ant->prox;
    Game* r = alvo->g;
    ant->prox = alvo->prox;
    free(alvo);
    L->n--;
    return r;
}

Game* carregarCSV(const char* caminho, int* total) {
    FILE* f = fopen(caminho,"r");
    if (!f) return NULL;
    int cap=1024; *total=0;
    Game* arr = (Game*)malloc(cap*sizeof(Game));
    if (!arr){ fclose(f); return NULL; }

    char linha[8192];
    if (!fgets(linha,sizeof(linha),f)) { fclose(f); free(arr); return NULL; }

    while (fgets(linha,sizeof(linha),f)) {
        size_t L=strlen(linha); if (L && linha[L-1]=='\n') linha[L-1]='\0';
        int campos=0; char** s = quebrarCSV(linha,&campos);
        if (!s) continue;
        if (campos>=14) {
            if (*total>=cap){ cap*=2; Game* tmp=(Game*)realloc(arr,cap*sizeof(Game)); if(!tmp){ for(int i=0;i<campos;i++) free(s[i]); free(s); break;} arr=tmp; }
            Game* g = &arr[*total];
            g->id = converteParaInt(s[0]);
            g->name = strtrimdup(s[1]);
            g->releaseDate = formatarData(s[2]);
            g->estimatedOwners = converteParaInt(s[3]);
            g->price = converteParaFloat(s[4]);
            g->supportedLanguages = extrairLista(s[5], &g->numSupportedLanguages);
            g->mediaScore = converteParaFloat(s[6]);
            g->achievements = converteParaInt(s[7]);
            g->userScore = converteParaFloat(s[8]);
            g->publishers = extrairLista(s[9], &g->numPublishers);
            g->developers = extrairLista(s[10], &g->numDevelopers);
            g->categories = extrairLista(s[11], &g->numCategories);
            g->genres = extrairLista(s[12], &g->numGenres);
            g->tags = extrairLista(s[13], &g->numTags);
            (*total)++;
        }
        for (int i=0;i<campos;i++) free(s[i]);
        free(s);
    }
    fclose(f);
    return arr;
}

Game* buscarPorId(Game* arr, int total, int id){
    for (int i=0;i<total;i++) if (arr[i].id == id) return &arr[i];
    return NULL;
}

int main() {
    const char* caminhoArquivo = "/tmp/games.csv";
    int total=0;
    Game* base = carregarCSV(caminhoArquivo, &total);
    if (!base) return 1;

    ListaGame lista; listaInit(&lista);

    char buf[256];
    while (fgets(buf,sizeof(buf),stdin)) {
        size_t L=strlen(buf); if (L && buf[L-1]=='\n') buf[L-1]='\0';
        if (strcmp(buf,"FIM")==0) break;
        int id = converteParaInt(buf);
        Game* g = buscarPorId(base,total,id);
        if (g) inserirFim(&lista, g);
    }

    if (!fgets(buf,sizeof(buf),stdin)) { 
        for (int i=0;i<total;i++) liberarMemoriaJogo(&base[i]); 
        free(base); 
        return 0; 
    }
    int nCmd = converteParaInt(buf);

    for (int k=0;k<nCmd;k++){
        if (!fgets(buf,sizeof(buf),stdin)) break;
        size_t L=strlen(buf); if (L && buf[L-1]=='\n') buf[L-1]='\0';

        if (strncmp(buf,"II",2)==0){
            int id; sscanf(buf+2, "%d", &id);
            Game* g = buscarPorId(base,total,id);
            if (g) inserirInicio(&lista, g);
        } else if (strncmp(buf,"IF",2)==0){
            int id; sscanf(buf+2, "%d", &id);
            Game* g = buscarPorId(base,total,id);
            if (g) inserirFim(&lista, g);
        } else if (strncmp(buf,"I*",2)==0){
            int pos,id; sscanf(buf+2, "%d %d", &pos,&id);
            Game* g = buscarPorId(base,total,id);
            if (g) inserirPos(&lista, g, pos);
        } else if (strncmp(buf,"RI",2)==0){
            Game* r = removerInicio(&lista);
            if (r) printf("(R) %s\n", r->name);
        } else if (strncmp(buf,"RF",2)==0){
            Game* r = removerFim(&lista);
            if (r) printf("(R) %s\n", r->name);
        } else if (strncmp(buf,"R*",2)==0){
            int pos; sscanf(buf+2, "%d",&pos);
            Game* r = removerPos(&lista, pos);
            if (r) printf("(R) %s\n", r->name);
        }
    }

    for (No* p=lista.primeiro; p; p=p->prox) imprimirInfoJogo(p->g);

    No* p=lista.primeiro;
    while (p){ No* nx=p->prox; free(p); p=nx; }

    for (int i=0;i<total;i++) liberarMemoriaJogo(&base[i]);
    free(base);
    return 0;
}
