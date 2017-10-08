import org.jblas.JavaBlas;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

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
        String[] args = {"./src/test/testDrivers/driver03.txt".replace("/", File.separator)};
        String name = "cube_centered_mw00.obj";
        Modeltoworld.main(args);
        String filepath = args[0].replace(".txt", "") + File.separator + name;
    }

    @Test
    public void testRotationMatrixIsOrthonormal()
    {
        Model testModel = new Model("test","model 0.0 1.0 0.0 45 2.0 10.0 0.0 10.0 cube.obj\n");
        testModel.transformAllVertices();
        testModel.saveAsObj("."+ File.separator + "driver00" + File.separator);
    }
}
