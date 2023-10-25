Api para sistema de parquímetro
========================

# 👓 Introdução

![status_desenvolvimento](https://img.shields.io/static/v1?label=Status&message=Em%20Desenvolvimento&color=yellow&style=for-the-badge)


![Badge Java](https://img.shields.io/static/v1?label=Java&message=17&color=orange&style=for-the-badge&logo=java)


![framework_back](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![server_ci](https://img.shields.io/badge/Github%20Actions-282a2e?style=for-the-badge&logo=githubactions&logoColor=367cfe)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

Sistema de parquímetro para atender a demanda de estacionamento crescente de cidade turística.

Haja vista o aumento significativo durante alta temporada, de quase 100% da frota de veículos utilizando o sistema, é um importante requisito que a solução seja escalável.

O link no Github é https://github.com/vsantsal/sistema-parquimetro-api.

# 🔬 Escopo

As principais funcionalidades disponibilizadas pelo sistema são:

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

![Diagrama de Casos de Uso](https://github.com/vsantsal/sistema-parquimetro-api/blob/main/docs/v2-casos-de-uso-sistema.png)

# 📖 Funcionalidades

Abaixo, descrevemos globalmente as funcionalidades implementadas.

## Registrar condutor

# 🗓️ Resumo Desenvolvimento

* Para os testes, subimos um banco de dados de testes `MongoDB` em vez de recorrer a banco em memória. Para tanto, recorremos à classe `MongoTemplate`, conforme documentação em https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/MongoTemplate.html;
* Para permitir que os testes de integração fossem executados no `Github Actions`, conferimos a documentação disponível em https://github.com/marketplace/actions/mongodb-in-github-actions de modo a criar o `step` necessário a subir o servidor;
* Em https://docs.github.com/en/actions/learn-github-actions/variables, visualizamos como informar variáveis de ambiente para serem usadas em execuções de testes no `Github Actions`; 