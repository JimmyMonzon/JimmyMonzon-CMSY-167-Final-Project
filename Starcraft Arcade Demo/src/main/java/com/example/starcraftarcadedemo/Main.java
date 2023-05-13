package com.example.starcraftarcadedemo;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Scanner;
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

        Image bgi;
        try {
            FileInputStream mapStream = new FileInputStream("map.Jpeg");
            bgi = new Image(mapStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        mainScene.setFill(new ImagePattern(bgi, 0, 0, 1, 1, true));

        Canvas canvas = new Canvas(512, 512);
        root.getChildren().add(canvas);

        ArrayList<String> input = new ArrayList<>();

        //lambdas

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


        GraphicsContext gc = canvas.getGraphicsContext2D();

        Font font = Font.font("Helvetica", FontWeight.SEMI_BOLD, 24);
        gc.setFont(font);
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        Image marineImg;
        Image droneImg;
        Image medicImg;

        //Collections

        ArrayList<Sprite> droneList = new ArrayList<>();
        ArrayList<Sprite> medicList = new ArrayList<>();


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

        new AnimationTimer() {
            long lastTime = ( System.nanoTime() );
            int score = 0;
            public void handle(long currentTime) {

                // calculate time since last update.
                double elapsedTime = (currentTime - lastTime) / 1000000000.0;
                lastTime = currentTime;

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
                        score++;
                    }
                }

                Iterator<Sprite> medicIter = medicList.iterator();
                while (medicIter.hasNext()) {
                    Sprite medic = medicIter.next();
                    if (marine.intersects(medic)) {
                        medicIter.remove();
                        score--;
                    }
                }

                // render

                gc.clearRect(0, 0, 512, 512);
                marine.render(gc);

                for (Sprite zergDrone : droneList)
                    zergDrone.render(gc);

                for (Sprite medic : medicList)
                    medic.render(gc);

                Path scoreFilePath = Paths.get("score.txt");

                try {
                    Scanner inputFileScanner = new Scanner(scoreFilePath);
                    String line = inputFileScanner.nextLine();

                    String pointsText = "record: " + (line);
                    gc.fillText(pointsText, 0, 36);
                    gc.strokeText(pointsText, 0, 36);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String pointsText = "Score: " + (100 * score);
                gc.fillText(pointsText, 360, 36);
                gc.strokeText(pointsText, 360, 36);

                try(Formatter frmt = new Formatter(scoreFilePath.toFile())) {
                    frmt.format(String.valueOf(score *100));

                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }

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
