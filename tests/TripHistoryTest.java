package io.bmsride.monitor;
public class TripHistoryTest {
    static void check(boolean b){if(!b)throw new AssertionError();}
    public static void main(String[] args){
        TripHistory h=new TripHistory();check(Double.isNaN(h.maxSpeed()));
        h.add(1000,24,18.3);h.add(2000,Double.NaN,Double.NaN);h.observeSpeed(34);
        check(h.copy().size()==2&&Double.isNaN(h.copy().get(1).speed)&&h.maxSpeed()==34);
        for(int i=3;i<=150;i++)h.add(i*1000L,20,18);
        check(h.copy().size()==121&&h.copy().get(0).at==30000&&h.maxSpeed()==34);
        h.copy().clear();check(h.copy().size()==121);h.clear();check(h.copy().isEmpty()&&Double.isNaN(h.maxSpeed()));
        h.add(2000,10,Double.NaN);h.add(1000,5,Double.NaN);check(h.copy().size()==1);
        System.out.println("PASS 120s chart window / missing data / maximum speed / reset / defensive copy");
    }
}
