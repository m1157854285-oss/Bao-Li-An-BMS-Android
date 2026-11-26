package io.bmsride.monitor;

import java.util.ArrayList;
import java.util.List;

/** Recent 120 seconds, with missing data kept as gaps. All calls on service/UI main thread. */
final class TripHistory {
    static final long WINDOW_MS = 120_000;
    static final class Sample {
        final long at; final double speed, efficiency;
        Sample(long at, double speed, double efficiency) {
            this.at=at; this.speed=speed; this.efficiency=efficiency;
        }
    }
    private final ArrayList<Sample> samples = new ArrayList<>();
    private double maxSpeed = Double.NaN;
    void observeSpeed(double speed) {
        if(Double.isFinite(speed)&&speed>=0) maxSpeed=Double.isFinite(maxSpeed)?Math.max(maxSpeed,speed):speed;
    }
    void add(long at,double speed,double efficiency) {
        if(!samples.isEmpty()&&at<samples.get(samples.size()-1).at) samples.clear();
        samples.add(new Sample(at,speed,efficiency));
        while(!samples.isEmpty()&&(samples.get(0).at<at-WINDOW_MS||samples.size()>121)) samples.remove(0);
        observeSpeed(speed);
    }
    double maxSpeed(){return maxSpeed;}
    List<Sample> copy(){return new ArrayList<>(samples);}
    void clear(){samples.clear();maxSpeed=Double.NaN;}
}
