package io.bmsride.monitor;

/** GPS-provider speed only; conservative hysteresis to reject stationary jitter. */
final class GpsFilter {
    static final class Result {
        final double kmh, meters;
        Result(double kmh,double meters){this.kmh=kmh;this.meters=meters;}
    }
    private long lastAt=-1;
    private double lastMps;
    private boolean moving;
    private int startCount;
    void reset(){lastAt=-1;lastMps=0;moving=false;startCount=0;}
    Result add(long at,double accuracy,double mps,double speedAccuracy){
        if(at<0||!Double.isFinite(accuracy)||accuracy<=0||accuracy>25||!Double.isFinite(mps)||mps<0||mps>60||(!Double.isNaN(speedAccuracy)&&(!Double.isFinite(speedAccuracy)||speedAccuracy<0||speedAccuracy>1.5))){reset();return new Result(Double.NaN,0);}
        if(lastAt>=0&&at<=lastAt){reset();return new Result(Double.NaN,0);}
        long dt=lastAt<0?0:at-lastAt;
        if(dt>3000){reset();dt=0;}
        double uncertainty=Double.isNaN(speedAccuracy)?1.0:speedAccuracy;
        boolean wasMoving=moving;
        // Require three consecutive reliable samples before considering movement real.
        if(!moving){if(mps>=1.4&&mps-uncertainty>=0.7){if(++startCount>=3)moving=true;}else startCount=0;}
        else if(mps<=0.8||mps-uncertainty<=0.15){moving=false;startCount=0;}
        double distance=moving&&wasMoving&&dt>0?(lastMps+mps)*0.5*dt/1000.0:0;
        lastAt=at;lastMps=moving?mps:0;
        return new Result(moving?mps*3.6:0,distance);
    }
}
