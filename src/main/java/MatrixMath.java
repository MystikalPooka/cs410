import org.jblas.Decompose;
import org.jblas.DoubleMatrix;

public class MatrixMath
{
    public static DoubleMatrix Cross(DoubleMatrix a, DoubleMatrix b)
    {
        DoubleMatrix cross = new DoubleMatrix(1,4);

        DoubleMatrix i = new DoubleMatrix(new double[][]{{1,0,0,0}});
        double a2b3 = a.get(1) * b.get(2);
        double a3b2 = a.get(2) * b.get(1);
        double iScalar = a2b3 - a3b2;
        i.mmuli(iScalar);

        DoubleMatrix j = new DoubleMatrix(new double[][]{{0,1,0,0}});
        double a3b1 = a.get(2) * b.get(0);
        double a1b3 = a.get(0) * b.get(2);
        double jScalar = a3b1 - a1b3;
        j.mmuli(jScalar);

        DoubleMatrix k = new DoubleMatrix(new double[][]{{0,0,1,0}});
        double a1b2 = a.get(0) * b.get(1);
        double a2b1 = a.get(1) * b.get(0);
        double kScalar = a1b2 - a2b1;
        k.mmuli(kScalar);

        cross.addi(i);
        cross.addi(j);
        cross.addi(k);
        cross.put(3,0);
        return cross;
    }

    public static DoubleMatrix CramersSolve(DoubleMatrix m)
    {
        //TODO: Implement cramer sub-matrices
        DoubleMatrix M1 = new DoubleMatrix();
        DoubleMatrix M2 = new DoubleMatrix();
        DoubleMatrix M3 = new DoubleMatrix();

        double x = Determinant(M1)/Determinant(m);
        double y = Determinant(M2)/Determinant(m);
        double z = Determinant(M3)/Determinant(m);

        return new DoubleMatrix(3,1,x,y,z);
    }

    public static double Determinant(DoubleMatrix a)
    {
        DoubleMatrix detA = (Decompose.cholesky(a)).diag();
        return detA.dot(detA);
    }
}
