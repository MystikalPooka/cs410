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
        String outFilename = args[1].toLowerCase();

        DriverFactory.BuildAllFromDriverFile(inFilename);
    }
}
