package dev.bk201.RavenBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

//TODO Add HTTP webserver for the bot, see https://github.com/NanoHttpd/nanohttpd
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

    public static void main(String[] args) throws LoginException {

        JDA client = JDABuilder.createDefault(token).build();

        System.out.println("Logged in as: " + client.getSelfUser().getAsTag());
        client.addEventListener(new helloCommand());
        client.addEventListener(new helpCommand());
        client.addEventListener(new addCommandHelp());
        client.addEventListener(new giveResponse());
        client.upsertCommand("addResponse","This command adds new Response to the bot").queue();
    }

    public static class helloCommand extends ListenerAdapter{
        @Override
        public void onMessageReceived(MessageReceivedEvent event){
            //Won't respond to bot
            if (event.getAuthor().isBot()) return;
            Message msg = event.getMessage();
            String content = msg.getContentRaw();

            if (content.equalsIgnoreCase("!hi")){
                MessageChannel channel = event.getChannel();
                channel.sendMessage("Hello.").queue();
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
            help.addField("```!addResponse !example,your message or link```","This is how you can add new response",false);
            help.addField("```!listResponses```", "This command will show you all the responses",false);
            help.setTimestamp(Instant.now());

            if (content.equalsIgnoreCase("!help")){
                MessageChannel channel = event.getChannel();
                channel.sendMessageEmbeds(help.build()).queue();
            }
        }
    }
    public static class addCommandHelp extends ListenerAdapter{
        @Override
        public void onMessageReceived(MessageReceivedEvent event){
            //Won't respond to bot
            if (event.getAuthor().isBot()) return;
            Message msg = event.getMessage();
            String content = msg.getContentRaw();

            if (content.equals("!addResponse")){
                MessageChannel channel = event.getChannel();
                channel.sendMessage("Type !help too see how you can add a Response.").queue();
            }
        }
    }


//    public static class addCommand extends ListenerAdapter{
//        @Override
//        public void onMessageReceived(MessageReceivedEvent event){
//            //Won't respond to bot
//            if (event.getAuthor().isBot()) return;
//
//            Message msg = event.getMessage();
//            String content = msg.getContentRaw();
//            Responses responses = new Responses();
//            String[] message;
//
//            if (content.contains("!addResponse")){
//                message = content.split(",");
//                if (responses.checkForDuplicate(message[1],true)){
//                    MessageChannel channel = event.getChannel();
//                    channel.sendMessage("Your response is already in the Database.").queue();
//                }else {
//                    responses.insertResponse(message[1],message[2],true);
//                    MessageChannel channel = event.getChannel();
//                    channel.sendMessage("Your response has been added.").queue();
//                }
//            }
//        }
//    }
    public static class giveResponse extends ListenerAdapter{
        @Override
        public void onMessageReceived(MessageReceivedEvent event){
            //Won't respond to bot
            if (event.getAuthor().isBot()) return;
            Message msg = event.getMessage();
            String content = msg.getContentRaw();
            Responses responses = new Responses();
            String response;

            if (content.equalsIgnoreCase("!help")){
                System.out.println("Here i am");
            }else {
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


