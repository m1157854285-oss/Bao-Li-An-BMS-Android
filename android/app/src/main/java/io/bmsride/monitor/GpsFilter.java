package io.bmsride.monitor;

/** Phone GPS speed, responsive at walking pace; suppress only near-zero noise. */
final class GpsFilter {
    static final long MAX_AGE_MS=5000;
    static final class Result {
        final double kmh, meters;
        Result(double kmh,double meters){this.kmh=kmh;this.meters=meters;}
    }
    private long lastAt=-1;
    private double lastMps;
    void reset(){lastAt=-1;lastMps=0;}
    Result add(long at,double accuracy,double mps,double speedAccuracy){
        if(at<0||!Double.isFinite(accuracy)||accuracy<=0||accuracy>35||!Double.isFinite(mps)||mps<0||mps>60){reset();return new Result(Double.NaN,0);}
        if(lastAt>=0&&at<=lastAt){return new Result(Double.NaN,0);}
        long dt=lastAt<0?0:at-lastAt;
        // No multi-sample start gate or hard speed-accuracy rejection.
        // The speed estimate is from the GNSS provider, not position drift.
        double effective=mps<0.25?0:mps;
        double distance=dt>0&&dt<=MAX_AGE_MS?(lastMps+effective)*0.5*dt/1000.0:0;
        lastAt=at;lastMps=effective;
        return new Result(effective*3.6,distance);
    }
}
