package com.heshanthenura.nightsim;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;
import java.util.logging.*;

public class MainController implements Initializable {

    @FXML
    private AnchorPane background;
    @FXML
    private Canvas canvas;
    public GraphicsContext gc;
    Logger logger = Logger.getLogger("info");

    List<Star> starList = new ArrayList<>();
    List<Meteor> meteorList = new ArrayList<>();
    Stage stage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.setLevel(Level.ALL);
        Platform.runLater(() -> {

            stage = (Stage) background.getScene().getWindow();
            gc = canvas.getGraphicsContext2D();
            canvas.setFocusTraversable(true);
            canvas.setWidth(stage.getWidth());
            canvas.setHeight(stage.getHeight());

            drawStars();
            drawMeteors();
            backgroundListeners();

            new AnimationTimer() {

                @Override
                public void handle(long l) {
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    changeStars();
                    moveMeteors();
//                    new Meteor();
                }
            }.start();

        });


    }

    public void backgroundListeners() {
        background.widthProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                canvas.setWidth(newVal.doubleValue());
                logger.info(String.valueOf(newVal.doubleValue()));
            });

        });
        background.heightProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                canvas.setHeight(newVal.doubleValue());
                logger.info(String.valueOf(newVal.doubleValue()));
            });
        });
        canvas.setOnKeyPressed(e -> {
            logger.info(e.getCode().getName());
            switch (e.getCode().getName()) {
                case "Esc":
                    Platform.exit();
                    System.exit(0);
                case "Enter":
                    drawStars();
            }
        });
    }

    public void drawStars() {
        Platform.runLater(() -> {
            for (int i = 0; i < 2999; i++) {
                gc.setFill(Color.WHITE);
                int size = (int) (Math.random() * (3));
                double x = (Math.random() * (canvas.getWidth()));
                double y = (Math.random() * (canvas.getHeight()));
                double initTime = System.currentTimeMillis();
                double lifeTime = ((Math.random() * (10000 - 2000)) + 10000);
                Star star = new Star(x, y, size, new Random().nextBoolean(), initTime, lifeTime);
                star.draw();
                starList.add(star);
            }
        });
    }

    public void drawMeteors() {
            Meteor m = new Meteor();
            meteorList.add(m);
    }

    public void moveMeteors() {
        List<Meteor> meteorsToRemove = new ArrayList<>();
        List<Meteor> newMeteorsToAdd = new ArrayList<>(); // New list for new meteors

        for (Meteor m : meteorList) {
            if (m.startX < 0 || m.startX > canvas.getWidth() || m.startY < 0 || m.startY > canvas.getHeight()) {
                logger.info("Removed");
                meteorsToRemove.add(m);
                Meteor nM = new Meteor();
                newMeteorsToAdd.add(nM); // Add new meteors to the separate list
                logger.info("Added New");
            } else {
                m.move();
            }
        }

        meteorList.removeAll(meteorsToRemove); // Remove meteors

        // Add new meteors after the loop
        meteorList.addAll(newMeteorsToAdd);
    }



    public void changeStars() {
        for (Star s : starList) {
            s.updateSize();
            s.draw();
        }
    }

    class Star {

        private double x, y;
        private double size;
        private double actualSize;
        private boolean change;
        private double initTime;
        private double lifeTime;
        private boolean shrink = true;

        public Star(double x, double y, int actualSize, boolean change, double initTime, double lifeTime) {
            this.x = x;
            this.y = y;
            this.actualSize = actualSize;
            this.change = change;
            this.initTime = initTime;
            this.lifeTime = lifeTime;
            this.size = actualSize;
        }

        public void updateSize() {
            double elapsedTime = System.currentTimeMillis() - initTime;
            if (change) {
                if (shrink) {
                    size = actualSize - (elapsedTime / lifeTime) * actualSize;
                    if (size <= 0) {
                        size = 0;
                        shrink = false;
                        initTime = System.currentTimeMillis(); // Reset initTime for growing phase
                    }
                } else {
                    size = (elapsedTime / lifeTime) * actualSize;
                    if (size >= actualSize) {
                        size = actualSize;
                        shrink = true;
                        initTime = System.currentTimeMillis(); // Reset initTime for shrinking phase
                    }
                }
            }
        }

        public void draw() {
            gc.setFill(Color.WHITE);
            gc.fillOval(x, y, size, size);
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getSize() {
            return size;
        }

        public void setSize(double size) {
            this.size = size;
        }

        public boolean isChange() {
            return change;
        }

        public void setChange(boolean change) {
            this.change = change;
        }

        public double getInitTime() {
            return initTime;
        }

        public void setInitTime(double initTime) {
            this.initTime = initTime;
        }

        public double getLifeTime() {
            return lifeTime;
        }

        public void setLifeTime(double lifeTime) {
            this.lifeTime = lifeTime;
        }

        public boolean isShrink() {
            return shrink;
        }

        public void setShrink(boolean shrink) {
            this.shrink = shrink;
        }

        public double getActualSize() {
            return actualSize;
        }

        public void setActualSize(double actualSize) {
            this.actualSize = actualSize;
        }

    }

    class Meteor {

        private double startX;
        private double startY;
        private double angle;
        private double velocity;
        private double length;
        private double sleepTime;

        private double initTime;

        Stop[] stops = new Stop[]{
                new Stop(0, Color.rgb(0, 0, 0, 0)),
                new Stop(1, Color.rgb(255, 255, 255, 1))
        };

        public Meteor() {
            this.startX = Math.random() * canvas.getWidth();
            this.startY = Math.random() * canvas.getHeight();
            this.angle = ((Math.random() * (360 - 180)) + 180);
            this.velocity = (Math.random() * (20 - 10)) + 10;
            this.length = (Math.random() * 300) + 10;
            this.sleepTime=(Math.random()*(5000-1000));
            this.initTime=System.currentTimeMillis();
            logger.info(String.valueOf(angle));
            draw(startX, startY, angle, length);

        }
        public void draw(double sX, double sY, double angle, double length) {
            double endX = sX - (length * Math.cos(Math.toRadians(angle)));
            double endY = sY - (length * Math.sin(Math.toRadians(angle)));
            gc.setStroke(new LinearGradient(sX, sY, endX, endY, false, null, stops));
            gc.setLineWidth(1);
            gc.strokeLine(sX, sY, endX, endY);
        }
        public void move() {
            if ((System.currentTimeMillis()-initTime)>=sleepTime){
                startX -= (velocity * Math.cos(Math.toRadians(angle)));
                startY -= (velocity * Math.sin(Math.toRadians(angle)));
                draw(startX, startY, angle, length);
            }

        }
    }


}

