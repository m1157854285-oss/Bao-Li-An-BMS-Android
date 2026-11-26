package io.bmsride.monitor;
/** Discharge energy and distance over intervals with both valid phone GPS and BLE battery data. */
final class TripEnergy {
    private long lastAt=-1;
    private double lastKm,lastWatts,coveredKm,wh;
    void add(long at,double km,double watts){
        if(!Double.isFinite(km)||!Double.isFinite(watts)||watts<0){breakInterval();return;}
        if(lastAt>=0&&at>lastAt&&at-lastAt<=3000&&km>=lastKm){
            wh+=(lastWatts+watts)*0.5*(at-lastAt)/3_600_000.0;
            coveredKm+=km-lastKm;
        }
        lastAt=at;lastKm=km;lastWatts=watts;
    }
    double efficiency(){return coveredKm>=0.5&&wh>0?wh/coveredKm:Double.NaN;}
    void breakInterval(){lastAt=-1;}
    void clear(){breakInterval();coveredKm=wh=0;}
}
