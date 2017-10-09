import java.util.*;

public class ModelMap
{
    //<Type,AllModels
    //"Cube",{"cube_mw00","cube_mw01"}
    private static Map<String,LinkedList<Model>> models = new HashMap();
    private static final String suffix = "_mw";
    private static final int padSize = 2;

    public String addModel(Model m)
    {
        String baseName = m.getName();
        if(models.containsKey(baseName))
        {
            renameToMatchConvention(m);
            models.get(baseName).add(m);
        }
        else
        {
            LinkedList<Model> newList = new LinkedList<>();
            newList.add(renameToMatchConvention(m));
            models.put(baseName,newList);
        }
        return m.getName();
    }

    public ArrayList<Model> getAllLoadedModels()
    {

        ArrayList<Model> list = new ArrayList<>();
        for(String key : models.keySet())
        {
            list.addAll(models.get(key));
        }

        return list;
    }

    private Model renameToMatchConvention(Model m)
    {
        String baseName = m.getName();
        m.setName(baseName + suffix + getNamedIndexOfModel(m));
        return m;
    }

    private String getNamedIndexOfModel(Model m)
    {
        String baseName = m.getName();
        if(models.containsKey(baseName))
        {
            String rModel = models.get(baseName).getLast().getName();
            int index = Integer.parseInt(rModel.substring(rModel.length()-2));
            ++index;
            String newIndex = String.format("%0" + padSize + "d",index);
            return newIndex;
        }
        else return "00";
    }
}
