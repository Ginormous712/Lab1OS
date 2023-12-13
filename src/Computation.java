import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Computation {
    final static int CASE1_ATTEMPTS = 10;
    static int attempt = CASE1_ATTEMPTS;
    private static final int COMPUTATION_TIMEOUT = 5000;

    private static Optional<Integer> f(int n) throws InterruptedException, TimeoutException {
        if (n == 0) {
            System.out.println("f function, value " + n);
            // return Optional.empty();
            return hardError();
        }

        //double a = n;
        Optional<Integer> result;

        if (n > 25) {
            Random random = new Random();
            result = softError();
            long startTime = System.currentTimeMillis();

            while (attempt > 0 && result.equals(Optional.empty()) && System.currentTimeMillis() - startTime < COMPUTATION_TIMEOUT) {
                int randomValue = random.nextInt(10);
                System.out.print('.');
                // 80% to get result
                if (randomValue < 8) {
                    attempt--;
                    result = softError();
                } else {
                    return Optional.of(n * n);
                }
            }
            if (System.currentTimeMillis() - startTime > COMPUTATION_TIMEOUT) {
                System.out.println("Time f function is out");
                throw new TimeoutException("Timeout in f function");
            }

            attempt = CASE1_ATTEMPTS;
            return result;
        }
        //System.out.println("sfsdf");
        return Optional.of(n * n);
    }

    private static Optional<Integer> g(int n) throws InterruptedException, TimeoutException {
        if (n < 3) {
            System.out.println("Hard error in g function, value " + n);
            return hardError();
        }

        //double a = n;
        Optional<Integer> result;

        if (n < 15) {
            Random random = new Random();
            result = softError();
            long startTime = System.currentTimeMillis();
            //result = Optional.empty();

            while (attempt > 0 && result.equals(Optional.empty()) && System.currentTimeMillis() - startTime < COMPUTATION_TIMEOUT) {
                int randomValue = random.nextInt(10);
                System.out.print('.');
                // 80% to get result
                if (randomValue < 8) {
                    attempt--;
                    result = softError();
                } else {
                    return Optional.of(n + 10);
                }
            }
            if (System.currentTimeMillis() - startTime > COMPUTATION_TIMEOUT) {
                System.out.println("Time g function is out");
                throw new TimeoutException("Timeout in g function");
            }

            attempt = CASE1_ATTEMPTS;
            return result;
        }

        return Optional.of(n + 10);
    }

    private static Optional<Integer> softError() throws InterruptedException {
        // generation soft error
        TimeUnit.SECONDS.sleep(1);
        return Optional.empty();
    }

    private static Optional<Integer> hardError() throws InterruptedException {
        // generation hard error
        TimeUnit.SECONDS.sleep(2);
        //return Optional.empty();
        throw new HardErrorException("This is a hard_error");
    }

    static class HardErrorException extends RuntimeException {
        public HardErrorException(String message) {
            super(message);
        }
    }

    public static Optional<Optional<Integer>> compfunc(int n) {
        CompletableFuture<Optional<Optional<Integer>>> resultFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                Optional<Integer> fResult = f(n);
                Optional<Integer> gResult = g(n);

                if (fResult.isPresent() && gResult.isPresent()) {
                    int gcdResult = gcd(fResult.get(), gResult.get());
                    resultFuture.complete(Optional.of(Optional.of(gcdResult)));
                } else {
                    printResultIfPresent("f", fResult);
                    printResultIfPresent("g", gResult);
                    resultFuture.complete(Optional.empty());
                }
            } catch (Exception e) {
                resultFuture.completeExceptionally(e);
            }
        });

        CompletableFuture<Void> timeoutFuture = new CompletableFuture<>();
        resultFuture.completeOnTimeout(Optional.empty(), COMPUTATION_TIMEOUT/1000, TimeUnit.SECONDS);

        resultFuture.whenComplete((result, throwable) -> {
            if (throwable != null && !(throwable.getCause() instanceof HardErrorException)) {
                System.out.println("Error performing calculations: " + throwable.getMessage());
            } else if (result.isPresent()) {
                System.out.println("Result: " + result.get());
            }
            timeoutFuture.complete(null);
        });

        // Чекаємо на завершення обчислень і повертаємо результат
        try {
            timeoutFuture.get(); // Чекаємо на завершення або таймаут
            return resultFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.of(Optional.empty());
        }
    }

    private static void printResultIfPresent(String name, Optional<Integer> result) {
        result.ifPresentOrElse(
                value -> System.out.println(name + ": " + value),
                () -> System.out.println(name + " function is not available")
        );
    }

    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

}
