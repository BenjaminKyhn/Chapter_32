import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Exercise_32_12 {
    public static void main(String[] args) {
        final int SIZE = 9000000;
        double[] list1 = new double[SIZE];
        double[] list2 = new double[SIZE];

        // Assign random values to list1 in parallel
        long startTime = System.currentTimeMillis();
        parallelAssignValues(list1); // Invoke parallel merge sort
        long endTime = System.currentTimeMillis();
        System.out.println("\nParallel time with " + Runtime.getRuntime().availableProcessors() +
                " processors is " + (endTime - startTime) + " milliseconds");

        // Assign random values to list2 in sequence
        startTime = System.currentTimeMillis();
        Random r = new Random();
        for (int i = 0; i < list1.length; i++) {
            list2[i] = r.nextDouble();
        }
        endTime = System.currentTimeMillis();
        System.out.println("\nSequential time is " + (endTime - startTime) + " milliseconds");
    }

    public static void parallelAssignValues(double[] list){
        RecursiveAction mainTask = new AssignValuesTask(list);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(mainTask);

    }

    private static class AssignValuesTask extends RecursiveAction{
        private final int THRESHOLD = 500;
        private double[] list;
        private Random r = new Random();

        AssignValuesTask(double[] list){
            this.list = list;
        }

        @Override
        protected void compute() {
            if (list.length < THRESHOLD)
                for (int i = 0; i < list.length; i++) {
                    list[i] = r.nextDouble();
                }
            else {
                // Obtain the first half
                double[] firstHalf = new double[list.length/2];
                System.arraycopy(list, 0, firstHalf, 0, list.length/2);

                // Obtain the second half
                int secondHalfLength = list.length - list.length/2;
                double[] secondHalf = new double[secondHalfLength];
                System.arraycopy(list, list.length / 2, secondHalf, 0, secondHalfLength);

                // Recursively sort the two halves
                invokeAll(new AssignValuesTask(firstHalf), new AssignValuesTask(secondHalf));

                // Merge firstHalf with secondHalf into list
                int current1 = 0; // Current index in list1
                int current2 = 0; // Current index in list2
                int current3 = 0; // Current index in temp

                while (current1 < firstHalf.length && current2 < secondHalf.length) {
                    if (firstHalf[current1] < secondHalf[current2])
                        list[current3++] = firstHalf[current1++];
                    else
                        list[current3++] = secondHalf[current2++];
                }

                while (current1 < firstHalf.length)
                    list[current3++] = firstHalf[current1++];

                while (current2 < secondHalf.length)
                    list[current3++] = secondHalf[current2++];
            }
        }
    }
}
