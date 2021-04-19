package simulator.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import simulator.gui.SimulatorController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Person {

    public static int radius;

    private State state;
    private Position loc;
    private Direction heading;
    private LocalDateTime timeStamp;
    private int quarantineAfter = 10;
    private double asymptomaticPercentage = 0.5;
    private int othersInfected = 0;
    private double maxRange;

    public void setSimulationType(int simulationType) {
        this.simulationType = simulationType;
    }

    private int simulationType;

    public void setMask(boolean mask) {
        this.mask = mask;
    }

    private boolean mask;

    public void setVaccinated(boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    private boolean vaccinated;

    public Pane pane;

    private Circle c;

    public static int healtime;
    private int sicktime = 0;

    private Position origin;

    public double getCommunityTravelFactor() {
        return communityTravelFactor;
    }

    public void setCommunityTravelFactor(double communityTravelFactor) {
        this.communityTravelFactor = communityTravelFactor;
    }

    private double communityTravelFactor;


    final Random rand = new Random();
    public Person(State state, Pane pane,double maxRange) {
        this.state = state;
        loc = new Position(pane, radius);
        this.maxRange = 1;
        heading = new Direction(2);
        this.pane = pane;
        c = new Circle(radius, state.getColor());
        c.setStroke(Color.BLACK);
        pane.getChildren().add(c);

        origin = new Position(loc.getX(), loc.getY());
        this.timeStamp = LocalDateTime.now();
        quarantineAfter = SimulatorController.quarantineTime;
        mask = false;
    }

    public State getState() {
        return state;
    }

    public void move() {
        loc.move(heading, pane, radius, origin);
    }

    public void setState(State state) {
        this.state = state;
        c.setFill((state.getColor()));
    }




    public void draw() {
        c.setRadius(radius);
        c.setTranslateX(loc.getX());
        c.setTranslateY(loc.getY());
    }
     public void undraw() {
        c.setRadius(0);
        c.setTranslateX(0);
        c.setTranslateY(0);
    }

    public void checkInMarket(ArrayList<Person> ipList,int maskedPercentage, double RFactor, double maskEffectiveness){
        if(simulationType == 2){
            boolean toTransmit = false;
            Person temp=null;
            double effectiveR0 = RFactor * (double)(1 - (((double)maskedPercentage / 100) * (double)maskEffectiveness));
            for (Person person : ipList) {
                
                if (person.isInMarket() && (person.getState() == State.INFECTED) && person.othersInfected<Math.ceil(effectiveR0)) {
                    toTransmit = true;
                    temp = person;
                    break;
                }
            }
            if(toTransmit && isInMarket()){
               setState(State.INFECTED);
               this.timeStamp = LocalDateTime.now();
               temp.othersInfected++;
            }
//            if(toTransmit && isInMarket()){
//                double random = (maxRange) * rand.nextDouble();
//                if(mask && vaccinated){
//                    if(random < 0.06){
//                        setState(State.INFECTED);
//                        timeStamp = LocalDateTime.now();
//                    }
//                }
//                else if(!mask && vaccinated){
//                    if(random < 0.20){
//                        setState(State.INFECTED);
//                        timeStamp = LocalDateTime.now();
//                    }
//                }
//                else if(!mask){
//                    setState(State.INFECTED);
//                    timeStamp = LocalDateTime.now();
//                }
//            }


        }
    }

    private boolean isInMarket(){
        double x1, x2, y1, y2;
        x1 = 209;
        x2 = 309;
        y1 = 244;
        y2 = 337;
        double currentX = loc.getX();
        double currentY = loc.getY();
        if((x1 <= currentX)&&(currentX <= x2)&&(y1 <=currentY)&&(currentY <= y2)){
            return true;
        }
        else {
            return false;
        }
    }


    public void collide(Person other, int maskedPercentage, double RFactor, double maskEffectiveness) {
        if (this.loc.distance(other.loc) < 2 * radius) {


            if (other.state == State.INFECTED && state == State.SUSCEPTIBLE) {

                double effectiveR0 = RFactor * (double)(1 - (((double)maskedPercentage / 100) * (double)maskEffectiveness));

                if (other.othersInfected < Math.ceil(effectiveR0)) {
                    setState(State.INFECTED);
                    this.timeStamp = LocalDateTime.now();
                    other.othersInfected++;
                }
            }

//            double random = Math.random();
//            if (other.state == State.INFECTED && state == State.SUSCEPTIBLE) {
//                if(mask && vaccinated){
//
//                    if(random < 0.06){
//
//                        setState(State.INFECTED);
//                        this.timeStamp = LocalDateTime.now();
//                    }
//                }
//                else if(!mask && vaccinated){
//
//                    if(random < 0.20){
//
//                        setState(State.INFECTED);
//                        this.timeStamp = LocalDateTime.now();
//                    }
//                }
//                else if(!mask){
//                    setState(State.INFECTED);
//                    this.timeStamp = LocalDateTime.now();
//                }
//
//            }
        }

    }

    public void getBetter() {
        if (state == State.INFECTED) {
            sicktime++;

            if (sicktime > healtime) {
                setState(State.RECOVERED);
                sicktime = 0;
            }
        }
    }

    public void moveQuarantine(Pane pane){
        LocalDateTime now = LocalDateTime.now();

        if(ChronoUnit.SECONDS.between(this.timeStamp, now) == quarantineAfter){
            if(this.state == State.INFECTED){



                this.moveQuarantineDriver(pane);
            }
        }
    }

    public void moveQuarantineDriver(Pane pane){
        this.undraw();
        this.pane = pane;
        loc = new Position(pane, radius);
        heading = new Direction(2);

        c = new Circle(radius, state.getColor());
        c.setStroke(Color.BLACK);
        pane.getChildren().add(c);

        origin = new Position(loc.getX(), loc.getY());
    }

    public void moveToNewCommunity(Pane pane){
        
        double temp = rand.nextDouble();
        if(communityTravelFactor > temp){
            this.undraw();
            this.pane = pane;
            loc = new Position(pane, radius);
            heading = new Direction(2);

            c = new Circle(radius, state.getColor());
            c.setStroke(Color.BLACK);
            pane.getChildren().add(c);

            origin = new Position(loc.getX(), loc.getY());
        }
    }

    public Position getLoc() {
        return loc;
    }

    public void setLoc(Position loc) {
        this.loc = loc;
    }

    public int getOthersInfected() {
        return othersInfected;
    }

    public void setOthersInfected(int othersInfected) {
        this.othersInfected = othersInfected;
    }
    
    

}
