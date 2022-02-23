import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.io.IOException;

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
        DiscordClient client = DiscordClient.create(token);

        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
                                final User self = event.getSelf();
                                System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
                            }))
                    .then();

            Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();

                if (message.getContent().equalsIgnoreCase("!hi")) {
                    return message.getChannel()
                            .flatMap(channel -> channel.createMessage("Hello!"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> ninjaCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!ninja")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://tenor.com/view/anime-cringe-anime-anime-ninja-cringe-maikati-gif-23429449"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> nqqLafishCommand = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!nqqlafish")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://media.discordapp.net/attachments/409977790155194370/911702125413793822/254102839_560232261738973_9187923660106385537_n.gif"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> policeiskiSireni = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!puliceiskisireni")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://www.youtube.com/watch?v=KGRKbrOt8Zk"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> hektor = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!hektor")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://tenor.com/view/stoqn-kolev-hektor-halka-ahil-gif-22079378"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> airfryer = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!airfryer")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://cdn.discordapp.com/attachments/836714339510648862/938031296532324403/20220120_202517.jpg"));
                }
                return Mono.empty();
            }).then();

            return printOnLogin.and(handlePingCommand).and(ninjaCommand).and(nqqLafishCommand).and(airfryer).and(hektor).and(policeiskiSireni);
        });

        Mono<Void> responses = client.withGateway((GatewayDiscordClient gateway) -> {
            Mono<Void> binChilling = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!binchilling")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://tenor.com/view/bing-chi-ling-alex-mei-bing-chi-ling-alex-gif-21908053"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> xinaChilling = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!xinachilling")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://cdn.discordapp.com/attachments/763094663975534615/945987369125941268/johnxinaheader.png"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> socialCreditScoreUp = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!scoreUp")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://cdn.discordapp.com/attachments/763094663975534615/945987628531064863/hqdefault.png"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> socialCreditScoreDown = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!scoreDown")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://cdn.discordapp.com/attachments/763094663975534615/945987757879218176/0bb.png"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> socialExecution = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!Execution")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://cdn.discordapp.com/attachments/763094663975534615/945987781899988992/b2c8d11a-4a2e-43e6-9623-8c54cb757e5c.png"));
                }
                return Mono.empty();
            }).then();

            Mono<Void> thanosDestroy = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getContent().equalsIgnoreCase("!thanosDestroy")){
                    return message.getChannel().flatMap(channel -> channel.createMessage("https://tenor.com/view/crying-emoji-dies-gif-21956120"));
                }
                return Mono.empty();
            }).then();

            return binChilling.and(xinaChilling).and(socialCreditScoreUp).and(socialCreditScoreDown).and(socialExecution).and(thanosDestroy);
        });

        login.and(responses).block();

    }
}


