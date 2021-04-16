package simulator.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import simulator.gui.SimulatorController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Person {

    public static int radius;

    private State state;
    private Position loc;
    private Direction heading;
    private LocalDateTime timeStamp;
    public Pane getPane() {
        return pane;
    }

    public void setPane(Pane pane) {

    }

    public Pane pane;
    private Circle c;

    public static int healtime;
    private int sicktime = 0;

    private Position origin;

    public Person(State state, Pane pane) {
        this.state = state;
        loc = new Position(pane, radius);
        heading = new Direction(2);
        this.pane = pane;
        c = new Circle(radius, state.getColor());
        c.setStroke(Color.BLACK);
        pane.getChildren().add(c);

        origin = new Position(loc.getX(), loc.getY());
        this.timeStamp = LocalDateTime.now();
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

    public boolean collide(Person other) {
        if (this.loc.distance(other.loc) < 2 * radius) {
            /**
             * Change based on probability here
             */
            if (other.state == State.INFECTED && state == State.SUSCEPTIBLE) {
                setState(State.INFECTED);
                this.timeStamp = LocalDateTime.now();
            }
            return true;
        }
        return false;
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

        int temp = (int) ChronoUnit.SECONDS.between(this.timeStamp, now);
        
        if(ChronoUnit.SECONDS.between(this.timeStamp, now) >= 5){
            if(this.state == State.INFECTED){
                this.undraw();
                this.pane = pane;
                loc = new Position(pane, radius);
                heading = new Direction(0.05);

                c = new Circle(radius, state.getColor());
                c.setStroke(Color.BLACK);
                pane.getChildren().add(c);

                origin = new Position(loc.getX(), loc.getY());
                //this.draw();
            }

        }

    }
}
