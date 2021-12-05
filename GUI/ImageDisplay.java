package GUI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ImageDisplay {
    private static final int width = 352, height = 288;
    public static BufferedImage GetImage(File imgFile) {
        try {
            BufferedImage outputImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            RandomAccessFile raf = new RandomAccessFile(imgFile, "r");
            raf.seek(0);

            long len = width*height*3;
            byte[] bytes = new byte[(int) len];

            raf.read(bytes);

            int ind = 0;
            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    byte r = bytes[ind];
                    byte g = bytes[ind+height*width];
                    byte b = bytes[ind+height*width*2];

                    int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    outputImg.setRGB(x,y,pix);
                    ind++;
                }
            }

            return outputImg;
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
