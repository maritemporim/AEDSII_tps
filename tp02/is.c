#include <stdio.h>
#include <string.h>
#include <ctype.h>

int vogal(char s[]) {
    for (int i = 0; s[i] != '\0'; i++) {
        char c = tolower(s[i]);
        if (!(c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u'))
            return 0;
    }
    return (strlen(s) > 0);
}

int consoante(char s[]) {
    for (int i = 0; s[i] != '\0'; i++) {
        char c = tolower(s[i]);
        if (!isalpha(c)) return 0;
        if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u')
            return 0;
    }
    return (strlen(s) > 0);
}

int inteiro(char s[]) {
    int i = 0;
    if (s[0] == '-' || s[0] == '+') i++;
    if (s[i] == '\0') return 0;
    for (; s[i] != '\0'; i++) {
        if (!isdigit(s[i])) return 0;
    }
    return 1;
}

int real(char s[]) {
    int i = 0, p = 0;
    if (s[0] == '-' || s[0] == '+') i++;
    if (s[i] == '\0') return 0;
    for (; s[i] != '\0'; i++) {
        if (s[i] == '.' || s[i] == ',') {
            if (p) return 0;
            p = 1;
        } else if (!isdigit(s[i])) {
            return 0;
        }
    }
    return p;
}

int main() {
    char s[1000];
    while (1) {
        fgets(s, 1000, stdin);
        s[strcspn(s, "\n")] = '\0';
        if (strcmp(s, "FIM") == 0) break;

        if (vogal(s)) printf("SIM ");
        else printf("NAO ");

        if (consoante(s)) printf("SIM ");
        else printf("NAO ");

        if (inteiro(s)) printf("SIM ");
        else printf("NAO ");

        if (real(s)) printf("SIM\n");
        else printf("NAO\n");
    }
    return 0;
}
