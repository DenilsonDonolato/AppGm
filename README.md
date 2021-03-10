# AppGm

Este projeto está configurado para utilizar o Firebase Crashlytics, para compilar o App é necessário adicionar um arquivo de configuração do Firebase `google-services.json` no diretório `app`

Links úteis:
1. [Configurar o Firebase](https://firebase.google.com/docs/android/setup?authuser=0)
2. [Fazer download do google-services.json](https://support.google.com/firebase/answer/7015592?authuser=0)

___
Para executar o App para teste utilize o [MockApiGm](https://github.com/DenilsonDonolato/MockApiGm) (o mock roda na porta 8080)

Para o App conseguir acessar o mock utilize o [ngrok](https://ngrok.com/) e altere o arquivo `build.gradle` na parte utilizando o endereço gerado pelo ngrok



```
    productFlavors {
        hml {
            buildConfigField("String", "BASE_URL", "\"http://177.144.136.156:4000/\"")
        }
        local {
            buildConfigField("String", "BASE_URL", "\"https://ebc5991bffc3.ngrok.io\"")
        }
    }
```
