
package predominantcolor;

/**
 *
 * @author larryataylor
 * Portions of this file were copied from source
 * material associated with Horstmann, Java Concepts, 8th ed.
 */

import java.io.File;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * PexPicture -- picture class for Pex challenge
 * @author larryataylor
 */
public class PexPicture 
{
   private String source;
   private BufferedImage image;

   private static final int SIXTEEN_MEGABYTES = 0x01000000;
   private static final int RGB_MASK = 0X00FFFFFF; // drops off alpha channel
   
   public PexPicture()
   {
       // initialization, if any
   }
   
   /**
      Loads a picture from a given source. 
      @param source the image source. If the source starts
      with http://, it is a URL, otherwise, a filename.
   */ 
   public void load(String source)
   {
      try 
      {
         this.source = source;
         BufferedImage img;
         if (source.startsWith("http://") 
                 || source.startsWith("https://"))
         {
             try (InputStream urlStream = new URL(source).openStream())
             { 
                img = ImageIO.read(urlStream);
             }
         }
         else
            img = ImageIO.read(new File(source));

        this.image = img;
      }
      catch (Exception ex)
      {
         this.source = null;
         ex.printStackTrace();
      }
   }
   
   /**
    * get predominant color
    * @return integer with the RGB components of the most numerous color
    * side effect
    *    sets image and source to null to mark objects for garbage collection
    */
   public int getPredominantColor()
   {
       // find the rgb values of the mode (most numerous color)
       int ans = 0;
       int imageWidth = image.getWidth();
       int imageHeight = image.getHeight();
       int [] countRGBTable = new int [SIXTEEN_MEGABYTES];
       for (int y = 0; y < imageHeight; y++)
       {
           for (int x = 0; x < imageWidth; x++)
           {
               int argb = image.getRGB(x, y);
               argb = argb & RGB_MASK;
               countRGBTable[argb]++; // count one rgb
           }
       }
       
       // now all pixels are counted
       // find the highest
       int highCount = 0;
       for (int i = 0; i < SIXTEEN_MEGABYTES; i++)
       {
           if (highCount < countRGBTable[i])
           {
               highCount = countRGBTable[i];
               ans = i;
           }
       }
       
       //image.flush(); // reclaims some resources
       this.image = null;
       this.source = null;
       return ans;
   }
}
