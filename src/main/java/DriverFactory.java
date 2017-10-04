import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DriverFactory
{
    private static ModelMap mappedModels = new ModelMap();

    public static void BuildAllFromDriverFile(String filename)
    {
        try
        {
            FileReader fr = new FileReader(filename);
            BufferedReader buffer = new BufferedReader(fr);

            String line;

            while((line = buffer.readLine()) != null)
            {
                BuildObjectFromLine(line);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void BuildObjectFromLine(String line)
    {
        String[] splitLine = line.split(" ");
        String objectType = splitLine[0].toLowerCase();
        String objectName = splitLine[splitLine.length-1].replace(".obj","");
        switch(objectType)
        {
            case "model":
                Model model = new Model(objectName,line);
                model.transformAllVertices();
                mappedModels.addModel(model);
                break;
            default:
                System.out.println("[WARNING]: Unable to find object type \'" + objectType + "\'");
        }
    }

    public static ArrayList<Model> getAllLoadedModels()
    {
        return mappedModels.getAllLoadedModels();
    }

    public static void SaveAllLoadedModelsAsOBJInDirectory(String dirPath)
    {
        for(Model m : DriverFactory.getAllLoadedModels())
        {
            m.saveAsObj(dirPath);
        }
    }
}
