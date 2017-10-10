import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PPMSaver
{
    public static void SavePPMToFile(String filename, int[][] intImage)
    {
        if(!filename.endsWith(".ppm")) filename += ".ppm";
        System.out.println(filename);
        File file = new File(filename);
        if(file.isFile() && file.exists())
        {
            file.delete();
        }

        try
        {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.append("P3\n");
            bw.append(intImage.length + " " + intImage[0].length/3 + " 255\n");

            for(int row = 0; row < intImage.length; ++row)
            {
                StringBuilder sbrow = new StringBuilder(4*4*4*intImage[0].length+4); //4 per pixel times number of pixels in line, plus 4 for whitespace
                for(int col = 0; col < intImage[0].length; col+=3)
                {
                    int r = intImage[row][col];
                    int g = intImage[row][col+1];
                    int b = intImage[row][col+2];

                    sbrow.append(r).append(" ").append(g).append(" ").append(b).append("\t");
                }
                sbrow.append("\n");
                bw.append(sbrow);
            }
            bw.close();
            fw.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
