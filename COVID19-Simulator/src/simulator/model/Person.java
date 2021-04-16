package simulator.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Person {

    public static int radius;

    private State state;
    private Position loc;
    private Direction heading;
    private Pane pane;
    private Circle c;
    private Pane quarantine;

    public static int healtime;
    private int sicktime = 0;

    private Position origin;

    public Person(State state, Pane pane,Pane quarantine) {
        this.state = state;
        loc = new Position(pane, radius);
        heading = new Direction();
        this.pane = pane;
        this.quarantine = quarantine;
        
        c = new Circle(radius, state.getColor());
        c.setStroke(Color.BLACK);
        pane.getChildren().add(c);

        origin = new Position(loc.getX(), loc.getY());
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
            if (other.state == State.INFECTED && state == State.SUSCEPTIBLE) {
                setState(State.QUARANTINED);
                 this.state = State.QUARANTINED;
        this.loc = new Position(quarantine, radius);
        this.heading = new Direction();
        this.pane=quarantine;
        undraw();
        c = new Circle(radius, state.getColor());
        c.setStroke(Color.BLACK);
        this.pane.getChildren().add(c);

        origin = new Position(loc.getX(), loc.getY());
               
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
    

    public Pane getPane() {
        return pane;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public static int getRadius() {
        return radius;
    }

    public static void setRadius(int radius) {
        Person.radius = radius;
    }

    public Position getLoc() {
        return loc;
    }

    public void setLoc(Position loc) {
        this.loc = loc;
    }

    public Direction getHeading() {
        return heading;
    }

    public void setHeading(Direction heading) {
        this.heading = heading;
    }

    public Circle getC() {
        return c;
    }

    public void setC(Circle c) {
        this.c = c;
    }

    public static int getHealtime() {
        return healtime;
    }

    public static void setHealtime(int healtime) {
        Person.healtime = healtime;
    }

    public int getSicktime() {
        return sicktime;
    }

    public void setSicktime(int sicktime) {
        this.sicktime = sicktime;
    }

    public Position getOrigin() {
        return origin;
    }

    public void setOrigin(Position origin) {
        this.origin = origin;
    }

    
}
