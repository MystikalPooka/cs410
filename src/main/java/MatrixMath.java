import org.jblas.Decompose;
import org.jblas.DoubleMatrix;

public class MatrixMath
{
    public static DoubleMatrix Cross(DoubleMatrix a, DoubleMatrix b)
    {
        DoubleMatrix cross = new DoubleMatrix(1,3);

        DoubleMatrix i = new DoubleMatrix(new double[][]{{1,0,0}});
        double a2b3 = a.get(1) * b.get(2);
        double a3b2 = a.get(2) * b.get(1);
        double iScalar = a2b3 - a3b2;
        i.mmuli(iScalar);

        DoubleMatrix j = new DoubleMatrix(new double[][]{{0,1,0}});
        double a3b1 = a.get(2) * b.get(0);
        double a1b3 = a.get(0) * b.get(2);
        double jScalar = a3b1 - a1b3;
        j.mmuli(jScalar);

        DoubleMatrix k = new DoubleMatrix(new double[][]{{0,0,1}});
        double a1b2 = a.get(0) * b.get(1);
        double a2b1 = a.get(1) * b.get(0);
        double kScalar = a1b2 - a2b1;
        k.mmuli(kScalar);

        cross.addi(i);
        cross.addi(j);
        cross.addi(k);
        return cross;
    }

    public static DoubleMatrix HeterogenousRow(DoubleMatrix threeDRow)
    {
        return DoubleMatrix.concatHorizontally(threeDRow,DoubleMatrix.zeros(1));
    }

    //Returns a 1x4 column vector, where the first 3 columns denote t, beta, and gamme
    //the fourth column will be either 0 or 1. a 0 means this matrix is singular
    private static final double zeroDelta = 0.0000001;
    public static DoubleMatrix CramersSolve(DoubleMatrix A,DoubleMatrix B,DoubleMatrix C, DoubleMatrix rayPt, DoubleMatrix rayDir)
    {
        //array access optimized
        //lxyz = origin of ray || dxyz = unit length direction of ray
        //axyz|bxyz|cxyz = points a|b|c from triangle
        double ax = A.get(0); double ay = A.get(1); double az = A.get(2);
        double bx = B.get(0); double by = B.get(1); double bz = B.get(2);
        double cx = C.get(0); double cy = C.get(1); double cz = C.get(2);
        double dx = rayDir.get(0); double dy = rayDir.get(1); double dz = rayDir.get(2);
        double lx = rayPt.get(0); double ly = rayPt.get(1); double lz = rayPt.get(2);

        //all used > 4 times
        double axby = ax*by; double azcy = az*cy; double aybz = ay*bz; double azdy = az*dy;
        double aydx = ay*dx; double aybx = ay*bx; double bydx = by*dx; double bxdy = bx*dy;
        double cxdy = cx*by; double cydz = cy*dz; double cxdz = cx*dz; double dylz = dy*lz;
        double dzlz = dz*lz; double dxlz = dx*lz;

        //bxdycz-axdycz-bydxcz+aydxcz-bxcydz+axcydz+bycxdz-aycxdz-axbydz+aybxdz-bzcxdy+azcxdy+axbzdy-azbxdy+bzcydx-azcydx-aybzdx+azbydx
        double detM =-(ax*dy*lz)-cz*(bydx+aydx+bxdy)
                     -(cydz*bx)+(cydz*ax)+(cxdz*by)-(cxdz*ay)
                     -(axby*dz)+(aybx*dz)+(cxdy*bz)+(az*cxdy)+(ax*bz*dy)
                     -(az*bxdy)+(bz*cy*dx)-(azcy*dx)-(aybz*dx)+(bydx*az);

        if(Math.abs(detM) <= zeroDelta)
            return DoubleMatrix.zeros(1,1);

        //dylzcz-dxlzcz-azdycz+aydxcz-cydzlz+cxdzlz+aydzlz-axdzlz-cxdylz-azdylz+axdylz+cydxlz+azdxlz-aydxlz+azcydz-aycxdz-ayazdz+axaydz+azcxdy+azazdy-axazdy-azcydx
        double detM1 = cz*(dylz-dzlz-azdy+aydx)+dzlz*(cx+ay-ax-cy)+dylz*(ax-cx-az)+dxlz*(cy+az-ay)
                     + ((ay*dz)*(ax-cx-az)) + (az*(cydz+cxdy+azdy-(cy*dx)-(ax*dy)));

        double beta = 0;
        if(detM1 != 0) beta= detM1/detM;
        if(beta+zeroDelta < 0) return DoubleMatrix.zeros(1,1); //Point is not inside triangle

        double detM2 = (dz * ((((aybx - axby) + (by * lx)) - (ay * lx) - (bx * ly)) + (ax * ly)))
                + (az * (((bydx - bxdy) + (dy * lx)) - (dx * ly)))
                + (lz * (((aydx - bydx) + bxdy) - (ax * dy)))
                + (bz * ((((dx * ly) - (dy * lx)) + (ax * dy)) - aydx));

        double gamma = 0;
        if(detM2 != 0) gamma = detM2/detM;
        if(detM1 != 0) beta= detM1/detM;
        if(gamma+zeroDelta < 0 || beta+gamma-zeroDelta > 1) return DoubleMatrix.zeros(1,1); //Point is not inside triangle

        double detM3 = (lz * ((cz * (ay - by)) + ((cy * (((bx - bz) + az) - ax)) + (ay * (bz - az - bx)) + axby)))
                + (cz * (((ly * (ax - bx)) - (lx * (ay + by))) + (by * (az - ax)) + (ay * (by - az))))
                + (az * (((cy * (ax - bz - bx - az)) + (ay * ((lx + az + ax + cx) - bz - cz)) + (ly * (bx - cx))) - (by * lx)))
                + (ay * ((bz * (ax - cx)) + (bx * cz)))
                + (ly * (bz * (cx - ax)));

        double t = 0;
        if(detM3 != 0) t = detM3/detM;
        if(!(t-zeroDelta>0)) return DoubleMatrix.zeros(1,1);

        return new DoubleMatrix(4,1,1,beta,gamma,t);
    }

    public static DoubleMatrix LUCramerRayIntersection(DoubleMatrix A, DoubleMatrix B, DoubleMatrix C, DoubleMatrix rayPt, DoubleMatrix rayDir)
    {
        double ax = A.get(0); double ay = A.get(1); double az = A.get(2);
        double bx = B.get(0); double by = B.get(1); double bz = B.get(2);
        double cx = C.get(0); double cy = C.get(1); double cz = C.get(2);
        double dx = rayDir.get(0); double dy = rayDir.get(1); double dz = rayDir.get(2);

        double axmbx = ax-bx; double aymby = ay-by; double azmbz = az-bz;
        double axmcx = ax-cx; double aymcy = ay-cy; double azmcz = az-cz;

        DoubleMatrix M = new DoubleMatrix(new double[][]{{axmbx,axmcx,dx},{aymby,aymcy,dy},{azmbz,azmcz,dz}});

        double detM = LUDet(M);
        if(Math.abs(detM) < zeroDelta)
            return DoubleMatrix.zeros(1,1);

        double lx = rayPt.get(0); double ly = rayPt.get(1); double lz = rayPt.get(2);
        double axmlx = ax-lx; double aymly = ay-ly; double azmlz = az-lz;

        DoubleMatrix M1 = new DoubleMatrix(new double[][]{{axmlx,axmcx,dx},{aymly,aymcy,dy},{azmlz,azmcz,dz}});

        double detM1 = LUDet(M1);
        double beta = 0;
        if(Math.abs(detM1) > zeroDelta) beta = detM1/detM;
        if(beta < 0-zeroDelta) return DoubleMatrix.zeros(1,1); //Point is not inside triangle

        DoubleMatrix M2 = new DoubleMatrix(new double[][]{{axmbx,axmlx,dx},{aymby,aymly,dy},{azmbz,azmlz,dz}});

        double detM2 = LUDet(M2);

        double gamma = 0;
        if(Math.abs(detM2) > zeroDelta) gamma = detM2/detM;
        if(gamma < 0-zeroDelta || beta+gamma > 1+zeroDelta) return DoubleMatrix.zeros(1,1); //Point is not inside triangle

        DoubleMatrix M3 = new DoubleMatrix(new double[][]{{axmbx,axmcx,axmlx},{aymby,aymcy,aymly},{azmbz,azmcz,azmlz}});

        double detM3 = LUDet(M3);
        double t = 0;
        if(Math.abs(detM3) > zeroDelta) t = detM3/detM;
        if(t < 0-zeroDelta || Math.abs(t) < zeroDelta) return DoubleMatrix.zeros(1,1);

        return new DoubleMatrix(4,1,1,beta,gamma,t);
    }

    public static double CholeskyDet(DoubleMatrix a)
    {
        final DoubleMatrix cholesky = Decompose.cholesky(a);
        DoubleMatrix detA = (Decompose.cholesky(a)).diag();
        return Math.pow(detA.prod(),2);
    }

    public static double LUDet(DoubleMatrix a)
    {
        Decompose.LUDecomposition<DoubleMatrix> lu = Decompose.lu(a);
        return (lu.l.diag().prod() * lu.u.diag().prod());
    }
}
