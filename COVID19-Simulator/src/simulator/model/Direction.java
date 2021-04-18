package simulator.model;

public class Direction {

    private double dx;
    private double dy;
    public double speed;

    public Direction(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction() {
        double dir = Math.random() * 2 * Math.PI;
        dx = Math.sin(dir);
        dy = Math.cos(dir);
    }
     public Direction(double speed) {
        this.speed = speed;
        double dir = Math.random() * 2 * Math.PI;
        dx = Math.sin(dir);
        dy = Math.cos(dir);
    }
    

    public double getDX(){
        return dx * speed;
    }

    public double getDY() {
        return dy * speed;
    }

    public void bounceX() {
        dx *= -1;
    }

    public void bounceY() {
        dy *= -1;
    }
}
