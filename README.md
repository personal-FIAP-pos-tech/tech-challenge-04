# Tech Challenge Fase 4

Aplicacao serverless em AWS para receber avaliacoes de aulas, classificar a urgencia do feedback, notificar administradores em casos criticos e enviar um relatorio semanal de satisfacao.

## Plataforma Serverless de Feedbacks

O objetivo do projeto e disponibilizar uma API simples e segura para coleta de feedbacks academicos. A solucao foi implementada com servicos gerenciados da AWS para reduzir operacao, evitar servidores dedicados e permitir escalabilidade sob demanda.

O fluxo principal e:

1. O usuario envia uma avaliacao pelo endpoint `POST /avaliacao`.
2. A API Gateway valida a API key e encaminha a requisicao para uma Lambda.
3. A Lambda valida o payload, classifica a urgencia e persiste o feedback no DynamoDB.
4. Feedbacks criticos publicam uma notificacao em SNS.
5. Uma segunda Lambda consolida semanalmente os feedbacks e envia o relatorio por e-mail.

## Arquitetura

A arquitetura macro do desafio e serverless, com infraestrutura declarada em `template.yaml` usando AWS SAM e CloudFormation.

```text
Cliente HTTP
    |
    v
API Gateway REST + API Key
    |
    v
ReceiveFeedbackFunction
    |             |
    |             v
    |         SNS CriticalFeedbackTopic
    v
DynamoDB FeedbackTable
    ^
    |
WeeklyReportFunction <--- EventBridge Schedule
    |
    v
SNS WeeklyReportTopic
```

### Componentes

- `FeedbackApi`: API REST publicada no stage `prod`, protegida por API key e usage plan.
- `ReceiveFeedbackFunction`: Lambda Java responsavel por receber, validar, classificar e salvar feedbacks.
- `FeedbackTable`: tabela DynamoDB com os atributos `id`, `description`, `grade`, `urgency` e `sendDate`.
- `CriticalFeedbackTopic`: topico SNS usado para alertas de feedback critico.
- `WeeklyReportFunction`: Lambda Java responsavel por consolidar os feedbacks dos ultimos 7 dias.
- `WeeklyReportTopic`: topico SNS usado para envio do relatorio semanal.
- `EventBridge`: agenda a execucao semanal da funcao de relatorio.
- `CloudWatch`: centraliza logs, metricas, alarmes e dashboard operacional.

## Decisoes Tecnicas

- **API Gateway**: escolhido para expor a API HTTP com API key, throttling e logs nativos.
- **Lambda**: escolhida para executar codigo sob demanda, sem manter EC2 ou containers sempre ativos.
- **DynamoDB**: escolhido por ser serverless e adequado para registros independentes de feedback.
- **SNS**: escolhido para notificacoes simples por e-mail, sem a configuracao adicional exigida pelo SES.
- **EventBridge**: escolhido para agendamento gerenciado do relatorio semanal.
- **CloudWatch**: escolhido para observabilidade nativa da stack AWS.
- **SAM/CloudFormation**: escolhido para manter infraestrutura como codigo e facilitar reproducibilidade.

## Endpoint

### Criar avaliacao

```http
POST /avaliacao
Content-Type: application/json
x-api-key: <api-key>
```

Request:

```json
{
  "descricao": "A aula travou durante a explicacao principal.",
  "nota": 2
}
```

Response:

```json
{
  "id": "uuid",
  "descricao": "A aula travou durante a explicacao principal.",
  "nota": 2,
  "urgencia": "CRITICA",
  "dataEnvio": "2026-05-13T23:00:00Z"
}
```

### Classificacao

| Nota | Urgencia |
| --- | --- |
| `0` a `3` | `CRITICA` |
| `4` a `6` | `MEDIA` |
| `7` a `10` | `BAIXA` |

## Principais Tecnologias

- Java 21
- Maven
- AWS SAM
- AWS Lambda
- Amazon API Gateway
- Amazon DynamoDB
- Amazon SNS
- Amazon EventBridge
- Amazon CloudWatch
- AWS CloudFormation

## Seguranca e Observabilidade

- A API exige `x-api-key`.
- O usage plan aplica quota e throttling.
- As Lambdas usam roles IAM criadas pelo SAM com permissoes especificas.
- A tabela DynamoDB possui criptografia em repouso e point-in-time recovery.
- Logs possuem retencao de 30 dias.
- Alarmes CloudWatch monitoram erros das Lambdas.
- O dashboard CloudWatch consolida invocacoes, erros e duracao.
