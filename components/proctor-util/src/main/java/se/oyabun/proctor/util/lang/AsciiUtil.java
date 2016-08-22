package se.oyabun.proctor.util.lang;

import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageWriteParam;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Proctor ASCII Utility class
 */
public class AsciiUtil {

    private static final int HORIZONTAL_PADDING = 1;
    private static final int VERTICAL_PADDING = 1;

    private static final int ALL_COLORS = -16777216;

    private static final String EMPTY_CHARACTER = " ";
    private static final String FILL_CHARACTER = "X";

    private static final int FONT_SIZE = 24;

    private AsciiUtil() {}

    /**
     * Generates epic ASCII text on non-headless environments, otherwise, just returns content as string.
     * @param content to render ascii art text for
     * @return content for headless or ascii artified random font content for heads
     */
    public static String generateAsciiArtText(final String content) {



        final int width = HORIZONTAL_PADDING + content.length() * FONT_SIZE + HORIZONTAL_PADDING;
        final int height = VERTICAL_PADDING + FONT_SIZE + VERTICAL_PADDING;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        final Graphics g = image.getGraphics();

        String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        g.setFont(new Font(fontFamilyNames[new Random().nextInt(fontFamilyNames.length-1)], Font.BOLD, FONT_SIZE));

        Graphics2D graphics = (Graphics2D) g;

        graphics.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        graphics.drawString(content, HORIZONTAL_PADDING, VERTICAL_PADDING + FONT_SIZE);

        StringBuilder asciiContent = new StringBuilder();

        for(int y = 0; y < height; y++) {

            StringBuilder rowBuilder = new StringBuilder();

            for (int x = 0; x < width; x++) {

                rowBuilder.append(
                        image.getRGB(x, y) == ALL_COLORS ?
                                EMPTY_CHARACTER :
                                FILL_CHARACTER);

            }

            final String row = StringUtils.stripEnd(rowBuilder.toString(), null);

            if(!StringUtils.containsOnly(EMPTY_CHARACTER)) {

                asciiContent.append(row + "\n");

            }


        }

        return asciiContent.toString();

    }


}
