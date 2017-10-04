import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Driver;

public class Modeltoworld
{
    public static void main(String[] args)
    {
        String filename = args[0].toLowerCase();
        String dirPath = "";

        try {
            dirPath = createDriverFolder(filename.replace(".txt",""));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DriverFactory.BuildAllFromDriverFile(filename);
        DriverFactory.SaveAllLoadedModelsAsOBJInDirectory(dirPath);
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
