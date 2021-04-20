/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.model;

import javafx.scene.layout.Pane;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Kunjan
 */
public class PositionTest {
    
    public PositionTest() {
    }
    /**
     * Test of move method, of class Position.
     */
    @Test
    public void testMove() {
        Direction h = new Direction(2);
        Pane p = new Pane();
        int radius = 1;
        Position origin = new Position(1,1);
        Position instance = new Position(2,2);
        instance.move(h, p, radius, origin);
        assertEquals(2.0, instance.getX(),0.1);
        assertEquals(2.0, instance.getY(),0.1);   
    }

    /**
     * Test of distance method, of class Position.
     */
    @Test
    public void testDistance() {
        System.out.println("distance");
        Position other = new Position(1,1);
        Position instance = new Position(2,2);
        double expResult = 1.414;
        double result = instance.distance(other);
        assertEquals(expResult, result, 0.1);
       
    }
    
}
