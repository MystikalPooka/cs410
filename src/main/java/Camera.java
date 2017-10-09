import org.jblas.Decompose;
import org.jblas.DoubleMatrix;
import org.jblas.Geometry;

public class Camera
{
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

    public void build()
    {
        WVector = Eye.sub(LookPoint);
        Geometry.normalize(WVector);

        UVector = MatrixMath.Cross(Up,WVector);
        Geometry.normalize(UVector);

        VVector = MatrixMath.Cross(WVector,UVector);
    }

    public int[][] castAllRays()
    {
        int[][] fullImage = new int[Resolution[0]][Resolution[1]];
        for(int row = 0; row < Resolution[0]; ++row)
        {
            for(int col = 0; col < Resolution[1]; ++col)
            {
                fullImage[row][col] = castRayGetColor(row,col);
            }
        }
        Image = fullImage;
        return fullImage;
    }

    private int castRayGetColor(int prow, int pcol)
    {
        castRay(getPixelPoint(prow,pcol));

        byte r = 0;
        byte g = 0;
        byte b = 0;
        return ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
    }

    //
    //Casts a ray into the scene.
    //RETURNS point of intersection (if it intersects a triangle)
    //
    private DoubleMatrix castRay(DoubleMatrix startPoint)
    {
        //TODO: Implement ray-triangle intersection (using cramer's rule)
        DoubleMatrix rayDirection = Geometry.normalize(startPoint.sub(Eye));
        return new DoubleMatrix();
    }

    private DoubleMatrix getPixelPoint(int row, int col)
    {
        double px = row/(Resolution[0]-1)*(LBRTBounds[2] - LBRTBounds[0])+LBRTBounds[0];
        double py = col/(Resolution[1]-1)*(LBRTBounds[3] - LBRTBounds[1])+LBRTBounds[1];
        return Eye.add(WVector.mul(-1*FocalLength)).add((UVector.mul(px))).add((VVector.mul(py)));
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
