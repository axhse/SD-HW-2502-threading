import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        SpamVictim victim = new SpamVictim(8);
        SpamAttacker attacker = new SpamAttacker(victim, 300, 200);
        SpamDefender defender = new SpamDefender(victim, 800, 700);
        new Thread(attacker).start();
        new Thread(defender).start();
    }
}

class SpamVictim {
    ArrayList < Integer > buffer = new ArrayList < Integer > ();
    int bufferLimitSize;
    final Object syncRoot = new Object();

    SpamVictim(int bufferLimitSize) {
        this.bufferLimitSize = bufferLimitSize;
    }
}

class SpamAttacker implements Runnable {
    private SpamVictim victim;
    private int sleepingPeriod;
    private int sleepingVariability;
    SpamAttacker(SpamVictim victim, int sleepingPeriod, int sleepingVariability) {
        this.victim = victim;
        this.sleepingPeriod = sleepingPeriod;
        this.sleepingVariability = sleepingVariability;
    }

    @Override
    public void run() {
        Logger logger = new Logger("\u001B[91m");
        while (true) {
            synchronized(victim.syncRoot) {
                if (victim.buffer.size() < victim.bufferLimitSize) {
                    int randomValue = new Random().nextInt();
                    victim.buffer.add(randomValue);
                    logger.log(String.format("Attacked:  %d", randomValue));
                } else {
                    logger.log("Buffer is filled, attack skipped");
                }
            }
            ThreadingUtils.sleep(sleepingPeriod + (new Random().nextInt() % sleepingVariability));
        }
    }
}

class SpamDefender implements Runnable {
    private SpamVictim victim;
    private int sleepingPeriod;
    private int sleepingVariability;
    SpamDefender(SpamVictim victim, int sleepingPeriod, int sleepingVariability) {
        this.victim = victim;
        this.sleepingPeriod = sleepingPeriod;
        this.sleepingVariability = sleepingVariability;
    }

    @Override
    public void run() {
        Logger logger = new Logger("\u001B[94m");
        while (true) {
            synchronized(victim.syncRoot) {
                String statusMessage =  String.format("Buffer(%d) = [", victim.buffer.size());
                if (victim.buffer.size() > 0) {
                    statusMessage += Integer.toString(victim.buffer.get(0));
                }
                for (int index = 1; index < victim.buffer.size(); index++) {
                    statusMessage += String.format(", %d", victim.buffer.get(index));
                }
                statusMessage += "]";
                logger.log(statusMessage);
                if (victim.buffer.size() == victim.bufferLimitSize) {
                    victim.buffer.clear();
                    logger.log("Buffer was cleaned\n");
                } else {
                    logger.log("Buffer is not filled, cleanup skipped");
                }
            }
            ThreadingUtils.sleep(sleepingPeriod + (new Random().nextInt() % sleepingVariability));
        }
    }
}


class Logger {
    private static final String effectResetCode = "\u001B[0m";
    private String effectCode;

    Logger(String effectCode) {
        this.effectCode = effectCode;
    }

    void log(String message) {
        System.out.println(effectCode + message + effectResetCode);
    }
}

class ThreadingUtils {
    static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException("Unexpected interruption.", e);
        }
    }
}