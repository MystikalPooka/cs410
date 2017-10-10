import org.jblas.DoubleMatrix;
import org.jblas.Geometry;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


public class TestMatrixMath
{
    @Test
    public void testLUDet()
    {
        DoubleMatrix A = new DoubleMatrix(3,3,3,0,0,0,3,0,0,0,3);
        assertEquals(27,MatrixMath.LUDet(A),0.00001);
    }

    @Test
    public void LUCramerRayIntersection()
    {
        DoubleMatrix A = new DoubleMatrix(3,1,3,0,0);
        DoubleMatrix B = new DoubleMatrix(3,1,0,3,0);
        DoubleMatrix C = new DoubleMatrix(3,1,0,0,3);

        DoubleMatrix rayPt = new DoubleMatrix(3,1,0,0,0);
        DoubleMatrix rayDir = new DoubleMatrix(3,1,-1,-1,-1);
        Geometry.normalize(rayDir);
        DoubleMatrix IntBetaGammaT = MatrixMath.LUCramerRayIntersection(A,B,C,rayPt,rayDir);
        assertEquals(1.0,IntBetaGammaT.get(0),0.000001);
        assertEquals(Math.sqrt(3),IntBetaGammaT.get(3),0.01);
        assertEquals(0.3333333,IntBetaGammaT.get(1),0.01);
        assertEquals(0.3333333,IntBetaGammaT.get(2),0.01);
    }

    @Test
    public void testCholeskyDet()
    {
        DoubleMatrix A = new DoubleMatrix(3,3,3,0,0,0,3,0,0,0,3);
        assertEquals(27,MatrixMath.CholeskyDet(A),0.00001);
    }
}
