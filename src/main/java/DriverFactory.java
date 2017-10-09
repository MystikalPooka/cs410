import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DriverFactory
{
    private static ModelMap mappedModels = new ModelMap();
    private static Camera MainCamera = new Camera();

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

        switch(objectType)
        {
            case "eye":
                double[] eye = {Double.parseDouble(splitLine[1]),
                                Double.parseDouble(splitLine[2]),
                                Double.parseDouble(splitLine[3])};
                MainCamera.setEye(eye);
                break;
            case "look":
                double[] look = {Double.parseDouble(splitLine[1]),
                                 Double.parseDouble(splitLine[2]),
                                 Double.parseDouble(splitLine[3])};
                MainCamera.setLookPoint(look);
                break;
            case "up":
                double[] up = {Double.parseDouble(splitLine[1]),
                               Double.parseDouble(splitLine[2]),
                               Double.parseDouble(splitLine[3])};
                MainCamera.setUpDirection(up);
                break;
            case "d":
                MainCamera.setFocalLength(Double.parseDouble(splitLine[1]));
                break;
            case "bounds":
                double[] bounds = {Double.parseDouble(splitLine[1]),
                                   Double.parseDouble(splitLine[2]),
                                   Double.parseDouble(splitLine[3]),
                                   Double.parseDouble(splitLine[4])};
                MainCamera.setLBRTBounds(bounds);
                break;
            case "res":
                int[] res = {Integer.parseInt(splitLine[1]),
                                Integer.parseInt(splitLine[2])};
                MainCamera.setResolution(res);
                break;
            case "sphere":
                //do sphere things
                break;
            case "model":
                String objectName = splitLine[splitLine.length-1].replace(".obj","");
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

    static Camera getMainCamera()
    {
        return MainCamera;
    }
}
