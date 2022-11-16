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
import java.time.Instant;
import java.util.*;
import java.util.List;

//TODO Add HTTP webserver for the bot, see https://github.com/NanoHttpd/nanohttpd
//TODO add command !gp, taking random link for gp video
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
        client.upsertCommand("addresponse", "This command adds new Response to the bot")
                .addOption(OptionType.STRING, "key", "Here type the command")
                .addOption(OptionType.STRING, "value", "Give value to your response").queue();

        client.addEventListener(new addResponseCommand());
        client.addEventListener(new listResponsesCommand());
        client.addEventListener(new embedButtonsClick());
        client.addEventListener(new editResponse());
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
            if (!event.getName().equals("addresponse")) return;
            msgKey = event.getOption("key").getAsString();
            msgValue = event.getOption("value").getAsString();
            String userID = event.getInteraction().getUser().getAsTag();
            boolean contains = Arrays.stream(addedCommands).anyMatch(msgKey::equals);

            if (responses != null) {
                if (msgValue == null) {
                    event.reply("You can't have empty value").setEphemeral(true).queue();
                } else if (msgKey.isEmpty()) {
                    event.reply("You can't have empty key.").setEphemeral(true).queue();
                } else {
                    if (responses.checkForDuplicate(msgKey, true)) {
                        event.reply("Your response is already in the Database.").setEphemeral(true).queue();
                    } else {
                        if (contains) {
                            event.reply("You can't add that response.").setEphemeral(true).queue();
                        } else {
                            responses.insertResponse(msgKey, msgValue, true);
                            event.reply("Your response with key '" + msgKey + "' has been added.").setEphemeral(true).queue();
                        }
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
            help.addField("```/addResponse```", "This is how you can add new response", false);
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

            // Building the Embed Message
            EmbedBuilder pagination = new EmbedBuilder();
            pagination.setTitle("Raven Responses");
            pagination.setTimestamp(Instant.now());
            pagination.setFooter("Page 1");
            pagination.setColor(0x039108);
            List<Button> buttons = new ArrayList<Button>();
//            buttons.add(Button.primary("page_1", Emoji.fromUnicode("⏪")));
//            buttons.add(Button.primary("page_2", Emoji.fromUnicode("◀")));
//            buttons.add(Button.primary("page_3", Emoji.fromUnicode("▶")));
//            buttons.add(Button.primary("page_4", Emoji.fromUnicode("⏩")));
            buttons.add(Button.primary("page_1", Emoji.fromUnicode("U+30U+FE0FU+20E3")));
            buttons.add(Button.primary("page_2", Emoji.fromUnicode("U+31U+FE0FU+20E3")));
            buttons.add(Button.primary("page_3", Emoji.fromUnicode("U+32U+FE0FU+20E3")));
            buttons.add(Button.primary("page_4", Emoji.fromUnicode("U+33U+FE0FU+20E3")));

            // Getting all the responses and Adding the Responses into the Message
            for (int i = 0; i < 30; i++) {
                allResponses.append(responses.giveAllResponses(true).get(i) + "\n");
            }

            pagination.setDescription(allResponses);

            if (content.equals("!listResponses")) {
                MessageChannel channel = event.getChannel();
                channel.sendMessageEmbeds(pagination.build()).setActionRow(buttons).queue();
            }
        }
    }

    public static class embedButtonsClick extends ListenerAdapter {
        @Override
        public void onButtonInteraction(ButtonInteractionEvent event) {
            // Split the ID to 2 Strings
            // page_1 = page | 1
            String[] args = event.getButton().getId().split("_");

            // Check if button is a page button
            if (args[0].equalsIgnoreCase("page")) {
                int pageNum = Integer.valueOf(args[1]);

                // Building the Embed Message
                EmbedBuilder pagination = new EmbedBuilder();
                List<Button> buttons = new ArrayList<Button>();
                StringBuilder allResponses = new StringBuilder();
                Responses responses = new Responses();

                switch (pageNum) {
                    case 1:
                        buttons.add(Button.primary("page_1", Emoji.fromUnicode("U+30U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_2", Emoji.fromUnicode("U+31U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_3", Emoji.fromUnicode("U+32U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_4", Emoji.fromUnicode("U+33U+FE0FU+20E3")));
                        pagination.setTitle("Raven Responses");
                        pagination.setColor(0x039108);
                        pagination.setFooter("Page 1");
                        pagination.setTimestamp(Instant.now());

                        for (int i = 0; i < 40; i++) {
                            allResponses.append(responses.giveAllResponses(true).get(i) + "\n");
                        }
                        pagination.setDescription(allResponses);
                        break;

                    case 2:
                        buttons.add(Button.primary("page_1", Emoji.fromUnicode("U+30U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_2", Emoji.fromUnicode("U+31U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_3", Emoji.fromUnicode("U+32U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_4", Emoji.fromUnicode("U+33U+FE0FU+20E3")));
                        pagination.setTitle("Raven Responses");
                        pagination.setColor(0x039108);
                        pagination.setFooter("Page 2");
                        pagination.setTimestamp(Instant.now());

                        for (int i = 40; i < responses.giveAllResponses(true).size(); i++) {
                            allResponses.append(responses.giveAllResponses(true).get(i) + "\n");
                        }
                        pagination.setDescription(allResponses);
                        break;

                    case 3:
                        buttons.add(Button.primary("page_1", Emoji.fromUnicode("U+30U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_2", Emoji.fromUnicode("U+31U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_3", Emoji.fromUnicode("U+32U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_4", Emoji.fromUnicode("U+33U+FE0FU+20E3")));
                        pagination.setTitle("Raven Responses");
                        pagination.setColor(0x039108);
                        pagination.setFooter("Page 3");
                        pagination.setTimestamp(Instant.now());

                        for (int i = 60; i < responses.giveAllResponses(true).size(); i++) {
                            allResponses.append(responses.giveAllResponses(true).get(i) + "\n");
                        }
                        pagination.setDescription(allResponses);
                        break;

                    case 4:
                        buttons.add(Button.primary("page_1", Emoji.fromUnicode("U+30U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_2", Emoji.fromUnicode("U+31U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_3", Emoji.fromUnicode("U+32U+FE0FU+20E3")));
                        buttons.add(Button.primary("page_4", Emoji.fromUnicode("U+33U+FE0FU+20E3")));
                        pagination.setTitle("Raven Responses");
                        pagination.setColor(0x039108);
                        pagination.setFooter("Page 4");
                        pagination.setTimestamp(Instant.now());

                        for (int i = 80; i < responses.giveAllResponses(true).size(); i++) {
                            allResponses.append(responses.giveAllResponses(true).get(i) + "\n");
                        }
                        pagination.setDescription(allResponses);
                        break;
                }

                //Edit the Message
                event.getMessage().editMessageEmbeds(pagination.build()).setActionRow(buttons).queue();
            }
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
            String response = null;

            for (int i = 0; i < addedCommands.length; i++) {
                if (!content.contains(addedCommands[i])) {
                    response = responses.searchResponse(content, true);
                }
            }
            MessageChannel channel = event.getChannel();
            if (response != null) {
                if (response.length() <= 2000) {
                    channel.sendMessage(response).queue();
                }
            }
        }
    }

    public static class editResponse extends ListenerAdapter {
        @Override
        public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
            if (event.getInteraction().getMember().getUser().isBot()) return;

            String msgKey;
            String msgNewValue;
            Responses responses = new Responses();

            if (!event.getInteraction().getMember().getUser().equals("")){
                event.reply("You don't have Permissions to edit Responses");
            }
        }
    }
}


