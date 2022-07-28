package codes.towel.RCCAssistance;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class ResponseEmbedBuilder {

    public static EmbedBuilder responseEmbedBuilder() {
        return new EmbedBuilder()
                .setAuthor("RCC Assistance",
                        "https://cdn.discordapp.com/icons/757663988879458324/7b9f1bb9148d1884ad9ad61f80f460ab.webp");
    }

    public static EmbedBuilder successResponseEmbedBuilder() {
        return responseEmbedBuilder()
                .setTitle("Success")
                .setDescription("The operation was successful.")
                .setColor(Color.GREEN);
    }

    public static EmbedBuilder errorResponseEmbedBuilder() {
        return responseEmbedBuilder()
                .setTitle("Error")
                .setDescription("The operation was not able to complete successfully.\n`No further information provided.`")
                .setColor(Color.ORANGE);
    }

    public static EmbedBuilder permErrorResponseEmbedBuilder() {
        return responseEmbedBuilder()
                .setTitle("Access Denied")
                .setDescription("You don't have permission to run that.");
    }

}
