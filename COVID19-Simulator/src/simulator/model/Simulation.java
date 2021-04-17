package simulator.model;

import javafx.scene.layout.Pane;
import simulator.gui.SimulatorController;

import java.util.ArrayList;

public class Simulation {

    public static ArrayList<Person> people;

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

    public void step() {
        move();
        heal();
        collisionCheck();
        draw();
    }

    public void moveToQuarantine(Pane pane){
        for(Person p: people){
            if(p.getState() == State.INFECTED){
                p.moveQuarantine(pane);
            }
        }
    }

    public void setMask(int masked){
        int temp = people.size() - 1;
        temp = masked * temp/100;
        for(int i =0; i<temp; i++){
            people.get(i).mask = true;
        }
    }


}
