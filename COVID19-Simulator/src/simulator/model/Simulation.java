package simulator.model;

import javafx.scene.layout.Pane;
import simulator.gui.SimulatorController;
import java.util.ArrayList;

public class Simulation {
    //Master list
    public ArrayList<Person> people;
    //Percentage of people wearing masks
    private static int maskedPercentage;
    //Percentage of people vaccinated
    private static int vaccinatedPercentage;


    /**
     * Constructor for simulation class
     * @param popCount - population count
     * @param world - simulation pane
     */
    public Simulation(int popCount, Pane world) {
        people = new ArrayList<>();
        for (int i = 0; i < popCount; i++) {
            //new instances of person class
            Person p = new Person(State.SUSCEPTIBLE, world);
            people.add(p);
        }
        //adding infected people to the mix
        Person p = new Person(State.INFECTED, world);
        people.add(p);
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    /**
     * move each person
     */
    public void move() {
        for (Person p: people) {
            p.move();
        }
    }

    /**
     * draw each person
     */
    public void draw() {
        for (Person p: people) {
            p.draw();
        }
    }

    /**
     * check for collision
     */
    public void collisionCheck() {
        for (Person p : people) {
            for (Person q : people) {
                p.collide(q, maskedPercentage, vaccinatedPercentage, RFactor, (SimulatorController.aRead.get("section", "MaskEffectiveness", double.class)),
                        (SimulatorController.aRead.get("section", "VaccineEfficacy", double.class)));
            }
        }
    }

    /**
     * heal each infected person
     */
    public void heal() {
        for (Person p: people) {
            p.getBetter();
        }
    }

    /**
     * check if person is in market
     */
    public void checkInMarket(){
        for(Person p: people){
            p.checkInMarket(people,maskedPercentage,vaccinatedPercentage, RFactor, (SimulatorController.aRead.get("section", "MaskEffectiveness", double.class)),
                    (SimulatorController.aRead.get("section", "VaccineEfficacy", double.class))
                    );
        }
    }

    /**
     * operation cycle on each person
     */
    public void step() {
        move();
        heal();
        collisionCheck();
        checkInMarket();
        draw();
    }

    /**
     * move infected people to quarantine pane
     * @param pane - simulation pane
     */
    public void moveToQuarantine(Pane pane){
        for(Person p: people){
            if(p.getState() == State.INFECTED){
                p.moveQuarantine(pane);
            }
        }
    }

    /**
     * move people to new community
     * @param pane - simulation pane
     */
    public void moveToNewCommunity(Pane pane){
        for(Person p: people){
            p.moveToNewCommunity(pane);
        }
    }

    /**
     * set mask to people
     * @param masked - masked population
     */
    public void setMask(int masked){
         //remove and set mask in case slider is changed
        if(people.size()>0){
            for(Person p: people){
                p.setMask(false);
            }

            for(int i=0; i<masked; i++){
                people.get(i).setMask(true);
            }
        }
    }

    /**
     * set percentage of masked personnel
     * @param count - percentage of people wearing masks
     */
    public void setSimulationMask(int count){
        maskedPercentage = count;
    }

    /**
     * set percentage of vaccinated personnel
     * @param count - set percentage of vaccinated personnel
     */
    public void setSimulationVaccinated(int count){
        vaccinatedPercentage = count;
    }

    /**
     * vaccinate individuals
     * @param count vaccinate individuals
     */
    public void vaccinatePeople(int count){
         //remove all from vaccinated and add again in case slider change midway through the simulation
        if(people.size() > 0){
            for(Person p: people){
                p.setVaccinated(false);
            }
            for(int i=0; i<count; i++){
                people.get(i).setVaccinated(true);
            }
        }
    }

    /**
     * set type of simulation -> used in case 2 where central location is involved
     * @param type set type of simulation -> used in case 2 where central location is involved
     */
    public void setSimulationType(int type){
        for(Person p: people){
            p.setSimulationType(type);
        }
    }

    /**
     * move people between new communities
     * @param factor move people between new communities
     */
    public void setMoveNewCommunity(double factor){
        for(Person p: people){
            p.setCommunityTravelFactor(factor);
        }
    }

    /**
     * read r factor from config file
     * @param ip read r factor from config file
     */
    public void setRFactor(String ip){
        this.RFactor = SimulatorController.aRead.get("section", ip, double.class);
    }

    private double RFactor;



}
