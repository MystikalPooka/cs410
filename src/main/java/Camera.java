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
        WVector = Geometry.normalize(WVector);

        UVector = MatrixMath.Cross(Up, WVector);
        UVector = Geometry.normalize(UVector);

        VVector = MatrixMath.Cross(WVector, UVector);
        VVector = Geometry.normalize(VVector);
    }

    private static final double zeroDelta = 0.0001;
    public void castAllRays()
    {
        double[][] tValues = new double[Resolution[0]][Resolution[1]];
        double tMin = Double.MAX_VALUE;
        double tMax = Double.MIN_VALUE;
        for (int row = 0; row < Resolution[0]; ++row)
        {
            for (int col = 0; col < Resolution[1]; ++col)
            {
                DoubleMatrix pxpy = getPixelPoint(row, col);
                double pixelMin = Double.MAX_VALUE;
                for (Model m : DriverFactory.getAllLoadedModels())
                {
                    DoubleMatrix[] allFaces = m.getFaceTriangles();
                    for (DoubleMatrix face : allFaces)
                    {
                        double t = castRay(pxpy, face.getRow(0), face.getRow(1), face.getRow(2));
                        if(t <= 0-zeroDelta) continue;

                        if (t < pixelMin)
                            pixelMin = t;
                    }
                }
                tValues[row][col] = pixelMin;
                if(pixelMin < tMin)
                {
                    tMin = pixelMin;
                }
                if(pixelMin > tMax && pixelMin != Double.MAX_VALUE)
                {
                    tMax = pixelMin;
                }
            }
        }
        SetColorsOfPixels(tValues,tMin, tMax);
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
    private void SetColorsOfPixels(double[][] tvalues, double tmin, double tmax)
    {
        Image = new int[Resolution[0]][Resolution[1]*3];

        double divisor = tmax - tmin;
        int r,g,b;
        for (int row = 0; row < Image.length; ++row)
        {
            for (int col = 0; col < Image[0].length; col+=3)
            {
                double t = tvalues[row][col/3];
                if(Math.abs(t) > zeroDelta)
                {
                    double ratio = 2*(t-tmin)/divisor;
                    r = (int)Math.max(0,255*(1-ratio));
                    b = (int)Math.max(0,255*(ratio-1));
                    g = (255-b-r);
                }
                else
                {
                    r = 239;
                    g = 239;
                    b = 239;
                }

                Image[row][col] = r;
                Image[row][col+1] = g;
                Image[row][col+2] = b;
            }
        }
    }



    private DoubleMatrix getPixelPoint(int row, int col)
    {
        double left = LBRTBounds[0];
        double bottom = LBRTBounds[1];
        double px = (row/(Resolution[0]-1)*(LBRTBounds[2] - left))+left;
        double py = (col/((Resolution[1]-1)*(LBRTBounds[3] - bottom)))+bottom;
        DoubleMatrix pxpy = Eye.add(WVector.mul(-1*FocalLength));
        pxpy.addi(UVector.mul(px));
        pxpy.addi(VVector.mul(py));
        return pxpy;
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
