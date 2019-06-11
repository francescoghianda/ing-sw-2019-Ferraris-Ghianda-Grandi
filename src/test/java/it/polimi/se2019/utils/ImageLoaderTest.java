package it.polimi.se2019.utils;

import javafx.embed.swing.SwingFXUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoaderTest
{

    @Test
    public void test()
    {
        File file = new File("test.png");
        RenderedImage renderedImage = SwingFXUtils.fromFXImage(ImageLoader.loadImage(null), null);
        try
        {
            ImageIO.write(renderedImage, "png", file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
