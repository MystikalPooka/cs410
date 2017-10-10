import org.jblas.DoubleMatrix;
import org.jblas.Geometry;

public class Camera {
    private DoubleMatrix Eye;
    private DoubleMatrix LookPoint;
    private DoubleMatrix Up;
    private double FocalLength;
    private double[] LBRTBounds;
    private int[] Resolution;

    private DoubleMatrix WVector;
    private DoubleMatrix UVector;
    private DoubleMatrix VVector;

    private int[][] Image;

    public void build() {
        WVector = Eye.sub(LookPoint);
        Geometry.normalize(WVector);

        UVector = MatrixMath.Cross(Up, WVector);
        Geometry.normalize(UVector);

        VVector = MatrixMath.Cross(WVector, UVector);
    }

    private static final double zeroDelta = 0.0000001;
    public void castAllRays()
    {
        double[][] tValues = new double[Resolution[0]][Resolution[1]];
        double tMin = Double.MAX_VALUE;
        double tMax = Double.MIN_VALUE;
        for (int row = 0; row < Resolution[0]; ++row)
        {
            for (int col = 0; col < Resolution[1]; ++col)
            {
                for (Model m : DriverFactory.getAllLoadedModels())
                {
                    DoubleMatrix[] allFaces = m.getFaceTriangles();
                    for (DoubleMatrix face : allFaces)
                    {
                        DoubleMatrix pxpy = getPixelPoint(row, col);
                        double t = castRay(pxpy, face.getRow(0), face.getRow(1), face.getRow(2));
                        if(t < 0) continue;
                        tValues[row][col] = t;
                        if (t < tMin)
                            tMin = t;
                        if (t > tMax)
                            tMax = t;
                    }
                }
            }
        }
        SetColorsOfPixels(tValues,tMin, tMax);
    }

    private void SetColorsOfPixels(double[][] tvalues, double tmin, double tmax)
    {
        Image = new int[Resolution[0]][Resolution[1]*3];

        double divisor = tmax - tmin;
        for (int row = 0; row < Resolution[0]; ++row)
        {
            for (int col = 0; col < Resolution[1]; col+=3)
            {
                double ratio = (2*(tvalues[row][col/3]-tmin))/divisor;
                int r = (int)Math.max(0,255*(1-ratio));
                int b = (int)Math.max(0,255*(ratio-1));
                int g = (255-b-r);
                Image[row][col] = r;
                Image[row][col+1] = g;
                Image[row][col+2] = b;
            }
        }
    }

    //
    //Casts a ray into the scene.
    //RETURNS point of intersection (if it intersects a triangle)
    //
    private double castRay(DoubleMatrix LOrigin,DoubleMatrix a,DoubleMatrix b, DoubleMatrix c)
    {
        DoubleMatrix rayDirection = Geometry.normalize(LOrigin.sub(Eye));
        DoubleMatrix IntBetaGammaT = MatrixMath.LUCramerRayIntersection(a,b,c,LOrigin,rayDirection);
        return IntBetaGammaT.get(0) == 1 ? IntBetaGammaT.get(3) : -1;
    }

    private DoubleMatrix getPixelPoint(int row, int col)
    {
        double left = LBRTBounds[0];
        double bottom = LBRTBounds[1];
        double px = (row/(Resolution[0]-1)*(LBRTBounds[2] - left))+left;
        double py = (col/((Resolution[1]-1)*(LBRTBounds[3] - bottom)))+bottom;
        return Eye.add(WVector.muli(-1*FocalLength)).add((UVector.muli(px))).add((VVector.muli(py)));
    }

    public void SaveToPPM(String filename)
    {
        PPMSaver.SavePPMToFile(filename,Image);
    }

    public void setEye(double[] eye) {
        Eye = new DoubleMatrix(eye);
    }

    public void setLookPoint(double[] lookPoint) {
        LookPoint = new DoubleMatrix(lookPoint);
    }

    public void setUpDirection(double[] up) {
        Up = new DoubleMatrix(up);
    }

    public void setFocalLength(double focalLength) {
        FocalLength = focalLength;
    }

    public void setLBRTBounds(double[] LBRTBounds) {
        this.LBRTBounds = LBRTBounds;
    }

    public void setResolution(int[] resolution) {
        Resolution = resolution;
    }
}
