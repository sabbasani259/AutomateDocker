package remote.wise.util;

public class Geofencing 
{
	public double calDistance(double Lat1,double Long1, double Lat2, double Long2)
	{
		double dDistance = 0;
        double dLat1InRad = Lat1 * (Math.PI / 180.0);
        double dLong1InRad = Long1 * (Math.PI / 180.0);
        double dLat2InRad = Lat2 * (Math.PI / 180.0);
        double dLong2InRad = Long2 * (Math.PI / 180.0);

        double dLongitude = dLong2InRad - dLong1InRad;
        double dLatitude = dLat2InRad - dLat1InRad;

        // Intermediate result a.
        double a = Math.pow(Math.sin(dLatitude / 2.0), 2.0) + 
                   Math.cos(dLat1InRad) * Math.cos(dLat2InRad) * 
                   Math.pow(Math.sin(dLongitude / 2.0), 2.0);

        // Intermediate result c (great circle distance in Radians).
        double c = 2.0 * Math.asin(Math.sqrt(a));

        // Distance.
        // const Double kEarthRadiusMiles = 3956.0;
        Double kEarthRadiusKms = 6376.5;
        dDistance = kEarthRadiusKms * c;

        return dDistance;
	}
}
