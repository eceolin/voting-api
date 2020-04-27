# Sobre o projeto

O objetivo do projeto é disponibilizar uma API para gerenciar sessões de votação dos associados. O projeto foi desenvolvido utilizando o ecossistema Spring, que é bastante conhecido e utilizado devido a sua praticidade e facilidade de uso. Para a camada web foi utilizado o módulo Spring WebFlux. Este módulo traz a capacidade de utilizar programação reativa, que entre as principais vantagens está a elasticidade e escalabilidade. Para o banco de dados, foi utilizado o Mongo, que é um banco NoSQL e possui suporte a programação reativa. Também foi utilizado JPA, que é um framework ORM que permite a abstração da camada de banco de dados e dessa forma traz independência do banco. O deploy foi realizado no Heroku, plataforma que permite o uso gratuito para aplicações Java e também disponibiliza o mongo como banco para realizar testes.

# Compilando o projeto

Para compilar o projeto,  é utilizado o maven através do comando abaixo:

```mvn clean compile```

Executar os testes:

```mvn test```

Para gerar um binário executável, empacote o arquivo:

```mvn package```

Para rodar o projeto localmente, configure o banco de dados no application.yml. Por padrão, ele tenta acessar o database "local" na porta 27017.
Para executar, empacote o projeto e acesse a pasta "target". Após, execute:

```java -jar voting-api.jar```

Ele irá subir o netty na porta 8080.

# Documentação da API

Para consultar os métodos disponíveis na API, acesse a documentação através do link:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

# Acesso API online

Para acessar a API online, utilize o link abaixo:

[http://test-voting-api.herokuapp.com/api/v1/](http://test-voting-api.herokuapp.com/api/v1/)

A documentação online está disponível em:

[http://test-voting-api.herokuapp.com/swagger-ui.html](http://test-voting-api.herokuapp.com/swagger-ui.html)
