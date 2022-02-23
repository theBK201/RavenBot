import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class Main {

    public static void main(String[] args) {
        DiscordClient client = DiscordClient.create("Nzc4OTczNjc2MTA3OTg4OTk0.X7ZxxQ.K_2KJP_mR8i63GklikT_MnHhm3o");

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

            return printOnLogin.and(handlePingCommand).and(ninjaCommand);
        });

        login.block();
    }
}


