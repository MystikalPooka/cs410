import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class testModel
{
    @Before
    public void setUp()
    {

    }

    @Test
    public void testModelCreation()
    {
        String testDir = "./src/test/testDrivers/".replace("/", File.separator);

        String[] args = {testDir + "driver03.txt"};
        String name = "cube_centered_mw00.obj";
        Modeltoworld.main(args);
        String filepath = args[0].replace(".txt", "") + File.separator + name;

        File testFile = new File("./src/test/testModels/driver03/"+name.replace("/", File.separator));
        File outputFile = new File(filepath);
        try {
            FileReader testFileReader = new FileReader(testFile);
            FileReader outputFileReader = new FileReader(outputFile);

            String testLine = "";
            String outputLine = "";
        } catch (FileNotFoundException e)
        {
            assertEquals(1,0);
            e.printStackTrace();
        }
    }
}
