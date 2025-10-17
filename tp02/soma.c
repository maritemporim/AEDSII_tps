#include <stdio.h>

int soma(int num){
    if(num < 10) return num;
    return (num % 10) + soma(num / 10);
}

int main(void){
    int num;
    while (scanf("%d",&num)) {
        int s = soma(num);
        printf("%d\n", s);
    }
    return 0;
}
