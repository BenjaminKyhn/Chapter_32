import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Listing_32_07 {
    private static Buffer buffer = new Buffer();

    public static void main(String[] args) {
        // Create a thread pool with 2 threads
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(new ProducerTask());
        executor.execute(new ConsumerTask());
        executor.shutdown();
    }

    // A task for adding an int to the buffer
    private static class ProducerTask implements Runnable{
        public void run() {
            try{
                int i = 1;
                while (true){
                    System.out.println("Producer writes " + i);
                    buffer.write(i++); // Add a value to the buffer
                    // Put the thread into sleep
                    Thread.sleep((int)(Math.random()*10000));
                }
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    // A task for reading and deleting an int from the buffer
    private static class ConsumerTask implements Runnable{
        public void run() {
            try{
                while (true){
                    System.out.println("\t\t\tConsumer reads " + buffer.read());
                    // Put the thread into sleep
                    Thread.sleep((int)(Math.random()*10000));
                }
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
        }
    }

    // An inner class for buffer
    private static class Buffer {
        private static final int CAPACITY = 1; // buffer size
        private LinkedList<Integer> qeueu = new LinkedList<>();

        // Create a new lock
        private static Lock lock = new ReentrantLock();

        // Create two conditions
        private static Condition notEmpty = lock.newCondition();
        private static Condition notFull = lock.newCondition();

        public void write(int value){
            lock.lock(); // Acquire the lock
            try{
                while (qeueu.size() == CAPACITY){
                    System.out.println("Wait for notFull condition");
                    notFull.await();
                }

                qeueu.offer(value);
                notEmpty.signal(); // Signal notEmpty condition
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
            finally {
                lock.unlock(); // Release the lock
            }
        }

        public int read(){
            int value = 0;
            lock.lock(); // Acquire the lock
            try {
                while (qeueu.isEmpty()){
                    System.out.println("\t\tWait for notEmpty ondition");
                    notEmpty.await();
                }

                value = qeueu.remove();
                notFull.signal(); // Signal notFull condition
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
            finally {
                lock.unlock(); // Release the lock
                return value;
            }
        }
    }
}
