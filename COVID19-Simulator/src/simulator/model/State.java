package simulator.model;

import javafx.scene.paint.Color;

public enum State {
    SUSCEPTIBLE {
        public Color getColor() {
            return Color.BLUE;
        }
    }, INFECTED {
        public Color getColor() {
            return Color.RED;
        }
    }, RECOVERED {
        public Color getColor() {
            return Color.DARKGRAY;
        }
    }, ASYMPTOMATIC{
       public Color getColor(){return Color.YELLOW;}
    };

    public abstract Color getColor();
}
