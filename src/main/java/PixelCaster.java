public class PixelCaster
{
    public static int castRayGetColor(double[] pixel)
    {
        byte r = 0;
        byte g = 0;
        byte b = 0;
        return ((r&0x0ff)<<16)|((g&0x0ff)<<8)|(b&0x0ff);
    }
}
