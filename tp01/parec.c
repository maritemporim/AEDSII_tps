#include <stdio.h>
#include <string.h>

void trocarR(char *str, int inicio, int fim) { 
    if (inicio >= fim) { // condição de parada da função
        return;
    }

    char temp = str[inicio]; // invertendo a palavra
    str[inicio] = str[fim];
    str[fim] = temp;

    trocarR(str, inicio + 1, fim - 1); // chamada recursiva funcionando como um for
}

int main(void) {
    char letras[100], str[100]; 

    fgets(letras, sizeof(letras), stdin);
    letras[strcspn(letras, "\n")] = '\0';

    while (strcmp(letras, "FIM") != 0) { // enquanto a palavra digitada pelo usuario não for "FIM"
        strcpy(str, letras); 
        int tam = strlen(letras);
        trocarR(letras, 0, tam - 1); // chama a função

        if (strcmp(letras, str) == 0) { // se forem iguais as palavras
            printf("SIM\n"); // imprime sim
        } else { // se não
            printf("NAO\n"); // imprime não
        }

        fgets(letras, sizeof(letras), stdin);
        letras[strcspn(letras, "\n")] = '\0';
    }

    return 0;
}