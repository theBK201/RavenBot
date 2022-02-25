package dev.bk201.RavenBot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.Embed;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;

//TODO Add HTTP webserver for the bot, see https://github.com/NanoHttpd/nanohttpd
//TODO Add !help command to help the users
//TODO Implement !addResponse so that can users add their responses

public class Main {
    static botToken botToken = new botToken();
    public static String token = null;

    static {
        try {
            token = botToken.getPropValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        GatewayDiscordClient client = DiscordClientBuilder.create(token).build().login().block();

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                 User self = event.getSelf();
                    System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
                });

        EmbedCreateSpec helpMessage = EmbedCreateSpec.builder()
                .color(Color.BLACK)
                .title("Help")
                .description("This message gives you a helpful commands for the Bot")
                .addField("```!addResponse```" ,"This command will add a new response " , false)
                .addField("```!listResponses```", "This command will show you all the responses",false)
                .timestamp(Instant.now())
                .build();

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().equalsIgnoreCase("!help"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage(helpMessage))
                .subscribe();
        client.onDisconnect().block();


//        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
//            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
//                                final User self = event.getSelf();
//                                System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
//                            }))
//                    .then();

    }
}


