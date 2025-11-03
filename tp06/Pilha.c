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

int converteParaInt(const char* s){
    if(!s) return 0; while(isspace((unsigned char)*s)) s++;
    int sign=1; if(*s=='+'||*s=='-'){ if(*s=='-') sign=-1; s++; }
    int v=0; while(isdigit((unsigned char)*s)){ v=v*10+(*s-'0'); s++; }
    return sign*v;
}
float converteParaFloat(const char* s){
    if(!s) return 0.0f; char b[128]; int j=0;
    while(*s && j<120){ if(*s==',') b[j++]='.'; else if(!isspace((unsigned char)*s)) b[j++]=*s; s++; }
    b[j]='\0'; return (float)atof(b);
}
static char* strtrimdup(const char* str){
    if(!str) return strdup(""); const char *p=str,*q=str+strlen(str);
    while(p<q && isspace((unsigned char)*p)) p++; while(q>p && isspace((unsigned char)q[-1])) q--;
    size_t n=(size_t)(q-p); char* r=(char*)malloc(n+1); if(!r) return strdup("");
    memcpy(r,p,n); r[n]='\0'; return r;
}
char* formatarData(const char* in){
    if(!in||!*in) return strdup("01/01/0000"); size_t L=strlen(in);
    char tmp[256]; if(in[0]=='"'&&in[L-1]=='"'&&L>=2){ memcpy(tmp,in+1,L-2); tmp[L-2]='\0'; } else { strncpy(tmp,in,sizeof(tmp)-1); tmp[sizeof(tmp)-1]='\0'; }
    char* clean=strtrimdup(tmp); if(*clean=='\0'){ free(clean); return strdup("01/01/0000"); }
    const char* abv[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    const char* mesnum[]={"01","02","03","04","05","06","07","08","09","10","11","12"};
    char dia[3]="01", mes[3]="01", ano[5]="0000";
    char *tok,*ctx=NULL; tok=strtok_r(clean," ",&ctx); char* p1=tok?strdup(tok):strdup("");
    tok=strtok_r(NULL," ",&ctx); char* p2=tok?strdup(tok):strdup("");
    tok=strtok_r(NULL," ",&ctx); char* p3=tok?strdup(tok):strdup("");
    if(*p1&&*p2&&*p3){ for(int j=0;j<12;j++) if(strncmp(p1,abv[j],3)==0){ strcpy(mes,mesnum[j]); break; } char* v=strchr(p2,','); if(v)*v='\0'; if(*p2) snprintf(dia,sizeof(dia),"%02d",converteParaInt(p2)); if(strlen(p3)==4) strcpy(ano,p3); }
    else if(*p1&&*p2){ for(int j=0;j<12;j++) if(strncmp(p1,abv[j],3)==0){ strcpy(mes,mesnum[j]); break; } if(strlen(p2)==4) strcpy(ano,p2); }
    else if(*p1){ if(strlen(p1)==4) strcpy(ano,p1); }
    free(p1); free(p2); free(p3); free(clean);
    char* out=(char*)malloc(11); if(!out) return strdup("01/01/0000"); snprintf(out,11,"%s/%s/%s",dia,mes,ano); return out;
}
char** extrairLista(const char* entradaBruta,int* contador){
    *contador=0; if(!entradaBruta||!*entradaBruta) return NULL; char temp[4096]; int j=0;
    for(size_t k=0;k<strlen(entradaBruta)&&j<4095;k++){ char c=entradaBruta[k]; if(c!='['&&c!=']'&&c!='\''&&c!='"') temp[j++]=c; }
    temp[j]='\0'; char* clean=strtrimdup(temp); if(*clean=='\0'){ free(clean); return NULL; }
    int qtd=1; for(int k=0;clean[k];k++) if(clean[k]==',') qtd++; char** v=(char**)malloc(qtd*sizeof(char*)); if(!v){ free(clean); return NULL; }
    char *tok,*ctx=NULL; tok=strtok_r(clean,",",&ctx); while(tok){ v[*contador]=strtrimdup(tok); (*contador)++; tok=strtok_r(NULL,",",&ctx); }
    free(clean); return v;
}
char** quebrarCSV(const char* linha,int* contadorCampos){
    *contadorCampos=0; int max=14; char** out=(char**)malloc(max*sizeof(char*)); if(!out) return NULL;
    bool asp=false; int ini=0; size_t n=strlen(linha);
    for(size_t k=0;k<=n;k++){ char ch=(k==n)?',':linha[k];
        if(ch=='"') asp=!asp; else if(ch==','&&!asp){ size_t tam=k-ini; char* tmp=(char*)malloc(tam+1); strncpy(tmp,linha+ini,tam); tmp[tam]='\0';
            if(*contadorCampos==1||*contadorCampos==9){ size_t L=strlen(tmp); if(L>=2&&tmp[0]=='"'&&tmp[L-1]=='"'){ tmp[L-1]='\0'; char* z=strdup(tmp+1); free(tmp); tmp=z; } }
            out[*contadorCampos]=tmp; (*contadorCampos)++; ini=(int)k+1; } }
    return out;
}
void imprimirInfoJogoIdx(int idx,const Game* info){
    printf("[%d] => %d ## %s ## %s ## %d ## %.2f ## [", idx, info->id, info->name, info->releaseDate, info->estimatedOwners, info->price);
    for(int j=0;j<info->numSupportedLanguages;j++) printf("%s%s", info->supportedLanguages[j], (j==info->numSupportedLanguages-1)?"":", ");
    printf("] ## %d ## %.1f ## %d ## [", (int)info->mediaScore, (float)info->achievements, (int)info->userScore);
    for(int j=0;j<info->numPublishers;j++) printf("%s%s", info->publishers[j], (j==info->numPublishers-1)?"":", ");
    printf("] ## [");
    for(int j=0;j<info->numDevelopers;j++) printf("%s%s", info->developers[j], (j==info->numDevelopers-1)?"":", ");
    printf("] ## [");
    for(int j=0;j<info->numCategories;j++) printf("%s%s", info->categories[j], (j==info->numCategories-1)?"":", ");
    printf("] ## [");
    for(int j=0;j<info->numGenres;j++) printf("%s%s", info->genres[j], (j==info->numGenres-1)?"":", ");
    printf("] ## [");
    for(int j=0;j<info->numTags;j++) printf("%s%s", info->tags[j], (j==info->numTags-1)?"":", ");
    printf("] ##\n");
}
void liberarMemoriaJogo(Game* g){
    if(!g) return;
    free(g->name); free(g->releaseDate);
    for(int j=0;j<g->numSupportedLanguages;j++) free(g->supportedLanguages[j]); free(g->supportedLanguages);
    for(int j=0;j<g->numPublishers;j++) free(g->publishers[j]); free(g->publishers);
    for(int j=0;j<g->numDevelopers;j++) free(g->developers[j]); free(g->developers);
    for(int j=0;j<g->numCategories;j++) free(g->categories[j]); free(g->categories);
    for(int j=0;j<g->numGenres;j++) free(g->genres[j]); free(g->genres);
    for(int j=0;j<g->numTags;j++) free(g->tags[j]); free(g->tags);
}

Game* carregarCSV(const char* caminho,int* total){
    FILE* f=fopen(caminho,"r"); if(!f) return NULL;
    int cap=1024; *total=0; Game* arr=(Game*)malloc(cap*sizeof(Game)); if(!arr){ fclose(f); return NULL; }
    char linha[8192]; if(!fgets(linha,sizeof(linha),f)){ fclose(f); free(arr); return NULL; }
    while(fgets(linha,sizeof(linha),f)){
        size_t L=strlen(linha); if(L&&linha[L-1]=='\n') linha[L-1]='\0';
        int campos=0; char** s=quebrarCSV(linha,&campos); if(!s) continue;
        if(campos>=14){
            if(*total>=cap){ cap*=2; Game* t=(Game*)realloc(arr,cap*sizeof(Game)); if(!t){ for(int i=0;i<campos;i++) free(s[i]); free(s); break;} arr=t; }
            Game* g=&arr[*total];
            g->id=converteParaInt(s[0]); g->name=strtrimdup(s[1]); g->releaseDate=formatarData(s[2]);
            g->estimatedOwners=converteParaInt(s[3]); g->price=converteParaFloat(s[4]);
            g->supportedLanguages=extrairLista(s[5],&g->numSupportedLanguages);
            g->mediaScore=converteParaFloat(s[6]); g->achievements=converteParaInt(s[7]); g->userScore=converteParaFloat(s[8]);
            g->publishers=extrairLista(s[9],&g->numPublishers); g->developers=extrairLista(s[10],&g->numDevelopers);
            g->categories=extrairLista(s[11],&g->numCategories); g->genres=extrairLista(s[12],&g->numGenres); g->tags=extrairLista(s[13],&g->numTags);
            (*total)++;
        }
        for(int i=0;i<campos;i++) free(s[i]); free(s);
    }
    fclose(f); return arr;
}
Game* buscarPorId(Game* arr,int total,int id){ for(int i=0;i<total;i++) if(arr[i].id==id) return &arr[i]; return NULL; }

typedef struct Celula{ Game* g; struct Celula* prox; } Celula;
typedef struct { Celula* topo; int tamanho; } PilhaGame;
void pilhaInit(PilhaGame* p){ p->topo=NULL; p->tamanho=0; }
void empilhar(PilhaGame* p, Game* g){ Celula* c=(Celula*)malloc(sizeof(Celula)); c->g=g; c->prox=p->topo; p->topo=c; p->tamanho++; }
Game* desempilhar(PilhaGame* p){ if(!p->topo) return NULL; Celula* c=p->topo; Game* g=c->g; p->topo=c->prox; free(c); p->tamanho--; return g; }

int main(){
    const char* caminho="/tmp/games.csv";
    int total=0; Game* base=carregarCSV(caminho,&total); if(!base) return 1;

    PilhaGame pilha; pilhaInit(&pilha);

    char s[256];
    while(fgets(s,sizeof(s),stdin)){
        size_t L=strlen(s); if(L&&s[L-1]=='\n') s[L-1]='\0';
        if(strcmp(s,"FIM")==0) break;
        int id=converteParaInt(s);
        Game* g=buscarPorId(base,total,id);
        if(g) empilhar(&pilha,g);
    }

    if(!fgets(s,sizeof(s),stdin)){ Celula* c=pilha.topo; while(c){ Celula* nx=c->prox; free(c); c=nx; } for(int i=0;i<total;i++) liberarMemoriaJogo(&base[i]); free(base); return 0; }
    int nCmd=converteParaInt(s);

    for(int k=0;k<nCmd;k++){
        if(!fgets(s,sizeof(s),stdin)) break;
        size_t L=strlen(s); if(L&&s[L-1]=='\n') s[L-1]='\0';
        if(s[0]=='I'){ int id=converteParaInt(s+1); Game* g=buscarPorId(base,total,id); if(g) empilhar(&pilha,g); }
        else if(s[0]=='R'){ Game* g=desempilhar(&pilha); if(g) printf("(R) %s\n", g->name); }
    }

    int n=pilha.tamanho;
    Game** v=(Game**)malloc(n*sizeof(Game*));
    int i=0; for(Celula* c=pilha.topo;c;c=c->prox) v[i++]=c->g;
    for(int k=n-1, idx=0; k>=0; k--, idx++) imprimirInfoJogoIdx(idx, v[k]);
    free(v);

    Celula* c=pilha.topo; while(c){ Celula* nx=c->prox; free(c); c=nx; }
    for(int t=0;t<total;t++) liberarMemoriaJogo(&base[t]); free(base);
    return 0;
}
