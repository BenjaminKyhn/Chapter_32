import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

public class Exercise_32_12 {
    public static void main(String[] args) {
        // Create a list
        final int N = 9000000;
        double[] list = new double[N];

        long startTime = System.currentTimeMillis();
        parallelAssignValues(list);
        long endTime = System.currentTimeMillis();
        System.out.println("The number of processors is " + Runtime.getRuntime().availableProcessors());
        System.out.println("Parallel time is " + (endTime - startTime) + " milliseconds");
        System.out.println("Index 500000 is " + list[500000]);


        startTime = System.currentTimeMillis();
        Random r = new Random();
        for (int i = 0; i < list.length; i++) {
            list[i] = r.nextDouble();
        }
        endTime = System.currentTimeMillis();
        System.out.println("\nSequential time is " + (endTime - startTime) + " milliseconds");
        System.out.println("Index 500000 is " + list[500000]);
    }

    public static void parallelAssignValues(double[] list){
        RecursiveTask<double[]> mainTask = new AssignValuesTask(list, 0, list.length);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(mainTask);
    }

    private static class AssignValuesTask extends RecursiveTask<double[]>{
        private final int THRESHOLD = 1000;
        private double[] list;
        private int low;
        private int high;
        private Random r = new Random();

        public AssignValuesTask(double[] list, int low, int high){
            this.list = list;
            this.low = low;
            this.high = high;
        }

        @Override
        protected double[] compute() {
            if (high - low < THRESHOLD){
                for (int i = low; i < high; i++)
                    list[i] = r.nextDouble();
                return list;
            }
            else {
                int mid = (low + high) / 2;
                RecursiveTask<double[]> left = new AssignValuesTask(list, low, mid);
                RecursiveTask<double[]> right = new AssignValuesTask(list, mid, high);

                // Returns the right side of the array
                right.fork();

                // Returns the left side of the array
                left.fork();

                return list;
            }
        }
    }
}
