import org.jblas.DoubleMatrix;
import org.jblas.Geometry;

import java.io.*;
import java.util.ArrayList;

public class Model
{
    private static final int precision = 6;
    private String Name;
    private String referenceFilePath = "";

    private ArrayList<DoubleMatrix> objectVertices = new ArrayList<>();
    private DoubleMatrix transformedObjectVertices = new DoubleMatrix(0,4);

    private double angleTheta;
    private DoubleMatrix rotationMatrix = new DoubleMatrix(0,4);
    private DoubleMatrix axisAngleRotMatrix = new DoubleMatrix(0,4);
    private double scaleFactor;
    private DoubleMatrix translationMatrix = new DoubleMatrix(0,4);

    private ArrayList<String> faces = new ArrayList<>();
    private DoubleMatrix[] faceTriangles;

    Model(String objectName, String line)
    {
        setName(objectName);
        setReferencePath("." + File.separator + "models" + File.separator + objectName + ".obj");
        loadFromDriverFile();
        loadTransformationMatricesFromLine(line);
        transformAllVertices();
        loadAllFaces();
    }

    void setName(String name)
    {
        this.Name = name;
    }

    String getName()
    {
        return this.Name;
    }

    private void setReferencePath(String refPath)
    {
        this.referenceFilePath = refPath;
    }

    private void loadFromDriverFile()
    {
        try
        {
            FileReader fr = new FileReader(referenceFilePath);
            BufferedReader buffer = new BufferedReader(fr);
            System.out.println(referenceFilePath);

            String line;
            while((line = buffer.readLine()) != null)
            {
                String[] splitLine = line.split(" ");
                switch(splitLine[0])
                {
                    case("v"):
                        DoubleMatrix vector = new DoubleMatrix(4,1);
                        for(int i = 1; i < splitLine.length; ++i)
                        {
                            vector.put(i-1,0,Double.parseDouble(splitLine[i]));
                        }
                        objectVertices.add(vector);
                        break;
                    case("f"):
                        line = line.replace("f ","");
                        faces.add(line);
                        break;
                    default:
                        break;
                        //all others
                }
            }
            fr.close();
            buffer.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private void loadTransformationMatricesFromLine(String line)
    {
        String[] splitLine = line.split(" ");
        double[] rotColumn = { Double.parseDouble(splitLine[1]),
                               Double.parseDouble(splitLine[2]),
                               Double.parseDouble(splitLine[3]),0};

        DoubleMatrix rotColMatrix = new DoubleMatrix(1,4,rotColumn);
        Geometry.normalize(rotColMatrix);
        rotationMatrix = createRotationMatrixFromSingleRow(rotColMatrix);
        angleTheta = Double.parseDouble(splitLine[4]);
        angleTheta = Math.toRadians(angleTheta);

        double[] firstRow = new double[]{Math.cos(angleTheta),(-1 * Math.sin(angleTheta)) +0.0f,0,0};
        double[] secondRow = new double[]{Math.sin(angleTheta),Math.cos(angleTheta),0,0};
        double[] thirdRow = new double[]{0,0,1,0};
        double[] fourthRow = new double[]{0,0,0,1};
        axisAngleRotMatrix = new DoubleMatrix(new double[][]{firstRow,
                                                            secondRow,
                                                            thirdRow,
                                                            fourthRow});

        scaleFactor = Double.parseDouble((splitLine[5]));
        double[] transColumn = { Double.parseDouble(splitLine[6]),
                                 Double.parseDouble(splitLine[7]),
                                 Double.parseDouble(splitLine[8]),1};

        translationMatrix = DoubleMatrix.eye(4);
        translationMatrix.putColumn(3,new DoubleMatrix(transColumn));
    }

    void transformAllVertices()
    {
        DoubleMatrix R = DoubleMatrix.eye(4);
        if(axisAngleRotMatrix.equals(DoubleMatrix.eye(4)))
        {
            System.out.println("Equal");
        }
        else
        {
            R = rotationMatrix.mmul(axisAngleRotMatrix);
            R.mmuli(rotationMatrix.transpose());
        }

        for(DoubleMatrix m : R.rowsAsList())
        {
            for(DoubleMatrix n : R.rowsAsList())
            {
                if(m.dot(n) != 0 && !m.equals(n))
                {
                    System.out.println("M: " + m);
                    System.out.println("N: " + n);
                    System.out.println("M . N NOT ZERO!!!");
                }
            }
        }

        transformedObjectVertices = new DoubleMatrix(objectVertices.size(),4);
        int row = 0;
        for(DoubleMatrix vector : objectVertices)
        {
            //System.out.println("V:" + vector);
            vector.put(3,0);
            transformedObjectVertices.putRow(row,vector);
            ++row;
            //System.out.println("Dot:" + vector);
        }

        //4x4 I * scale = scale on all diagonal entries
        DoubleMatrix S = DoubleMatrix.eye(4).mmul(scaleFactor);
        S.putColumn(3,new DoubleMatrix(new double[]{0,0,0,1}));

        DoubleMatrix T = DoubleMatrix.eye(4);
        T.putColumn(3,translationMatrix.transpose());

        transformedObjectVertices = transformedObjectVertices.transpose();
        transformedObjectVertices = R.mmul(S).mmul(T).mmul(transformedObjectVertices).transpose();
        //transformedObjectVertices = transformedObjectVertices.mmul(Big);
        //System.out.println("TRANSFORMED:");
//        for(DoubleMatrix tv : transformedObjectVertices.rowsAsList())
//        {
//            System.out.println(tv.toString());
//        }
    }

    private DoubleMatrix createRotationMatrixFromSingleRow(DoubleMatrix W)
    {
        DoubleMatrix rotMatrix = new DoubleMatrix(4,4);

        DoubleMatrix M = getOrthonormalRowVector(W);

        DoubleMatrix U = MatrixMath.HeterogenousRow(MatrixMath.Cross(W,M));
        DoubleMatrix V = MatrixMath.HeterogenousRow(MatrixMath.Cross(U,W));
        if(U.dot(W) != 0) System.out.println("W.U not 0");
        if(U.dot(M) != 0) System.out.println("M.U not 0");

        rotMatrix.putRow(0,U);
        rotMatrix.putRow(1,V);
        rotMatrix.putRow(2,W);
        rotMatrix.putRow(3,new DoubleMatrix(1,4,0,0,0,1));

        return Geometry.normalizeRows(rotMatrix);
    }

    private DoubleMatrix getOrthonormalRowVector(DoubleMatrix row)
    {
        DoubleMatrix orthoRow = new DoubleMatrix(row.getColumns());
        orthoRow.put(row.argmin(),1);

        //Geometry.normalize(orthoRow);
        return orthoRow;
    }

    public void loadAllFaces()
    {
        faceTriangles = new DoubleMatrix[faces.size()];
        for (int i = 0; i < faces.size();++i)
        {
            String face = faces.get(i);
            String[] pointIndices = face.split("[/ ]");

            DoubleMatrix triangle = new DoubleMatrix(3, 3);
            for (int p = 0; p < pointIndices.length/3; ++p)
            {
                int index = Integer.parseInt(pointIndices[p*3]);
                DoubleMatrix row = transformedObjectVertices.getRow(index-1);
                triangle.putRow(p, row);
            }
            faceTriangles[i] = triangle;
        }
    }

    public DoubleMatrix[] getFaceTriangles() {return faceTriangles;}

    public void saveAsObj(String folderPath)
    {
        File file = new File(folderPath + File.separator + getName() +".obj");
        if(file.isFile() && file.exists())
        {
            file.delete();
        }

        try
        {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            for(DoubleMatrix m : transformedObjectVertices.rowsAsList())
            {
                bw.append("v");
                String line = "";
                int i = 0;
                for(double d : m.elementsAsList())
                {
                    ++i;
                    if(i == 4) continue;
                    line += String.format(" %0" + precision + "f",d);
                }
                bw.append(" " + line);
                bw.newLine();
            }

            bw.flush();
            for(String face : faces)
            {
                bw.append("f " + face + "\n");
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
