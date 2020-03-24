import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Exercise_32_01 extends Application {
    private String text = "";
    Lock lock = new ReentrantLock();

    @Override
    public void start(Stage stage) throws Exception {
        // Create tasks
        Runnable printA = new PrintChar('a', 100);
        Runnable printB = new PrintChar('b', 100);
        Runnable print100 = new PrintNum(100);

        // Create threads
        Thread thread1 = new Thread(printA);
        Thread thread2 = new Thread(printB);
        Thread thread3 = new Thread(print100);

        // Start threads
        thread1.start();
        thread2.start();
        thread3.start();

        VBox vbox = new VBox(); // Using a VBox makes the text area fit to the scene width
        TextArea textArea = new TextArea(text);
        textArea.wrapTextProperty().setValue(true); // Needed to make the text stay inside the text area
        vbox.getChildren().add(textArea);
        Scene scene = new Scene(vbox, 400, 150);
        stage.setScene(scene);
        stage.show();
    }

    class PrintChar implements Runnable {
        private char charToPrint; // The character to print
        private int times; // The number of times to repeat

        /**
         * Construct a task with a specified character and number of times to print the character
         */

        public PrintChar(char c, int t) {
            charToPrint = c;
            times = t;
        }

        @Override
        public void run() {
            for (int i = 0; i < times; i++) {
                lock.lock(); //Acquire the lock
                text += charToPrint;
                lock.unlock(); // Release the lock
            }
        }
    }

    class PrintNum implements Runnable {
        private int lastNum;

        /**
         * Construct a task for printing 1, 2, ..., n
         */
        public PrintNum(int n) {
            lastNum = n;
        }

        @Override
        public void run() {
            for (int i = 0; i < lastNum; i++) {
                lock.lock(); //Acquire the lock
                text += i;
                lock.unlock(); // Release the lock
            }
        }
    }
}