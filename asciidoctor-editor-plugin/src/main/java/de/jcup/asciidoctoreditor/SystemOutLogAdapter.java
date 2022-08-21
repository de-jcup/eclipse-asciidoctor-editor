package de.jcup.asciidoctoreditor;

public class SystemOutLogAdapter implements LogAdapter {

    @Override
    public void logInfo(String message) {
        System.out.println("info:" + message);
    }

    @Override
    public void logWarn(String message) {
        System.out.println("warn:" + message);
    }

    @Override
    public void logError(String message, Throwable t) {
        System.err.println("error:" + message);
        if (t != null) {
            t.printStackTrace();
        }
    }

    private long time;

    @Override
    public void resetTimeDiff() {
        time = System.currentTimeMillis();
    }

    @Override
    public void logTimeDiff(String info) {
        System.out.println("timediff:" + info + " - " + (System.currentTimeMillis() - time) + " ms");
    }

}
