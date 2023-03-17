package dev.bk201.RavenBot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.List;

//TODO Add HTTP webserver for the bot, see https://github.com/NanoHttpd/nanohttpd
//TODO add command !gp, taking random link for gp video
//TODO give roles to people
//TODO implement a backup for the db

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
        client.upsertCommand("add_response", "This command adds new Response to the bot")
                .addOption(OptionType.STRING, "key", "Here type the command")
                .addOption(OptionType.STRING, "value", "Give value to your response").queue();

        client.addEventListener(new addResponseCommand());
        client.addEventListener(new listResponsesCommand());
        client.addEventListener(new embedButtonsClick());
        client.upsertCommand("edit_response", "This command edit an existing Response")
                .addOption(OptionType.STRING, "key", "The name of the existing Key")
                .addOption(OptionType.STRING, "value", "The new value for the key").queue();
        client.addEventListener(new editResponse());
        client.upsertCommand("delete_response", "This command deletes and existing Response")
                .addOption(OptionType.STRING, "key", "the name of the existing key").queue();
        client.addEventListener(new deleteResponse());
    }

    public static class addResponseCommand extends ListenerAdapter {
        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            //Won't respond to bot
            if (event.getInteraction().getMember().getUser().isBot()) return;

            String msgKey;
            String msgValue;
            Responses responses = new Responses();
            String[] addedCommands = {"!help", "!gp", "!listResponses", "bk", "bк","бk"};

            //making sure we are handling the right command
            if (!event.getName().equals("add_response")) return;
            msgKey = event.getOption("key").getAsString();
            msgValue = event.getOption("value").getAsString();
            String userID = event.getInteraction().getUser().getAsTag();
            boolean contains = Arrays.stream(addedCommands).anyMatch(msgKey::equals);


            if(msgValue.isEmpty()){
                event.reply("You can't have empty value").setEphemeral(true).queue();
            } else if (msgKey.isEmpty()) {
                event.reply("You can't have empty key").setEphemeral(true).queue();
            }else {
                if (responses.checkForDuplicateSQL(msgKey)){
                    event.reply("Your response is already in the Database.").setEphemeral(true).queue();
                }else {
                    if (contains){
                        event.reply("You can't add that response.").setEphemeral(true).queue();
                    } else {
                        responses.insertResponseSQl(msgKey,msgValue,userID);
                        event.reply("Your response with key '" + msgKey + "' has been added.").setEphemeral(true).queue();
                    }
                }
            }
        }
    }

    public Map<Integer, String> blacklist(List<String> user) {
        Map<Integer, String> innerBlacklist = new HashMap<Integer, String>();
        int index = 1;
        for (String item : user) {
            innerBlacklist.put(index, item);
            index++;
        }
        return innerBlacklist;
    }

    public static class helpCommand extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            //Won't respond to bot
            if (event.getAuthor().isBot()) return;
            Message msg = event.getMessage();
            String content = msg.getContentRaw();
            EmbedBuilder help = new EmbedBuilder();

            help.setColor(Color.BLACK);
            help.setTitle("Help!");
            help.setDescription("This message gives you a helpful commands for the Bot");
            help.addField("```/add_Response```", "This is how you can add new response", false);
            help.addField("```!<your Response>```", "Like this you can receive your command", false);
            help.addField("```!listResponses```", "This command will show you all the responses", false);
            help.setTimestamp(Instant.now());

            if (content.equalsIgnoreCase("!help")) {
                MessageChannel channel = event.getChannel();
                channel.sendMessageEmbeds(help.build()).queue();
            }
        }
    }

    public static class listResponsesCommand extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            //Won't respond to bot
            if (event.getAuthor().isBot()) return;

            Message msg = event.getMessage();
            String content = msg.getContentRaw();
            StringBuilder allResponses = new StringBuilder();
            Responses responses = new Responses();
            List<String> responsesList;
            List<String> onlyNresponses;
            List<Integer> listHelpers = responses.getAllHelpers();
            int pageNumber;

            if (content.equals("!listResponses")) {
                responses.editHelpers(1,0,25);
                pageNumber = listHelpers.get(0);

                // Building the Embed Message
                EmbedBuilder pagination = new EmbedBuilder();
                pagination.setTitle("Raven Responses");
                pagination.setTimestamp(Instant.now());
                pagination.setFooter("Page " + pageNumber);
                pagination.setColor(0x039108);
                List<Button> buttons = new ArrayList<Button>();
                buttons.add(Button.primary("first_page", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("previous_page", Emoji.fromUnicode("◀")));
                buttons.add(Button.primary("next_page", Emoji.fromUnicode("▶")));
                buttons.add(Button.primary("last_page", Emoji.fromUnicode("⏩")));

                // Getting all the responses and Adding the Responses into the Message
                System.out.println(listHelpers.get(1));
                System.out.println(listHelpers.get(2));
                responsesList = responses.giveAllResponses();
                onlyNresponses = responsesList.subList(listHelpers.get(1),listHelpers.get(2));


                for (int i = 0; i < onlyNresponses.size(); i++) {
                    int indexN = responsesList.indexOf(onlyNresponses.get(i));
                    allResponses.append(indexN + ": " + onlyNresponses.get(i) + "\n");
                }

                pagination.setDescription(allResponses);

                MessageChannel channel = event.getChannel();
                channel.sendMessageEmbeds(pagination.build()).setActionRow(buttons).queue();
            }
        }
    }

    public static class embedButtonsClick extends ListenerAdapter {
        @Override
        public void onButtonInteraction(ButtonInteractionEvent event) {
            // Split the ID to 2 Strings -> first_page = first | page
            String[] args = event.getButton().getId().split("_");

            // Check if button is a page buttonlistHelpers.get(0);
            if (args[1].equalsIgnoreCase("page")) {
                String buttonCommand = args[0];

                // Building the Embed Message
                EmbedBuilder pagination = new EmbedBuilder();
                List<Button> buttons = new ArrayList<>();
                StringBuilder allResponses = new StringBuilder();
                Responses responses = new Responses();
                List<String> responsesList;
                List<String> onlyNresponses;
                List<Integer> listHelpers = responses.getAllHelpers();
                responsesList = responses.giveAllResponses();
                int pageNumber;
                int firstIndex;
                int secondIndex;

                switch (buttonCommand) {
                    case "first":
                        responses.editHelpers(1,0,25);
                        pageNumber = listHelpers.get(0);

                        buttons.add(Button.primary("first_page", Emoji.fromUnicode("⏪")));
                        buttons.add(Button.primary("previous_page", Emoji.fromUnicode("◀")));
                        buttons.add(Button.primary("next_page", Emoji.fromUnicode("▶")));
                        buttons.add(Button.primary("last_page", Emoji.fromUnicode("⏩")));
                        pagination.setTitle("Raven Responses");
                        pagination.setColor(0x039108);
                        pagination.setFooter("Page " + pageNumber);
                        pagination.setTimestamp(Instant.now());

                        onlyNresponses = responsesList.subList(listHelpers.get(1),listHelpers.get(2));

                        for (int i = 0; i < onlyNresponses.size(); i++) {
                            int indexN = responsesList.indexOf(onlyNresponses.get(i));
                            allResponses.append(indexN + ": " + onlyNresponses.get(i) + "\n");
                        }
                        pagination.setDescription(allResponses);
                        break;

                    case "previous":
                        pageNumber = listHelpers.get(0);
                        firstIndex = listHelpers.get(1);
                        secondIndex = listHelpers.get(2);
                        if (firstIndex > 0){
                            responses.editHelpers(pageNumber-1,firstIndex-25,secondIndex-25);
                        }else {
                            if(pageNumber > 1){
                                pageNumber = 1;
                            }
                            responses.editHelpers(pageNumber,firstIndex,secondIndex);
                        }

                        buttons.add(Button.primary("first_page", Emoji.fromUnicode("⏪")));
                        buttons.add(Button.primary("previous_page", Emoji.fromUnicode("◀")));
                        buttons.add(Button.primary("next_page", Emoji.fromUnicode("▶")));
                        buttons.add(Button.primary("last_page", Emoji.fromUnicode("⏩")));
                        pagination.setTitle("Raven Responses");
                        pagination.setColor(0x039108);
                        pagination.setFooter("Page " + pageNumber);
                        pagination.setTimestamp(Instant.now());

                        onlyNresponses = responsesList.subList(firstIndex,secondIndex);
                        for (int i = 0; i < onlyNresponses.size(); i++) {
                            int indexN = responsesList.indexOf(onlyNresponses.get(i));
                            allResponses.append(indexN + ": " + onlyNresponses.get(i) + "\n");
                        }
                        pagination.setDescription(allResponses);
                        break;

                    case "next":
                        pageNumber = listHelpers.get(0);
                        firstIndex = listHelpers.get(1);
                        secondIndex = listHelpers.get(2);
                        int tempIndex;
                        if ((secondIndex+25) < responsesList.size()){
                            responses.editHelpers(pageNumber+1,firstIndex+25,secondIndex+25);
                        } else {
                            if(secondIndex == responsesList.size()){
                                tempIndex = responsesList.size() - firstIndex;
                                responses.editHelpers(pageNumber,secondIndex - tempIndex,secondIndex);
                            }else {
                                tempIndex = responsesList.size() - secondIndex;
                                responses.editHelpers(pageNumber+1,firstIndex,secondIndex + tempIndex);
                            }
                        }

                        pageNumber = listHelpers.get(0);
                        buttons.add(Button.primary("first_page", Emoji.fromUnicode("⏪")));
                        buttons.add(Button.primary("previous_page", Emoji.fromUnicode("◀")));
                        buttons.add(Button.primary("next_page", Emoji.fromUnicode("▶")));
                        buttons.add(Button.primary("last_page", Emoji.fromUnicode("⏩")));
                        pagination.setTitle("Raven Responses");
                        pagination.setColor(0x039108);
                        pagination.setFooter("Page " + pageNumber);
                        pagination.setTimestamp(Instant.now());

                        System.out.println(firstIndex);
                        System.out.println(secondIndex);
                        System.out.println(pageNumber);

                        firstIndex = listHelpers.get(1);
                        secondIndex = listHelpers.get(2);

                        onlyNresponses = responsesList.subList(firstIndex,secondIndex);

                        for (int i = 0; i < onlyNresponses.size(); i++) {
                            int indexN = responsesList.indexOf(onlyNresponses.get(i));
                            allResponses.append(indexN + ": " + onlyNresponses.get(i) + "\n");
                        }
                        pagination.setDescription(allResponses);
                        break;

                    case "last":
                        buttons.add(Button.primary("first_page", Emoji.fromUnicode("⏪")));
                        buttons.add(Button.primary("previous_page", Emoji.fromUnicode("◀")));
                        buttons.add(Button.primary("next_page", Emoji.fromUnicode("▶")));
                        buttons.add(Button.primary("last_page", Emoji.fromUnicode("⏩")));
                        pagination.setTitle("Raven Responses");
                        pagination.setColor(0x039108);
                        pagination.setFooter("Last Page");
                        pagination.setTimestamp(Instant.now());

                        onlyNresponses = responsesList.subList(responsesList.size()-25,responsesList.size());

                        for (int i = 0; i < onlyNresponses.size(); i++) {
                            int indexN = responsesList.indexOf(onlyNresponses.get(i));
                            allResponses.append(indexN + ": " + onlyNresponses.get(i) + "\n");
                        }
                        pagination.setDescription(allResponses);
                        break;
                }

                //Edit the Message
                event.getMessage().editMessageEmbeds(pagination.build()).setActionRow(buttons).queue();
                event.deferEdit().queue();
            }
        }
    }

    public static class lastListIndex {
        private int index;

        public int getIndex(){
            return this.index;
        }

        public void setIndex(int newIndex){
            this.index = newIndex;
        }
    }

    public static class giveResponse extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            //Won't respond to bot
            if (event.getAuthor().isBot()) return;

            Message msg = event.getMessage();
            String content = msg.getContentRaw();
            Responses responses = new Responses();
            String[] addedCommands = {"!help", "!gp", "!listResponses"};
            boolean usingDefaultCommands = false;
            String[] responseAndValue = new String[2];

            for (int i = 0; i < addedCommands.length; i++) {
                if (!content.contains(addedCommands[i])) {
                    usingDefaultCommands = true;
                }
            }

            if (usingDefaultCommands == true){
                responseAndValue[0] = responses.searchResponseSQL(content)[0];
                responseAndValue[1] = responses.searchResponseSQL(content)[1];
            }

            MessageChannel channel = event.getChannel();
            if(responseAndValue[0] != null){
                if (responseAndValue[0].matches(content)){
                    if (responseAndValue[0].length() <= 2000) {
                        channel.sendMessage(responseAndValue[1]).queue();
                    }
                } else {
                    return;
                }
            }else {
                return;
            }
        }
    }

    public static class editResponse extends ListenerAdapter {
        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            if (event.getInteraction().getMember().getUser().isBot()) return;

            String userID = event.getUser().getAsTag();
            String msgKey;
            String msgNewValue;
            Responses responses = new Responses();

            if (event.getName().equals("edit_response")){
                if (!userID.equals("BK201#8111")){
                    event.reply("You don't have Permissions to edit Responses").setEphemeral(true).queue();
                }else {
                    msgKey = event.getOption("key").getAsString();
                    msgNewValue = event.getOption("value").getAsString();
                    try {
                        responses.editResponse(msgKey,msgNewValue);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    event.reply("Value for key: " + msgKey + " has changed to: " + msgNewValue).setEphemeral(true).queue();
                }
            }
        }
    }

    public static class deleteResponse extends ListenerAdapter {
        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            if (event.getInteraction().getMember().getUser().isBot()) return;

            String userID = event.getUser().getAsTag();
            String msgKey;
            Responses responses = new Responses();

            if (event.getName().equals("delete_response")){
                if (!userID.equals("BK201#8111")){
                    event.reply("You don't have Permissions to edit Responses").setEphemeral(true).queue();
                }else {
                    msgKey = event.getOption("key").getAsString();
                    try {
                        responses.deleteResponse(msgKey);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    event.reply("The key: " + msgKey + " has been deleted").setEphemeral(true).queue();
                }
            }
        }
    }
}


