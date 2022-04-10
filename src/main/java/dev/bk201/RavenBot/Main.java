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
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.List;

//TODO Add HTTP webserver for the bot, see https://github.com/NanoHttpd/nanohttpd
//TODO add command !gp, taking random link for gp video
//TODO add a command to list all the responses
//TODO add sqlite so that a response can have more than one value
//TODO give roles to people

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
        client.addEventListener(new listResponsesCommand());
    }

        public static class addResponseCommand extends ListenerAdapter{
        @Override
            public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
            //Won't respond to bot
            if (event.getInteraction().getMember().getUser().isBot()) return;

            String msgKey;
            String msgValue;
            Responses responses = new Responses();
            String[] blackList;

            //making sure we are handling the right command
            if(!event.getName().equals("addresponse")) return;
            msgKey = event.getOption("key").getAsString();
            msgValue = event.getOption("value").getAsString();
            String userID = event.getInteraction().getUser().getAsTag();

             if (responses != null){
                 if (msgValue == null){
                     event.reply("You can't have empty value").setEphemeral(true).queue();
                 }
                 else if (msgKey.isEmpty()){
                     event.reply("You can't have empty key.").setEphemeral(true).queue();
                 }else {
                     if (responses.checkForDuplicate(msgKey,true)){
                         event.reply("Your response is already in the Database.").setEphemeral(true).queue();
                     }else {
                         responses.insertResponse(msgKey,msgValue,true);
                         event.reply("Your response with key '" + msgKey + "' has been added.").setEphemeral(true).queue();
                         try {
                             FileWriter fileWriter = new FileWriter("/opt/DiscordBots/logs.txt");
                             fileWriter.write(userID + " added command: " + msgKey + " at " + Instant.now());
                             fileWriter.close();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                 }
             }
            }
        }

    public Map<Integer, String> blacklist(List<String> user ){
        Map<Integer, String> innerBlacklist = new HashMap<Integer, String>();
        int index = 1;
        for(String item : user){
            innerBlacklist.put(index, item);
                index++;
        }
        return innerBlacklist;
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
            help.addField("```!<your Response>```", "Like this you can receive your command",false);
            help.addField("```!listResponses```", "This command will show you all the responses",false);
            help.setTimestamp(Instant.now());

            if (content.equalsIgnoreCase("!help")){
                MessageChannel channel = event.getChannel();
                channel.sendMessageEmbeds(help.build()).queue();
            }
        }
    }

    public static class listResponsesCommand extends ListenerAdapter{
        @Override
        public void onMessageReceived(MessageReceivedEvent event){
            //Won't respond to bot
            if (event.getAuthor().isBot()) return;
            Message msg = event.getMessage();
            String content = msg.getContentRaw();
            StringBuilder allResponses = new StringBuilder("Responses: ");
            Responses responses = new Responses();

            if (content.equals("!listResponses")){
                 for(int i = 0; i < responses.giveAllResponses(true).size(); i++){
                    allResponses.append(responses.giveAllResponses(true).get(i) + " , ");
                 }
                MessageChannel channel = event.getChannel();
                channel.sendMessage("```" + allResponses + "```").queue();
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
            String[] addedCommands = {"!help","!gp","!listResponses"};
            String response = null;

            for (int i = 0; i < addedCommands.length; i++){
                if(!content.equalsIgnoreCase(addedCommands[i])){
                    response = responses.searchResponse(content,true);
                }
            }
            MessageChannel channel = event.getChannel();
            if(response != null){
                channel.sendMessage(response).queue();
            }
        }
    }
}


