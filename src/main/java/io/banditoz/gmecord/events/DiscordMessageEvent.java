package io.banditoz.gmecord.events;

import io.banditoz.gmecord.*;
import io.banditoz.gmecord.api.Attachment;
import io.banditoz.gmecord.paste.Paste;
import io.banditoz.gmecord.paste.PasteggUploader;
import io.banditoz.gmecord.util.BuildAttachments;
import io.banditoz.gmecord.util.EmbedFormatter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class DiscordMessageEvent extends ListenerAdapter {
    private final Logger logger;
    public DiscordMessageEvent() {
        logger = LoggerFactory.getLogger(DiscordMessageEvent.class);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        // I'm honestly surprised this method works...
        StringBuilder message = new StringBuilder(e.getMessage().getContentDisplay());
        ArrayList<Attachment> attachments = new ArrayList<>();
        if (e.getChannel().getId().equals(SettingsManager.getInstance().getSettings().getChannel()) &&
                (e.getAuthor().getId().compareToIgnoreCase(Bot.getJda().getSelfUser().getId()) != 0)) {
            try {
                attachments = BuildAttachments.buildImageAttachments(e);
                if (attachments.size() > 1) {
                    DiscordMessageCreator creator = new DiscordMessageCreator("<@" + e.getAuthor().getId() + ">, due to " +
                            "a bug in Groupme, only your first image was sent.", true);
                    DiscordMessageSender.sendMessageToDiscord(creator.getMessage());
                }
                message.append(BuildAttachments.buildOtherAttachments(e).toString());
            } catch (Exception ex) {
                logger.error("Exception on building attachments!", ex);
                DiscordMessageCreator creator = new DiscordMessageCreator("<@" + e.getAuthor().getId() + ">, " +
                        "there was an error while building the attachments." +
                        " Exception: `" + ex + "`", true);
                DiscordMessageSender.sendMessageToDiscord(creator.getMessage());
            }
            if (!e.getMessage().getEmbeds().isEmpty()) {
                for (MessageEmbed embed : e.getMessage().getEmbeds()) {
                    message.append("\n<EMBEDDED MESSAGE>\n");
                    message.append(EmbedFormatter.formatEmbed(embed));
                }
            }
            if (message.length() > 965) {
                messageIsTooLong(e, message.toString());
            }
            else {
                String nameTemp = e.getMember().getNickname();
                if (nameTemp == null){
                    nameTemp = e.getAuthor().getName();
                }
                GroupmeMessageCreator gmeMessage = new GroupmeMessageCreator("<" + nameTemp + "> " + message, false, attachments, e.getMember());
                GroupmeMessageSender.sendMessageToGroupMe(gmeMessage.getMessage());
            }
        }
    }

    private void messageIsTooLong(MessageReceivedEvent e, String message) {
        try {
            String nameTemp = e.getMember().getNickname();
            if (nameTemp == null){
                nameTemp = e.getAuthor().getName();
            }
            Paste paste = new Paste("<" + nameTemp + "> " + message);
            String url = new PasteggUploader(paste).uploadToPastegg();
            GroupmeMessageCreator sysMessage = new GroupmeMessageCreator("Message from user " + nameTemp + " is too long! Paste.gg link: " + url, true, e.getMember());
            GroupmeMessageSender.sendMessageToGroupMe(sysMessage.getMessage());
        } catch (Exception ex) {
            logger.error("Exception thrown! Letting Discord know their message will not be delivered...", ex);
            DiscordMessageCreator creator = new DiscordMessageCreator("<@" + e.getAuthor().getId() + ">, your message " +
                    "was greater than 965 characters, and paste.gg is not working! Therefore, your message" +
                    " will not be delivered over the bridge. Exception: `" + ex + "`", true);
            DiscordMessageSender.sendMessageToDiscord(creator.getMessage());
        }
    }
}
