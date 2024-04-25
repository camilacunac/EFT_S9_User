FROM openjdk:21-ea-24-oracle

WORKDIR /app
COPY target/users-0.0.1-SNAPSHOT.jar app.jar
COPY Wallet_PW8HJQLCLP5FQYK2 /app/oracle_wallet
EXPOSE 8080

CMD [ "java", "-jar", "app.jar" ]