package simulator.model;

import javafx.scene.layout.Pane;
import simulator.gui.SimulatorController;

import java.util.ArrayList;

public class Simulation {

    public ArrayList<Person> people;

    public Simulation(int popCount, Pane world) {
        people = new ArrayList<Person>();
        for (int i = 0; i < popCount; i++) {
            Person p = new Person(State.SUSCEPTIBLE, world);

            people.add(p);
        }

        Person p = new Person(State.INFECTED, world);
        people.add(p);
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public void move() {
        for (Person p: people) {
            p.move();
        }
    }
    public void draw() {
        for (Person p: people) {
            p.draw();
        }
    }

    public void collisionCheck() {
        for (Person p : people) {
            for (Person q : people) {
                p.collide(q);
            }
        }
    }

    public void heal() {
        for (Person p: people) {
            p.getBetter();
        }
    }

    public void checkInMarket(){
        for(Person p: people){
            p.checkInMarket(people);
        }
    }

    public void step() {
        move();
        heal();
        collisionCheck();
        checkInMarket();
        draw();

    }

    public void moveToQuarantine(Pane pane){
        for(Person p: people){
            if(p.getState() == State.INFECTED){
                p.moveQuarantine(pane);
            }
        }
    }

    public void moveToNewCommunity(Pane pane){
        for(Person p: people){
           // p.setCommunityTravelFactor(chance);
            p.moveToNewCommunity(pane);
        }
    }

    public void setMask(int masked){
        if(people.size()>0){
            for(Person p: people){
                p.setMask(false);
            }

            for(int i=0; i<masked; i++){
                people.get(i).setMask(true);
            }
        }
    }


    public void vaccinatePeople(int count){
        if(people.size() > 0){
            for(Person p: people){
                p.setVaccinated(false);
            }
            for(int i=0; i<count; i++){
                people.get(i).setVaccinated(true);
            }
        }
    }

    public void setSimulationType(int type){
        for(Person p: people){
            p.setSimulationType(type);
        }
    }
    
     public void setMoveNewCommunity(double factor){
        for(Person p: people){
            p.setCommunityTravelFactor(factor);
        }
    }
}
