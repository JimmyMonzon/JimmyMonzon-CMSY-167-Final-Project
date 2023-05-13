package com.example.starcraftarcadedemo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
//leveraging external libraries, JavaFx

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Exterminate the Zerg!");

        Group root = new Group();
        Scene mainScene = new Scene(root);
        stage.setScene(mainScene);

        mainScene.setFill(new RadialGradient(
                0, 0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#81c483")),
                new Stop(1, Color.web("#fcc200"))));

        Canvas canvas = new Canvas(512, 512);
        root.getChildren().add(canvas);

        ArrayList<String> input = new ArrayList<>();

        mainScene.setOnKeyPressed(
                e -> {
                    String code = e.getCode().toString();
                    if ( !input.contains(code) )
                        input.add( code );
                });

        mainScene.setOnKeyReleased(
                e -> {
                    String code = e.getCode().toString();
                    input.remove( code );
                });
        //lambdas

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Font font = Font.font("Helvetica", FontWeight.SEMI_BOLD, 24);
        gc.setFont(font);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        Image marineImg;
        Image droneImg;
        Image medicImg;

        ArrayList<Sprite> droneList = new ArrayList<>();
        ArrayList<Sprite> medicList = new ArrayList<>();
        //Collections

        try {
            FileInputStream marineStream = new FileInputStream("marine.Png");
            marineImg = new Image(marineStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            FileInputStream droneStream = new FileInputStream("zerg drone.Png");
            droneImg = new Image(droneStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            FileInputStream medicStream = new FileInputStream("medic.Png");
            medicImg = new Image(medicStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Sprite marine = new Sprite();
        marine.setImage(marineImg);
        marine.setPosition(200, 0);

        for (int i = 0; i < 15; i++) {
            createNPCs(droneImg, droneList);
        }

        for (int  a= 0; a < 5; a++) {
            createNPCs(medicImg, medicList);
        }

        LongValue lastTime = new LongValue( System.nanoTime() );
        IntValue score = new IntValue(0);

        new AnimationTimer() {
            public void handle(long currentTime) {

                // calculate time since last update.
                double elapsedTime = (currentTime - lastTime.value) / 1000000000.0;
                lastTime.value = currentTime;

                // game logic

                marine.setVelocity(0, 0);
                if (input.contains("LEFT"))
                    marine.addVelocity(-50, 0);
                if (input.contains("RIGHT"))
                    marine.addVelocity(50, 0);
                if (input.contains("UP"))
                    marine.addVelocity(0, -50);
                if (input.contains("DOWN"))
                    marine.addVelocity(0, 50);

                marine.update(elapsedTime);

                // collision detection

                Iterator<Sprite> droneIter = droneList.iterator();
                while (droneIter.hasNext()) {
                    Sprite zergDrone = droneIter.next();
                    if (marine.intersects(zergDrone)) {
                        droneIter.remove();
                        score.value++;
                    }
                }

                Iterator<Sprite> medicIter = medicList.iterator();
                while (medicIter.hasNext()) {
                    Sprite medic = medicIter.next();
                    if (marine.intersects(medic)) {
                        medicIter.remove();
                        score.value--;
                    }
                }

                // render

                gc.clearRect(0, 0, 512, 512);
                marine.render(gc);

                for (Sprite zergDrone : droneList)
                    zergDrone.render(gc);

                for (Sprite medic : medicList)
                    medic.render(gc);

                String pointsText = "Score: " + (100 * score.value);
                gc.fillText(pointsText, 360, 36);
                gc.strokeText(pointsText, 360, 36);
            }
        }.start();

        stage.show();
    }

    private void createNPCs(Image npcImg, ArrayList<Sprite> npcList) {
        Sprite npc = new Sprite();
        npc.setImage(npcImg);
        double x = 350 * Math.random() + 50;
        double y = 350 * Math.random() + 50;
        npc.setPosition(x, y);
        npcList.add(npc);
    }
}
