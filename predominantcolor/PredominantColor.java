
package predominantcolor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * FindColorThread
 *   worker thread to work on one URL
 * @author larryataylor
 * portions of this file were copied or adapted from source code
 * associated with Horstmann, Java Concepts, 8th ed.
 */

class FindColorThread implements Runnable
{
    String myUrl = "";
    PrintWriter output;
    
    public FindColorThread(String inUrl, final PrintWriter output)
    {
        this.myUrl = inUrl;
        this.output = output;
    }
    
    /**
     * run
     * required by Runnable interface
     * side effects
     *    URL request
     *    writes to output stream 
     */
    @Override
    public void run()
    {
        //
        PexPicture pic = new PexPicture();
        pic.load(this.myUrl);

        int intRGB = pic.getPredominantColor();
        pic = null; // 

        int xBlue = intRGB % 256; // mask
        intRGB = intRGB / 256; // shift
        int xGreen = intRGB % 256; // mask
        intRGB = intRGB / 256; // shift
        int xRed = intRGB % 256; // not really necessary, but consistent

        synchronized (output) {
            output.printf("%s,%02X,%02X,%02X%n",
                    this.myUrl, xRed, xGreen, xBlue);
            output.flush();
        }

        System.gc(); // garbage collection suggestion
         
    }
    
}

/**
 * 
 * @author larryataylor
 */
public class PredominantColor {

    static final String inputFileName = "urls (3).txt";
    static final String outputFileName = "Pex Color Challenge.csv";
    static final int NUM_THREADS = 3;
    
    /**
     * Find the predominant color for each image in a list of URLs.
     * @param args the command line arguments
     */
    public static void main(String[] args)
            throws FileNotFoundException, IOException, InterruptedException 
    {
        // some code adapted from
        // https://codelatte.wordpress.com/2013/11/09/a-simple-newfixedthreadpool-example/

        // Create a fixed thread pool containing NUM_THREADS thread
        ExecutorService fixedPool 
                = Executors.newFixedThreadPool(NUM_THREADS);

        // process one URL at a time from input file
        //    and submit to thread pool
        File inputFile = new File(inputFileName);
        PrintWriter output = new PrintWriter(outputFileName);
        try (Scanner input = new Scanner(inputFile);) 
        {
            while (input.hasNextLine()) {
                String currentURL = input.nextLine();

                FindColorThread findColorThread
                        = new FindColorThread(currentURL, output);
                Future<?> runnableFuture
                        = fixedPool.submit(findColorThread);
                // toss another shrimp on the barbie

            } // has next line

        } // try with resources

        fixedPool.shutdown(); // all submitted threads
        final boolean terminated 
                = fixedPool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        if (!terminated) 
        {
            throw new IllegalStateException("pool shutdown timeout");
        }

        output.close();
    }
}
