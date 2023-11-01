Api para sistema de parquímetro
========================

# 👓 Introdução

![status_desenvolvimento](https://img.shields.io/static/v1?label=Status&message=Em%20Desenvolvimento&color=yellow&style=for-the-badge)


![Badge Java](https://img.shields.io/static/v1?label=Java&message=17&color=orange&style=for-the-badge&logo=java)


![framework_back](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![server_ci](https://img.shields.io/badge/Github%20Actions-282a2e?style=for-the-badge&logo=githubactions&logoColor=367cfe)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)


![example workflow](https://github.com/vsantsal/sistema-parquimetro-api/actions/workflows/maven.yml/badge.svg)
![Coverage](.github/badges/jacoco.svg)

Sistema de parquímetro para atender a demanda de estacionamento crescente de cidade turística.

Haja vista o aumento significativo durante alta temporada, de quase 100% da frota de veículos utilizando o sistema, é um importante requisito que a solução seja escalável.

O link no Github é https://github.com/vsantsal/sistema-parquimetro-api.

# 🔬 Escopo

O protótipo do sistema ora apresentado é feito sob a ótica dos usuários que exercem o papel de *condutor* na aplicação.

Reconhece-se haver outros casos de usos envolvendo outros papéis no domínio do sistema (por exemplo, *proprietários de estacionamento*, *administradores de sistema* etc.), porém serão abstraídos na presente implementação. 

Assim, as principais funcionalidades disponibilizadas pelo sistema são:

- Autenticar Usuário
- Manter Condutor
- Manter Veículo
- Manter Forma de Pagamento
- Iniciar Período de Estacionamento
- Controlar Tempo Estacionado
- Alertar Tempo Estacionado
- Pagar Estacionamento
- Emitir Recibo

Podemos sintetizar as interações do principal ator do sistema - um condutor - no seguinte diagrama de casos de uso:

![Diagrama de Casos de Uso](https://github.com/vsantsal/sistema-parquimetro-api/blob/main/docs/v3-casos-de-uso-sistema.png)

Não pretendemos sobrecarregar os leitores com uma especificação rígida e pesada dos fluxos principal e alternativos para os casos de usos acima, considerando suficiente para o objetivo do presente trabalho descreveremos as principais interaçoes entre os atores e o sistema. 

**Autenticar Usuario**: um visitante nao logado acessa a API visando ao seu cadastro nela. Ele fornece *login* e *senha*, alem de dados cadastrais necessarios para a aplicaçao identifica-lo como condutor. Caso o *login* ja tenha sido utilizado por outro usuario, o sistema informa a impossibilidade de utiliza-lo novamente.

**Manter Condutor**: um condutor (isto e, um usuario autenticado), pode visualizar seus dados cadastrais na API, alem de atualiza-los ou solicitar inativaçao de sua conta. Nao lhe e permitida a visualizaçao de dados de outros participantes.

**Manter Veículo**: um condutor pode visualizar os veículos associados a sua conta na API, além registrar ou excluir. Um condutor pode vincular vários veículos à sua conta. Na versão inicial do programa, cada veículo somente poderá estar associado a um condutor por vez.

**Iniciar período de estacionamento**: um condutor com forma de pagamento registrada pode iniciar o registro de tempo no sistema, informando veículo a estacionar e estacionamento, além de escolher entre tempo fixo (com duração desejada) ou por hora.

**Controlar Tempo Estacionado** \ **Alertar Tempo Estacionado**: esses casos de uso são percebidos pelos condutores ao consultarem o endpoint adequado da API, que retornará o tempo decorrido e eventuais alertas emitidos. Para horários fixos, o sistema emite alerta faltando 10 minutos para expiração. Para horários variáveis, o sistema emite alerta (no mesmo marco anterior, considerando como fim uma hora após o início do registro) informando que estenderá o estacionamento por mais uma hora caso não seja encerrado o período.

**Pagar Estacionamento** \ **Emitir Recibo**: um condutor encerra o período e paga o valor devido, implicando emissão de recibo com informações de tempo estacionado, tarifa aplicada e valor total pago. Para tempos fixos, o valor total é cobrado independente do tempo utilizado, ao passo que para períodos variáveis a cobrança se dá por hora completa.

# 📖 Funcionalidades

Abaixo, descrevemos globalmente as funcionalidades implementadas.

Observar que o projeto se vale do *Swagger* para gerar documentação automaticamente, nos formatos *HTML*, *JSON* e *YAML*, nos *endpoints* padrão (`swagger-ui.html` e `v3/api-docs`).

## Autenticar Usuário

Nossa API Rest deve suportar cadastro e posterior login para usuários, disponíveis nos *endpoints* `auth/registrar` e `auth/login`, respectivamente.

Para o POST em `auth/registrar`, o *body* de cada requisição deve informar JSON no seguinte formato:

```json
{
    "login": "username",
    "senha": "password",
    "condutor": {
        "nome": "Nome do Usuário",
        "email": "email.do.usuario@email.com",
        "endereco": "Logradouro ABC, 123",
        "telefone":"5599123456789"        
    }
}

```

Em caso de cadastro bem sucedido, a aplicação retorna resposta com status HTTP usual (200).

Caso haja nova tentativa de cadastro, a aplicação retornará o erro informando, conforme abaixo:

```json
{
    "mensagem": "Condutor já cadastrado"
}
```

Para o POST em `auth/login`, o *body* de cada requisição deve informar JSON no seguinte formato:

```json
{
    "login": "username",
    "senha": "password"
}
```

Em caso de login inválido, a aplicação retorna o status 403 (sem mensagem).

Em caso de login bem sucedido, a aplicação retornará token JWT que o cliente deverá informar a cada nova solicitação.

## Manter Condutor

Nossa API Rest deve suportar a manutenção do cadastro de condutores.

O enpdpoint será baseado em `/condutores`, suportando os métodos HTTP GET, PUT, DELETE.

O GET no endpoint somente pode ser realizado complementando com ID - mais especificamente, a ID do usuário autenticado.

A resposta da requisição ocorre como no exemplo abaixo.

```json
{
    "nome": "Nome do Usuário",
    "endereco": "Logradouro ABC, 123",
    "email": "email.do.usuario@email.com",
    "telefone": "5599123456789",
    "id": "abcde"
}
```

Caso tente consultar id de outro usuário (ou inexistente), a aplicação lança erro e informa como abaixo.

```json
{
  "mensagem": "Recurso inválido"
}
```
Para o PUT, deve-se passar  o id do condutor a atualizar no endpoint (por exemplo, `condutores/abce`) e os novos valores para os campos no corpo da requisição, conforme abaixo:

```json
{
    "nome": "Nome 2 do Usuário",
    "endereco": "Logradouro DEF, 456",
    "email": "email2.do.usuario@email.com",
    "telefone": "5599123456780"
}
```
A aplicação fará as atualizações dos campos e retornará o STATUS CODE 200, em caso de sucesso.
Um condutor logado somente poderá atualizar seus próprios dados.

Para o DELETE, deve-se passar o id do condutor a remover no endpoint (por exemplo, `condutores/abcde`). 
A aplicação marcará internamente o identificador `ativo` como false do modelo e retornará o STATUS CODE 204. 
Assim como nos demais verbos, o usuário logado somente poderá inativar seu registro.

## Manter Forma de Pagamento

Nossa API Rest deve suportar a manutenção da forma de pagamento preferida de um condutor

O enpdpoint será baseado em `/pagamentos/forma`, suportando os métodos HTTP GET, PUT, POST.

O GET no endpoint é realizado sem passagem de ID.

A resposta da requisição ocorre como no exemplo abaixo.

```json
{
    "tipo": "PIX",
    "tiposAceitosTempoEstacionado": ["FIXO"]
}
```

Caso não possua forma de pagamento preferida registrada ainda, a aplicação lança erro e informa como abaixo.

```json
{
  "mensagem": "Forma de pagamento preferida não registrada"
}
```
Para o PUT e para o POST, deve-se passar no corpo da requisição o tipo de forma de pagamento desejado:

```json
{
    "tipo": "CARTAO_DE_CREDITO"
}
```
A aplicação fará as atualizações dos campos e retornará o STATUS CODE 200, em caso de sucesso.

Um condutor logado somente poderá atualizar seus próprios dados.

## Manter Veículo

Nossa API Rest deve suportar a manutenção de veículos pelos condutores.

O enpdpoint será baseado em `/veiculos`, suportando os métodos HTTP GET, POST e DELETE.

Para o POST, o *body* de cada requisição deve informar JSON no seguinte formato:

```json
{
  "placa": "ABC1234"
}
```

A resposta da requisição ocorre como no exemplo abaixo, trazendo, além da placa informada (caso seja válida), os ids do veículo e do condutor que o cadastrou.

```json
{
  "placa": "ABC1234",
  "veiculoId": "123ab1cd3456d2ed65b2d3f",
  "condutorId": "123a12345feb343a11a2a588"
}
```

Caso a placa informada seja inválida, a aplicação retorna a mensagem de erro abaixo.

```json
{
  "mensagem": "Valor incorreto de placa informado"
}
```

Caso algum usuário já esteja utilizando o carro, também há sinalização de erro.

```json
{
  "mensagem": "Veículo já em uso na plataforma"
}
```

O GET no endpoint pode ser realizado com ou sem a passagem de ID do veículo. 
Quando for passado ID, o retorno é o mesmo do POST após o cadastro do veículo. Sem ID, retorna lista desses mesmos DTOs.
O condutor somente poderá visualizar veículos associados à sua conta.

Para o DELETE, deve-se passar o id do veículo a remover no endpoint (por exemplo, `veiculos/xyz`).
A aplicação marcará internamente o identificador `ativo` como false do modelo e retornará o STATUS CODE 204.
Assim como nos demais verbos, o usuário logado somente poderá inativar veículos associados a sua conta.

## Iniciar período de estacionamento

Nossa API Rest deve suportar a manutenção de veículos pelos condutores, a partir de método POST.

O enpdpoint será baseado em `/estacionamentos/usar`.

Para o POST, o *body* de cada requisição deve informar JSON no seguinte formato, em caso de solicitação para período variável a estacionar.

```json
{
  "placaVeiculo": "ABC1234",
  "cnpjEstacionamento": "71146289000108",
  "tipoTempoEstacionado": "VARIAVEL",
  "inicio": "2023-10-01T11:05:42"
}
```

Para período fixo, deve haver inclusao do campo duracao, com valor no formato HH:MM:SS

```json
{
  "placaVeiculo": "ABC1234",
  "cnpjEstacionamento": "71146289000108",
  "tipoTempoEstacionado": "FIXO",
  "inicio": "2023-10-01T11:05:42",
  "duracao": "01:00:00"
}
```

Para ambas solicitações, em caso de sucesso, é retornado status 201, com localização do recurso criado.

Conforme regras de negócio, caso haja incompatibilidade entre forma de pagamento preferida do condutor e tipo de período (fixo/variável), a aplicaçao retorna o erro abaixo:

````json
{
  "mensagem": "Período de estacionamento inválido para forma de pagamento"
}
````

Se condutor solicitar uso de estacionamento com tempo fixo sem informar duração, a aplicação critica conforme abaixo:

```json
{
  "mensagem": "Necessário informar duração para tipoTempoEstacionado 'FIXO'"
}
```

Igualmente, caso condutor tente iniciar um período de estacionamento antes de selecionar uma forma de pagamento na aplicação, também é lançado erro.

```json
{
  "mensagem": "É necessário selecionar forma de pagamento válida antes de estacionar"
}
```
Caso seja informado uma forma inválida de tipo de tempo estacionado (por exemplo, `INVALIDO`), também há tratamento para a situação.

```json
{
  "mensagem": "Valor 'INVALIDO' inválido para 'tipoTempoEstacionado'"
}
```

Se condutor tentar estacionar veículo inexistente ou não associado à sua conta (por exemplo, placa `XYZ1A23`), é informado o erro abaixo:

```json
{
  "mensagem": "Não foi possível localizar o veículo correspondente à placa 'XYZ1A23'"
}
```

## Controlar Tempo Estacionado \ Alertar Tempo Estacionado

Os dois casos de uso são observados pelos condutores através do método GET *endpoint* `/estacionamentos/usar`, com ou sem passagem de `id` (naturalmente, o segundo caso também ao receberem e-mail\notificação no app).

Especialmente considerando os períodos de grande demanda, e a expectativa de, para cada condutor logado, haver um histórico razoável de usos de estacionamento, adota-se estratégia de paginação para apresentá-lo aos condutores. 

O retorno paginado acontece conforme exemplo abaixo:

```json
{
    "content": [
        {
            "placaVeiculo": "ABC1234",
            "cnpjEstacionamento": "71146289000108",
            "tipoTempoEstacionado": "VARIAVEL",
            "inicio": "2023-10-31T14:36:00",
            "duracaoDecorrida": "PT3H18M32.115785725S",
            "duracaoLimite": "PT4H",
            "fim": null,
            "total": null,
            "alertas": [
                "Tempo de estacionamento prestes a expirar. Estacionamento será estendido automaticamente caso não seja encerrado.",
                "Tempo de estacionamento prestes a expirar. Estacionamento será estendido automaticamente caso não seja encerrado.",
                "Tempo de estacionamento prestes a expirar. Estacionamento será estendido automaticamente caso não seja encerrado."
            ],
            "id": "65414db356e09205d7e79ef4"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "first": true,
    "numberOfElements": 1,
    "empty": false
}


```

# 🥼 Testes e CI/CD

Há testes de integração para os controllers de modo a confirmar os principais comportamentos.

Configuramos *workflow* no Actions para executar os testes em integrações de código no ramo principal (*main*), além de permitir seu *bot* a atualizar a *badge* de cobertura de código pelos testes.

# 🐳 Contêineres

Disponibilizamos imagem de modo a possibilitar explorações e testes manuais da aplicação pelos usuários.

Para rodar, basta executar:

`docker-compose up --build`

Interrompe-se o contêiner por meio do comando:

`docker-compose down`

# 🗓️ Resumo Desenvolvimento

* Para cadastro de usuários e login na aplicação, adicionamos dependências [*Spring Security*](https://spring.io/projects/spring-security) e [*auth0/java-jwt*](https://github.com/auth0/java-jwt);
* Para os testes, subimos um banco de dados de testes `MongoDB` em vez de recorrer a banco em memória. Para tanto, recorremos à classe `MongoTemplate`, conforme documentação em https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html;
* Configuramos *workflow* de execução de testes automáticos quando houver integração ao ramo principal (`main`) por meio do *Github Actions*;
* Para permitir que os testes de integração fossem executados no `Github Actions`, conferimos a documentação disponível em https://github.com/marketplace/actions/mongodb-in-github-actions de modo a criar o `step` necessário a subir o servidor;
* Em https://docs.github.com/en/actions/learn-github-actions/variables, visualizamos como informar variáveis de ambiente para serem usadas em execuções de testes no `Github Actions`;
* Implementação de métrica de cobertura de código pelos testes, com habilitação do *github-actions bot* para gerar *badge*;
* Em https://spring.io/blog/2021/11/29/spring-data-mongodb-relation-modelling, visualizamos como implementar o relacionamento modelado entre condutores e veículos;
* Utilização de estratégia de paginação (interface [Pageable](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Pageable.html)) para recuperação da consulta mais "pesada" da aplicação - isto é, a consulta do histórico de usos de estacionamento pelos condutores; 
* Incluímos `Dockerfile` e `docker-compose.yml` para disponibilizar imagem de modo a facilitar explorações manuais que se deseje fazer da aplicação.

## ⚠️ Pontos de atenção

* Aumentar cobertura de código pelos testes
* Criar aplicação para automação de rotinas por usuários de serviço (controle e alerta de tempo estacionado)
