<img src="https://github.com/user-attachments/assets/e5d216a8-f538-4373-a065-4449fb530631" width=380px height=100px>
<br>
<br>

![Java 21.0.6](https://img.shields.io/badge/Java-21.0.6-f08c00?style=flat&logo=openjdk&logoColor=white) 
![JavaFX 23.0.1](https://img.shields.io/badge/JavaFX-23.0.1-0078D7?style=flat&logo=javafx&logoColor=white) 
[![OSHI 6.8.3](https://img.shields.io/badge/OSHI-6.8.3-25bf0d?style=flat&logo=java&logoColor=white)](https://github.com/oshi/oshi) 
![Maven](https://img.shields.io/badge/Maven-3.9.9-C71A36?style=flat&logo=apachemaven&logoColor=white)

# Monitor de processos e gerenciador de Hardware
  
Monitor de processos em Java, que usa a biblioteca **OSHI** para informações de 
processos / hardware e **JavaFX** para visualização das métricas, com interface intuitiva 
inspirada em gerenciadores nativos de sistemas operacionais.

## Compatível com: 
![Windows](https://img.shields.io/badge/Windows-0078D6?style=for-the-badge&logo=windows&logoColor=A8E4A0)

## É possível ver métricas de:

- Processador
- Memória RAM
- Disco
- Rede

## Screenshots e detalhes das funcionalidades

## Processador: 
![CpuView](https://github.com/user-attachments/assets/53e05398-7725-491f-b3f9-639326787567)

Nesta view, o usuário pode ver informações sobre o processador, como: 

- Nome
- Porcentagem usada pelo sistema (porcentagem e gráfico)
- Porcentagem usada pelos usuários (porcentagem e gráfico)
- Quantidade de Processos rodando
- Quantidade total de Threads consumidas por todos os Processos
- Clock Base
- Quantidade de núcleos
- Quantidade de Processadores Lógicos

E também uma lista de processos ativos no sistema, com especificações de: 

- Nome
- ID do Processo
- Porcentagem usada do processador
- Threads usadas
- Tempo de execução no processador
- Usuário executando o processo

## Memória RAM:
![MemView](https://github.com/user-attachments/assets/72c6b9d9-1ac8-4736-a7f8-2ac17e0e483b)

Aqui, é possível ver informações de:

- Quantidade total de memória
- Total de memória disponível para uso 
- Quantidade de memória sendo utilizada (valor em **GB**, porcentagem e gráfico)
- Total de memória reservada para Hardware
- Frequência das memórias em **MHz**
- Total de slots da placa-mãe usados
- Tipo da memória

E também informações específicas de processos ativos:

- Nome
- ID do Processo
- Porcentagem usada da memória
- Threads usadas
- Usuário executando o processo

## Disco: 
![DiskView](https://github.com/user-attachments/assets/e6559dbb-b5bb-45ee-b27b-be86eb28737e)

Na View de Disco, o usuário encontra informações de armazenamento do seu hardware, contando com uma lista de dispositivos de armazenamento e suas respectivas informações de: 

- Nome 
- Porcentagem de tempo de atividade
- Capacidade de armazenamento
- Quantidade de dados escritos e lidos (em **GB**)

O usuário também pode selecionar um dispositivo dentro da lista, e acessar algumas informações adicionais: 

- Quantidade utilizada do armazenamento (Em **GB** e barra de progresso representando o uso)
- Velocidade de escrita e leitura do dispositivo, que varia entre **KB/s**, **MB/s** e **GB/s** de acordo com a velocidade
- Capacidade real do dispositivo
- Capacidade do dispositivo formatado

## Rede:
![NetworkView](https://github.com/user-attachments/assets/7f3ec197-15b5-4ebf-996b-0e2218b9f746)

> <strong>Endereços IP e MAC utilizados na imagem acima são fictícios por questões de privacidade, mas os endereços reais são mostrados durante a execução do programa.</strong>

Na view rede, é possível ver quantos adaptadores de rede estão presentes no seu hardware, com informações de: 

- Nome
- Tipo de interface
- Quantidade de **GB** enviados
- Quantidade de **GB** recebidos

Também é possível selecionar um adaptador dentro da lista e obter mais informações, como: 

- Endereço Ipv4
- Endereço Ipv6 
- Endereço MAC 
- Pacotes enviados
- Pacotes recebidos
- velocidade de envio e recepção de dados, que varia entre **Kbps**, **Mbps** e **Gbps** de acordo com a velocidade
