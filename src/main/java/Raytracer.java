import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Driver;

public class Raytracer
{
    public static void main(String[] args)
    {
        String inFilename = args[0].toLowerCase();
        String outFilename = args[1].toLowerCase().replaceAll("(\\\\/)",File.separator);

        Modeltoworld.main(args);

        Model m1 = DriverFactory.getAllLoadedModels().get(0);

        DriverFactory.getMainCamera().build();
        Camera mainc = DriverFactory.getMainCamera();
        mainc.castAllRays();

        mainc.SaveToPPM(outFilename);
    }

    public static String createDriverFolder(String folderName) throws IOException {
        File file = new File(folderName);
        if(file.isDirectory() && file.exists())
        {
            file.delete();
        }
        file.mkdir();
        return file.getCanonicalPath();
    }
}
