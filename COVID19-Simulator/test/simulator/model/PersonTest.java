/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.model;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.Pane;
import org.ini4j.Ini;
import org.junit.Test;
import static org.junit.Assert.*;
import static simulator.gui.SimulatorController.aRead;

/**
 *
 * @author Kunjan
 */
public class PersonTest {

    Pane pane;
    double covid19Rfactor;
    double mersRfactor;
    double maskEffectiveness;
    int radius;
    double vaccineEfficacy;
    int highmaskedPercentage = 80;
    int highvaccinatedPercentage = 80;
    int avgmaskedPercentage = 50;
    int avgvaccinatedPercentage = 60;
    int lowmaskedPercentage = 30;
    int lowvaccinatedPercentage = 30;

    /**
     * Set Up
     */
    public PersonTest() {
        pane = new Pane();
        aRead = new Ini();
        radius = 1;
        try {
            aRead.load(new FileReader("src/simulator/gui/setting.ini"));
            covid19Rfactor = aRead.get("section", "Covid19", double.class);
            mersRfactor = aRead.get("section", "Mers", double.class);
            maskEffectiveness = aRead.get("section", "MaskEffectiveness", double.class);
            vaccineEfficacy = aRead.get("section", "VaccineEfficacy", double.class);
        } catch (IOException ex) {
            Logger.getLogger(PersonTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Test of checkInMarket method  of class Person for Low MaskFactor And VaccinatedFactor
     */
    @Test
    public void testCheckInMarketForLowMaskFactorAndVaccinatedFactor() {
        ArrayList<Person> ipList = getPersonList();
        ipList.get(0).checkInMarket(ipList, lowmaskedPercentage, lowvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(State.INFECTED, ipList.get(0).getState());
        assertEquals(1, ipList.get(2).getOthersInfected());
        ipList.get(1).checkInMarket(ipList, lowmaskedPercentage, lowvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(State.INFECTED, ipList.get(1).getState());
        assertEquals(1, ipList.get(0).getOthersInfected());

    }

    /**
     * Test of checkInMarket method  of class Person for Avg MaskFactor And VaccinatedFactor
     */
    @Test
    public void testCheckInMarketForAvgMaskFactorAndVaccinatedFactor() {
        ArrayList<Person> ipList = getPersonList();
        ipList.get(0).checkInMarket(ipList, avgmaskedPercentage, avgvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(State.INFECTED, ipList.get(0).getState());
        assertEquals(1, ipList.get(2).getOthersInfected());
        ipList.get(1).checkInMarket(ipList, avgmaskedPercentage, avgvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(1, ipList.get(0).getOthersInfected());
        ipList.get(3).checkInMarket(ipList, avgmaskedPercentage, avgvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(State.INFECTED, ipList.get(3).getState());
        assertEquals(1, ipList.get(1).getOthersInfected());
    }

    /**
     *Test of checkInMarket method  of class Person for High MaskFactor And VaccinatedFactor
     */
    @Test
    public void testCheckInMarketForHighMaskFactorAndVaccinatedFactor() {
        ArrayList<Person> ipList = getPersonList();
        ipList.get(0).checkInMarket(ipList, highmaskedPercentage, highvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(State.SUSCEPTIBLE, ipList.get(0).getState());
        assertEquals(0, ipList.get(2).getOthersInfected());
        ipList.get(1).checkInMarket(ipList, highmaskedPercentage, highvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(State.SUSCEPTIBLE, ipList.get(1).getState());
        assertEquals(0, ipList.get(2).getOthersInfected());
        ipList.get(3).checkInMarket(ipList, highmaskedPercentage, highvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(State.SUSCEPTIBLE, ipList.get(3).getState());
        assertEquals(0, ipList.get(2).getOthersInfected());
    }

    /**
     * Test of collide method, of class Person for High Mask And VaccinatedFactor
     */
    @Test
    public void testCollideForHighMaskAndVaccinatedFactor() {
        Person other = new Person(State.INFECTED, pane);
        other.setLoc(new Position(10, 10));
        other.radius = radius;

        Person instance = new Person(State.SUSCEPTIBLE, pane);
        instance.radius = radius;
        instance.setLoc(new Position(11, 11));

        Person instance1 = new Person(State.SUSCEPTIBLE, pane);
        instance1.radius = radius;
        instance1.setLoc(new Position(11, 10));

        instance.collide(other, highmaskedPercentage, highvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(State.SUSCEPTIBLE, instance.getState());
        assertEquals(0, other.getOthersInfected());
        instance1.collide(other, highmaskedPercentage, highvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(0, other.getOthersInfected());
        assertEquals(State.SUSCEPTIBLE, instance1.getState());

    }

    /**
     * Test of collide method, of class Person for Avg Mask And VaccinatedFactor
     */
    @Test
    public void testCollideForAvgMaskAndVaccinatedFactor() {

        Person other = new Person(State.INFECTED, pane);
        other.setLoc(new Position(10, 10));
        other.radius = radius;

        Person instance = new Person(State.SUSCEPTIBLE, pane);
        instance.radius = radius;
        instance.setLoc(new Position(11, 11));

        Person instance1 = new Person(State.SUSCEPTIBLE, pane);
        instance1.radius = radius;
        instance1.setLoc(new Position(11, 10));

        instance.collide(other, avgmaskedPercentage, avgvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(State.INFECTED, instance.getState());
        assertEquals(1, other.getOthersInfected());
        instance1.collide(other, avgmaskedPercentage, avgvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(1, other.getOthersInfected());
        assertEquals(State.SUSCEPTIBLE, instance1.getState());

    }

    /**
     *Test of collide method, of class Person for Low Mask And VaccinatedFactor
     */
    @Test
    public void testCollideForLowMaskFactorAndVaccinatedFactor() {
        Person other = new Person(State.INFECTED, pane);
        other.setLoc(new Position(10, 10));
        other.radius = radius;
        Person instance = new Person(State.SUSCEPTIBLE, pane);
        instance.radius = radius;
        instance.setLoc(new Position(11, 11));
        Person instance1 = new Person(State.SUSCEPTIBLE, pane);
        instance1.radius = radius;
        instance1.setLoc(new Position(11, 10));
        instance.collide(other, lowmaskedPercentage, lowvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(State.INFECTED, instance.getState());
        assertEquals(1, other.getOthersInfected());
        instance1.collide(other, lowmaskedPercentage, lowvaccinatedPercentage, covid19Rfactor, maskEffectiveness, vaccineEfficacy);
        assertEquals(2, other.getOthersInfected());
        assertEquals(State.INFECTED, instance1.getState());

    }

    /**
     * Test of getBetter method, of class Person.
     */
    @Test
    public void testGetBetter() {
        System.out.println("getBetter");
        Person instance = new Person(State.INFECTED, pane);
        instance.setSickTime(2);
        instance.healTime = 3;
        instance.getBetter();
        instance.getBetter();
        assertEquals(State.RECOVERED, instance.getState());

    }

    /**
     * Test of moveQuarantine method, of class Person.
     */
    @Test
    public void testMoveQuarantineDriver() {
        System.out.println("moveQuarantine");
        Pane pane1 = new Pane();
        pane1.setId("quarantine");
        Person instance = new Person(State.INFECTED, pane);
        instance.radius = radius;
        instance.moveQuarantineDriver(pane1);
        assertEquals("quarantine", instance.pane.getId());
        // TODO review the generated test code and remove the default call to fail.
        //   fail("The test case is a prototype.");
    }

    /**
     * Test of moveToNewCommunity method, of class Person.
     */
    @Test
    public void testMoveToNewCommunity() {
        System.out.println("moveToNewCommunity");
        Pane pane1 = new Pane();
        pane1.setId("community");
        Person p = new Person(State.SUSCEPTIBLE, pane);
        p.radius = radius;
        p.setLoc(new Position(210, 250));
        p.setCommunityTravelFactor(1);
        p.moveToNewCommunity(pane1);
        assertEquals("community", p.pane.getId());

    }

    /**
     *
     * @return
     */
    public ArrayList<Person> getPersonList() {

        ArrayList<Person> ipList = new ArrayList<>();
        Person p = new Person(State.SUSCEPTIBLE, pane);
        p.radius = radius;
        p.setLoc(new Position(210, 250));
        p.setSimulationType(2);
        ipList.add(p);
        Person p1 = new Person(State.SUSCEPTIBLE, pane);
        p1.radius = radius;
        p1.setLoc(new Position(215, 275));
        p1.setSimulationType(2);
        ipList.add(p1);
        Person p2 = new Person(State.INFECTED, pane);
        p2.radius = radius;
        p2.setLoc(new Position(300, 300));
        p2.setSimulationType(2);
        ipList.add(p2);
        Person p3 = new Person(State.SUSCEPTIBLE, pane);
        p3.radius = radius;
        p3.setLoc(new Position(290, 302));
        p3.setSimulationType(2);
        ipList.add(p3);

        return ipList;
    }
}
