package simulator.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import simulator.gui.SimulatorController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    /**
     *
     * @param simulationType set simulation type - used for case 2 simulation
     */
    public void setSimulationType(int simulationType) {
        this.simulationType = simulationType;
    }

    private int simulationType;

    /**
     *
     * @param mask set mask on a person
     */
    public void setMask(boolean mask) {
        this.mask = mask;
    }

    private boolean mask;

    /**
     * set person vaccinated
     * @param vaccinated set person vaccinated
     */
    public void setVaccinated(boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    private boolean vaccinated;

    public Pane pane;

    private Circle c;

    public static int healTime;
    private int sickTime = 0;

    private Position origin;

    /**
     * get community travel factor - higher value means more chance to travel between communities
     * @return
     */
    public double getCommunityTravelFactor() {
        return communityTravelFactor;
    }

    /**
     *
     * @param communityTravelFactor setter for above mentioned function
     */
    public void setCommunityTravelFactor(double communityTravelFactor) {
        this.communityTravelFactor = communityTravelFactor;
    }

    private double communityTravelFactor;


    final Random rand = new Random();

    /**
     * Constructor for person class
     * @param state - from State enumeration
     * @param pane - frame
     */
    public Person(State state, Pane pane) {
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

    /**
     * get current state
     * @return
     */
    public State getState() {
        return state;
    }

    /**
     * move person in pane
     */
    public void move() {
        loc.move(heading, pane, radius, origin);
    }

    /**
     *
     * @param state set new state
     */
    public void setState(State state) {
        this.state = state;
        c.setFill((state.getColor()));
    }

    /**
     * draw simulation
     */
    public void draw() {
        c.setRadius(radius);
        c.setTranslateX(loc.getX());
        c.setTranslateY(loc.getY());
    }

    /**
     * clear simulation
     */
    public void undraw() {
        c.setRadius(0);
        c.setTranslateX(0);
        c.setTranslateY(0);
    }

    /**
     *
     * @param ipList list of people
     * @param maskedPercentage percentage wearing masks
     * @param vaccinatedPercentage percentage vaccinated
     * @param RFactor - r factor
     * @param maskEffectiveness - mask effectiveness
     * @param vaccineEfficacy - efficacy
     */
    public void checkInMarket(ArrayList<Person> ipList,int maskedPercentage, int vaccinatedPercentage, double RFactor, double maskEffectiveness, double vaccineEfficacy){
        if(simulationType == 2){
            boolean toTransmit = false;
            Person temp=null;
            double effectiveR0 = RFactor * (double)(1 - (((double)maskedPercentage / 100) * (double)maskEffectiveness)
                    - (((double)vaccinatedPercentage / 100) * (double)vaccineEfficacy)
            );

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
        }
    }

    /**
     *
     * @return check if in market
     */
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

    /**
     *
     * @param other other person
     * @param maskedPercentage - percentage masked
     * @param vaccinatedPercentage - percentage vaccinated
     * @param RFactor - r factor
     * @param maskEffectiveness - effectiveness of masks
     * @param vaccineEfficacy - efficacy of vaccine
     */
    public void collide(Person other, int maskedPercentage, int vaccinatedPercentage, double RFactor, double maskEffectiveness, double vaccineEfficacy) {
        if (this.loc.distance(other.loc) < 2 * radius) {
            if (other.state == State.INFECTED && state == State.SUSCEPTIBLE) {

                double effectiveR0 = RFactor * (double)(1 - (((double)maskedPercentage / 100) * (double)maskEffectiveness)
                 - (((double)vaccinatedPercentage / 100) * (double)vaccineEfficacy)
                );

                if (other.othersInfected < Math.ceil(effectiveR0)) {
                    setState(State.INFECTED);
                    this.timeStamp = LocalDateTime.now();
                    other.othersInfected++;
                }
            }
        }
    }

    /**
     * reduce virus incubation period
     */
    public void getBetter() {
        if (state == State.INFECTED) {
            sickTime++;

            if (sickTime > healTime) {
                setState(State.RECOVERED);
                sickTime = 0;
            }
        }
    }

    /**
     *
     * @param pane move to quarantine pane
     */
    public void moveQuarantine(Pane pane){
        LocalDateTime now = LocalDateTime.now();

        if(ChronoUnit.SECONDS.between(this.timeStamp, now) == quarantineAfter){
            if(this.state == State.INFECTED){
                this.moveQuarantineDriver(pane);
            }
        }
    }

    /**
     *
     * @param pane driver to move to different pane
     */
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

    /**
     *
     * @param pane move to different community
     */
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

    /**
     *
     * @return current location
     */
    public Position getLoc() {
        return loc;
    }

    /**
     *
     * @param loc set location
     */
    public void setLoc(Position loc) {
        this.loc = loc;
    }

    /**
     *
     * @return people infected by current person
     */
    public int getOthersInfected() {
        return othersInfected;
    }

    /**
     *
     * @param othersInfected update others infected by this person
     */
    public void setOthersInfected(int othersInfected) {
        this.othersInfected = othersInfected;
    }

    /**
     *
     * @return sick time
     */
    public int getSickTime() {
        return sickTime;
    }

    /**
     *
     * @param sickTime
     */
    public void setSickTime(int sickTime) {
        this.sickTime = sickTime;
    }
    
    
}
