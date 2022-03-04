package dev.bk201.RavenBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.time.Instant;

//TODO Add HTTP webserver for the bot, see https://github.com/NanoHttpd/nanohttpd
//TODO add command !gp, taking random link for gp video

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

    public static void main(String[] args) throws LoginException {

        JDA client = JDABuilder.createDefault(token).build();

        System.out.println("Logged in as: " + client.getSelfUser().getAsTag());
        client.addEventListener(new helpCommand());
        client.addEventListener(new giveResponse());
        client.upsertCommand("addresponse","This command adds new Response to the bot")
                .addOption(OptionType.STRING,"key","Here type the command")
                .addOption(OptionType.STRING,"value","Give value to your response").queue();

        client.addEventListener(new addResponseCommand());
    }

        public static class addResponseCommand extends ListenerAdapter{
        @Override
            public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
                String msgKey;
                String msgValue;
                Responses responses = new Responses();

                //making sure we are handling the right command
                if(!event.getName().equals("addresponse")) return;
                msgKey = event.getOption("key").getAsString();
                msgValue = event.getOption("value").getAsString();

                //Won't respond to bot
                 if (event.getInteraction().getMember().getUser().isBot()) return;

                 if (responses != null){
                     if (responses.checkForDuplicate(msgKey,true)){
                         event.reply("Your response is already in the Database.").setEphemeral(true).queue();
                     }else {
                         responses.insertResponse(msgKey,msgValue,true);
                         event.reply("Your response with key '" + msgKey + "' has been added.").setEphemeral(true).queue();
                     }
                 }
            }
        }

    public static class helpCommand extends ListenerAdapter{
        @Override
        public void onMessageReceived(MessageReceivedEvent event){
            //Won't respond to bot
            if (event.getAuthor().isBot()) return;
            Message msg = event.getMessage();
            String content = msg.getContentRaw();
            EmbedBuilder help = new EmbedBuilder();

            help.setColor(Color.BLACK);
            help.setTitle("Help!");
            help.setDescription("This message gives you a helpful commands for the Bot");
            help.addField("```/addResponse```","This is how you can add new response",false);
            help.addField("```!listResponses```", "This command will show you all the responses",false);
            help.setTimestamp(Instant.now());

            if (content.equalsIgnoreCase("!help")){
                MessageChannel channel = event.getChannel();
                channel.sendMessageEmbeds(help.build()).queue();
            }
        }
    }
    public static class giveResponse extends ListenerAdapter{
        @Override
        public void onMessageReceived(MessageReceivedEvent event){
            //Won't respond to bot
            if (event.getAuthor().isBot()) return;
            Message msg = event.getMessage();
            String content = msg.getContentRaw();
            Responses responses = new Responses();
            String[] addedCommands = {"!help","!gp"};
            String response;

            if (!content.equalsIgnoreCase(addedCommands[0])){
                response = responses.searchResponse(content,true);
                MessageChannel channel = event.getChannel();
                if(response != null){
                    channel.sendMessage(response).queue();
                }else {
                    channel.sendMessage("Your response is not in the Database.").queue();
                }
            }
        }
    }
}


