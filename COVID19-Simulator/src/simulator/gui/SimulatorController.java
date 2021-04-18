package simulator.gui;

import javafx.scene.control.*;
import simulator.model.*;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.EnumMap;
import java.util.Random;

public class SimulatorController {

    @FXML
    Pane community1;
    @FXML
    Pane community2;
    @FXML
    Pane community3;
    @FXML
    Pane community4;
    @FXML
    Label marketLabel;
    @FXML
    Pane quarantine;
    @FXML
    Pane world;
    @FXML
    Pane centralLocation;
    @FXML
    Rectangle market;
    @FXML
    Pane histogram;
    @FXML
    Pane timechart;
    @FXML
    Button startButton;
    @FXML
    Button stopButton;
    @FXML
    Button resetButton;
    @FXML
    Button stepButton;
    @FXML
    Slider sizeSlider;
    @FXML
    Slider sickTimeSlider;
    @FXML
    TextField stepCount;
    @FXML
    Slider socialDistancingSlider;
    @FXML
    Slider quarantineSlider;
    @FXML
    Slider maskSlider;
    @FXML
    Slider vaccinatedSlider;
    @FXML
    CheckBox enableQuarantine;
    @FXML
    TabPane tabPane;
    @FXML
    Slider communityTravelSlider;

    //Standard simulation
    Simulation sim;

    //Market Simulation
    Simulation simMarket;


    //Community Simulation
    Simulation simCommunity1;
    Simulation simCommunity2;
    Simulation simCommunity3;
    Simulation simCommunity4;

    int simulationType;

    //Used for graphs
    EnumMap<State, Rectangle> hrects = new EnumMap<State, Rectangle>(State.class);

    //Quarantine time -> used in person class
    public static int quarantineTime;

    //used for timing the simulation
    private Movement clock;
    //Animation
    private class Movement extends AnimationTimer {

        private long FRAMES_PER_SEC = 50L;
        private long INTERVAL = 1000000000L / FRAMES_PER_SEC;

        private long last = 0;
        private int ticks = 0;

        @Override
        public void handle(long now) {
            if (now - last > INTERVAL) {
                step();
                drawCharts();
                last = now;
                ticks++;
                stepCount.setText("" + ticks);
                moveQuarantine();
                moveToNewCommunity();
            }
        }

        public void resetTicks() {
            ticks = 0;
            stepCount.setText("" + ticks);
        }

        public int getTicks() {
            return ticks;
        }
    }

    /**
     * Initializing the javafx tools
     */
    @FXML
    public void initialize() {

        sizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setSize();
            }
        });
        sickTimeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setSickTime();
            }
        });

        socialDistancingSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setSocialDistancingLimit();
            }
        });
        quarantineSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setQuarantine();

            }
        });
        maskSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setMask();
            }
        });
        vaccinatedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setVaccinated();
            }
        });
        enableQuarantine.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                disableQuarantine();
            }
        });
        communityTravelSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                moveToNewCommunity();
            }
        });
        //new instance of timer
        clock = new Movement();
        disableButtons(true, true, true);

        disableQuarantine();
        world.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        centralLocation.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
    }

    @FXML
    public void setup() {
        clock.stop();
        clock.resetTicks();
        //world.getChildren().clear();
        quarantine.getChildren().clear();


        switch (tabPane.getSelectionModel().getSelectedIndex()){
            case 0:
                world.getChildren().clear();
                sim = new Simulation(100, world);
                sim.setSimulationType(1);
                sim.draw();
                break;
            case 1:
                centralLocation.getChildren().clear();
                centralLocation.getChildren().add(market);
                centralLocation.getChildren().add(marketLabel);
                simMarket = new Simulation(100, centralLocation);
                simMarket.setSimulationType(2);
                simMarket.draw();
                break;
            case 2:
                community1.getChildren().clear();
                community2.getChildren().clear();
                community3.getChildren().clear();
                community4.getChildren().clear();

                simCommunity1 = new Simulation(5, community1);
                simCommunity1.draw();
                simCommunity2 = new Simulation(5, community2);
                simCommunity2.draw();
                simCommunity3 = new Simulation(5, community3);
                simCommunity3.draw();
                simCommunity4 = new Simulation(5, community4);
                simCommunity4.draw();
                break;
        }
        setSize();
        setSocialDistancingLimit();
        setSickTime();
        setMask();
        setVaccinated();
        disableButtons(true, false, false);
        disableQuarantine();
        histogram.getChildren().clear();
        timechart.getChildren().clear();
        int offset = 0;
        for (State s : State.values()) {
            Rectangle r = new Rectangle(50, 0, s.getColor());
            r.setTranslateX(offset);
            offset += 55;
            hrects.put(s, r);
            histogram.getChildren().add(r);
        }
        drawCharts();
    }



    public void setMask(){

        int temp = (int)(maskSlider.getValue());
        switch(tabPane.getSelectionModel().getSelectedIndex())
        {
            case 0:
                sim.setMask(temp);
                break;
            case 1:
                simMarket.setMask(temp);
                break;
            case 2:
                simCommunity1.setMask(temp);
                simCommunity2.setMask(temp);
                simCommunity3.setMask(temp);
                simCommunity4.setMask(temp);
                break;

        }
    }

    public void setSize() {
        switch(tabPane.getSelectionModel().getSelectedIndex())
        {
            case 0:
                Person.radius = (int) (sizeSlider.getValue());
                sim.draw();
                break;
            case 1:
                Person.radius = (int) (sizeSlider.getValue());
                simMarket.draw();
                break;
            case 2:
                Person.radius = (int) (sizeSlider.getValue());
                simCommunity1.draw();
                simCommunity2.draw();
                simCommunity3.draw();
                simCommunity4.draw();
                break;

        }
    }

    public void disableQuarantine(){
        if(enableQuarantine.isSelected()){
            quarantineSlider.setDisable(false);
        }
        else {
            quarantineSlider.setDisable(true);
        }
    }


    public void setQuarantine(){
        quarantineTime = (int)(quarantineSlider.getValue());
    }
    public void setSocialDistancingLimit(){
        double temp = (double)(socialDistancingSlider.getValue());
        System.out.println(temp);
        if(temp == 0){
            temp = 0.01;
        }
        if(temp == 1){

        }
        temp = 1 - temp;
        temp *= 200;

        Position.limit = (int) temp;
    }

    public void setVaccinated(){
        int temp = (int)(vaccinatedSlider.getValue());
//        sim.vaccinatePeople(temp);

        switch(tabPane.getSelectionModel().getSelectedIndex())
        {
            case 0:
                sim.vaccinatePeople(temp);
                break;
            case 1:
                simMarket.vaccinatePeople(temp);
                break;
            case 2:
                simCommunity1.vaccinatePeople(temp);
                simCommunity2.vaccinatePeople(temp);
                simCommunity3.vaccinatePeople(temp);
                simCommunity4.vaccinatePeople(temp);
                break;

        }
    }
    public void setSickTime() {
        Person.healtime = 50 * (int)(sickTimeSlider.getValue());
    }


    public void disableButtons(boolean stop, boolean step, boolean start) {
        stopButton.setDisable(stop);
        stepButton.setDisable(step);
        startButton.setDisable(start);
    }

    @FXML
    public void start() {
        System.out.println("Starting Simulation");
        clock.start();
        disableButtons(false, true, true);
    }

    @FXML
    public void stop() {
        System.out.println("Stopping!");
        clock.stop();
        disableButtons(true, false, false);
    }

    @FXML
    public void step() {
        switch(tabPane.getSelectionModel().getSelectedIndex())
        {
            case 0:
                sim.step();
                break;
            case 1:
                simMarket.step();
                break;
            case 2:
                simCommunity1.step();
                simCommunity2.step();
                simCommunity3.step();
                simCommunity4.step();
                break;

        }
    }

    public void drawCharts() {

        switch(tabPane.getSelectionModel().getSelectedIndex())
        {
            case 0:
                drawChartDriver(sim);
                break;
            case 1:
                drawChartDriver(simMarket);
                break;
            case 2:
                //drawChartDriver();
                break;

        }
    }

    public void drawChartDriver(Simulation s){
        EnumMap<State, Integer> currentPop = new EnumMap<State, Integer>(State.class);
        for (Person p : s.getPeople()) {
            if (!currentPop.containsKey(p.getState())) {
                currentPop.put(p.getState(), 0);
            }
            currentPop.put(p.getState(), 1 + currentPop.get(p.getState()));
        }
        for (State state : hrects.keySet()) {
            if (currentPop.containsKey(state)) {
                hrects.get(state).setHeight(currentPop.get(state));
                hrects.get(state).setTranslateY(30 + 100 - currentPop.get(state));

                Circle c = new Circle(1, state.getColor());
                c.setTranslateX(clock.getTicks() / 5.0);
                c.setTranslateY(130 - currentPop.get(state));
                timechart.getChildren().add(c);
            }
        }
        if (!currentPop.containsKey(State.INFECTED)) {
            clock.stop();
            disableButtons(true, true, true);
        }
    }

    public void moveQuarantine(){
        if(enableQuarantine.isSelected()) {
            switch(tabPane.getSelectionModel().getSelectedIndex())
            {
                case 0:
                    sim.moveToQuarantine(quarantine);
                    break;
                case 1:
                    simMarket.moveToQuarantine(quarantine);
                    break;
                case 2:
                    simCommunity1.moveToQuarantine(quarantine);
                    simCommunity2.moveToQuarantine(quarantine);
                    simCommunity3.moveToQuarantine(quarantine);
                    simCommunity4.moveToQuarantine(quarantine);
                    break;

            }
        }
    }

    public void moveToNewCommunity(){
        //Random rand = new Random();
        switch(tabPane.getSelectionModel().getSelectedIndex()){
            case 2:
                int temp = (int) communityTravelSlider.getValue();
                //int random = rand.nextInt(4);
                simCommunity1.moveToNewCommunity(community2, temp);
                simCommunity2.moveToNewCommunity(community3, temp);
                simCommunity3.moveToNewCommunity(community4, temp);
                simCommunity4.moveToNewCommunity(community1, temp);
                break;
        }
    }



}
