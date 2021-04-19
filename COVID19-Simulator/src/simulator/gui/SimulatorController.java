package simulator.gui;


import javafx.scene.control.*;
import org.ini4j.Ini;
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
import java.io.FileReader;
import java.io.IOException;
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
    @FXML
    ComboBox selectedDisease;

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


    public static Random random = new Random();
    //used for timing the simulation
    private Movement clock;

    public static Ini aRead;
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
    public void initialize() throws IOException {
        aRead = new Ini();
        aRead.load(new FileReader("src/simulator/gui/setting.ini"));

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
                setSimulationMask();
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
                setNewCommunity();
            }
        });
        selectedDisease.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                setRFactor();
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
        quarantine.getChildren().clear();


        switch (tabPane.getSelectionModel().getSelectedIndex()){
            case 0:
                world.getChildren().clear();
                sim = new Simulation(100, world);
                sim.setSimulationType(1);
                sim.setRFactor(setRFactor() == "" ? "Covid19" : setRFactor());
                sim.draw();

                break;
            case 1:
                centralLocation.getChildren().clear();
                centralLocation.getChildren().add(market);
                centralLocation.getChildren().add(marketLabel);
                simMarket = new Simulation(100, centralLocation);
                simMarket.setSimulationType(2);
                simMarket.setRFactor(setRFactor() == "" ? "Covid19" : setRFactor());
                simMarket.draw();
                break;
            case 2:
                community1.getChildren().clear();
                community2.getChildren().clear();
                community3.getChildren().clear();
                community4.getChildren().clear();
                simCommunity1 = new Simulation(5, community1);
                simCommunity1.setRFactor(setRFactor() == "" ? "Covid19" : setRFactor());
                simCommunity1.draw();

                simCommunity2 = new Simulation(10, community2);
                simCommunity2.setRFactor(setRFactor() == "" ? "Covid19" : setRFactor());
                simCommunity2.draw();
                simCommunity3 = new Simulation(5, community3);
                simCommunity3.setRFactor(setRFactor() == "" ? "Covid19" : setRFactor());
                simCommunity3.draw();
                simCommunity4 = new Simulation(15, community4);
                simCommunity4.setRFactor(setRFactor() == "" ? "Covid19" : setRFactor());
                simCommunity4.draw();
                setNewCommunity();
                break;
        }
        setSize();
        setSocialDistancingLimit();
        setSickTime();
        setMask();
        setVaccinated();
        setSimulationMask();
        setSimulationVaccine();
       
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

    /**
     * set vaccination rate in simulation
     */
    public void setSimulationVaccine(){
        int temp = (int)(vaccinatedSlider.getValue());
        switch(tabPane.getSelectionModel().getSelectedIndex())
        {
            case 0:
                sim.setSimulationVaccinated(temp);
                break;
            case 1:
                simMarket.setSimulationVaccinated(temp);
                break;
            case 2:
                simCommunity1.setSimulationVaccinated(temp);
                simCommunity2.setSimulationVaccinated(temp);
                simCommunity3.setSimulationVaccinated(temp);
                simCommunity4.setSimulationVaccinated(temp);
                break;
        }
    }

    /**
     * set percentage of people wearing masks in simulation
     */
    public void setSimulationMask(){
        int temp = (int)(maskSlider.getValue());
        switch(tabPane.getSelectionModel().getSelectedIndex())
        {
            case 0:
                sim.setSimulationMask(temp);
                break;
            case 1:
                simMarket.setSimulationMask(temp);
                break;
            case 2:
                simCommunity1.setSimulationMask(temp);
                simCommunity2.setSimulationMask(temp);
                simCommunity3.setSimulationMask(temp);
                simCommunity4.setSimulationMask(temp);
                break;

        }
    }

    /**
     * set mask to person in simulator class
     */
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

    /**
     * set size of people in simulation
     */
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

    /**
     * toggling between quarantine zone
     */
    public void disableQuarantine(){
        if(enableQuarantine.isSelected()){
            quarantineSlider.setDisable(false);
        }
        else {
            quarantineSlider.setDisable(true);
        }
    }

    /**
     * set quarantine after how many days
     */
    public void setQuarantine(){
        quarantineTime = (int)(quarantineSlider.getValue());
    }

    /**
     * move to new community - only applicable to category 3
     */
    public void setNewCommunity(){

        switch(tabPane.getSelectionModel().getSelectedIndex()) {
            case 2:
                simCommunity1.setMoveNewCommunity((double) (communityTravelSlider.getValue()));
                simCommunity2.setMoveNewCommunity((double) (communityTravelSlider.getValue()));
                simCommunity3.setMoveNewCommunity((double) (communityTravelSlider.getValue()));
                simCommunity4.setMoveNewCommunity((double) (communityTravelSlider.getValue()));
                break;
        }
    }

    /**
     * set degree of social distancing
     */
    public void setSocialDistancingLimit(){
        double temp = (double)(socialDistancingSlider.getValue());
        System.out.println(temp);
        if(temp == 0){
            temp = 0.01;
        }

        temp = 1 - temp;
        temp *= 200;

        Position.limit = (int) temp;
    }

    /**
     * set vaccinated percentage
     */
    public void setVaccinated(){
        int temp = (int)(vaccinatedSlider.getValue());
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

    /**
     * set time to recovery of a person
     */
    public void setSickTime() {
        Person.healTime = 50 * (int)(sickTimeSlider.getValue());
    }

    /**
     *
     * @param stop stop sim
     * @param step take 1 step in sim
     * @param start start sim
     */
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

    /**
     * draw charts
     */
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

    /**
     * draw charts for selected simulation type
     * @param s simulation type
     */
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

    /**
     * move people to quarantine once it is enabled
     */
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

    /**
     * move to new community - in simulation 2
     */
    public void moveToNewCommunity(){
        switch(tabPane.getSelectionModel().getSelectedIndex()){
            case 2:
                simCommunity1.moveToNewCommunity(community2);
                simCommunity2.moveToNewCommunity(community3);
                simCommunity3.moveToNewCommunity(community4);
                simCommunity4.moveToNewCommunity(community1);
                break;
        }
    }

    /**
     *
     * @return r factor of selected disease
     */
    public String setRFactor(){
            return (String) selectedDisease.getValue();
    }



}
