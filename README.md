# Sobre o projeto

O objetivo do projeto é gerenciar sessões de votação através de um API. O desenvolvimento foi realizado utilizando o Spring Framework, que é bastante conhecido e utilizado devido a sua praticidade e facilidade de uso. Para o banco de dados, foi utilizado a JPA, que é um framework ORM que permite a abstração da camada de banco de dados e dessa forma traz independência do banco. Por ser um projeto de teste, foi configurado para a JPA criar as tabelas, ao invés do Flyway para gerenciar as versões de banco. O deploy foi realizado no Heroku, plataforma que permite o uso gratuito para aplicações Java e também disponibiliza o postgresql como banco para realizar testes.

# Compilando o projeto

Para compilar o projeto,  é utilizado o maven através do comando abaixo:

`mvn clean compile`

Executar os testes:

`mvn test`

Para gerar um binário executável, empacote o arquivo:

`mvn package`

Para rodar o projeto localmente, configure o banco de dados no application.properties. Por padrão, ele tenta acessar o database "votingapi" com user e senha "postgres". Se for usar essas configurações padrões, crie o database com o comando:

`create database votingapi;`

Para executar, compile e empacote o projeto e acesse a pasta "target". Após, execute:

`java -jar voting-api.jar`

Ele irá subir o apache tomcat na porta 8080.

# Consumindo a API

Abaixo está descrito os métodos disponíveis para uso na API.

### Cadastrando uma nova pauta

`curl --location --request POST 'https://test-voting-api.herokuapp.com/api/v1/pautas' \
--header 'Content-Type: application/json' \
--data-raw '{
	"assunto" : "Investir em desenvolvimento sustentável"
}'`

### Consultar pautas cadastradas

`curl --location --request GET 'https://test-voting-api.herokuapp.com/api/v1/pautas'`

### Iniciar uma sessão de votação para a pauta

`curl --location --request POST 'https://test-voting-api.herokuapp.com/api/v1/sessoes' \
--header 'Content-Type: application/json' \
--data-raw '{
	"dataInicio" : "2020-03-05T19:00:00",
	"dataFim" : "2020-03-05T19:05:00",
	"pauta": {
		"codigo" : 7
	}
}'`

Os parâmetros dataInicio e dataFim são opcionais. Se dataInicio não for informada, será adotada a data atual. Se a dataFim não for informada, será considerado 1 minuto após a data de inicio.

### Consultar sessões cadastradas

`curl --location --request GET 'https://test-voting-api.herokuapp.com/api/v1/sessoes'`

### Votar em uma pauta

`curl --location --request POST 'https://test-voting-api.herokuapp.com/api/v1/votos' \
--header 'Content-Type: application/json' \
--data-raw '{
	"codigoAssociado" : 1,
	"cpfAssociado" : "72783475057",
	"codigoPauta" : 7,
	"voto": true
}'`

O código do associado é utilizado como id único e o CPF é utilizado para validar se o associado pode votar. O CPF não é persistido no banco, para não vincular o voto diretamente com o CPF.

### Consultar o resultado da votação

`curl --location --request GET 'https://test-voting-api.herokuapp.com/api/v1/sessoes/5/resultado'`

É enviado o id da sessão como uma variável na url: "/api/v1/sessoes/{id}/resultado"

