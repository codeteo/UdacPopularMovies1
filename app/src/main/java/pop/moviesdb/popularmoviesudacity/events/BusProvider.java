package pop.moviesdb.popularmoviesudacity.events;


import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Maintains a singleton instance for obtaining the bus. Ideally this would be replaced with a more efficient means
 * such as through injection directly into interested classes.
 * Taken from Jake Wharton Otto Sample :
 * https://github.com/square/otto/blob/master/otto-sample/src/main/java/com/squareup/otto/sample/BusProvider.java
 */
public final class BusProvider {

    private static final Bus _instance = new MainThreadBus();

    public static Bus getInstance() {
        return _instance;
    }

    private BusProvider() {
        // No instances.
    }

    private static class MainThreadBus extends Bus {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            // if I'm already on the main thread, I can just post
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            }
            else {          // otherwise, wrap the post in mainThreadHandler
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        post(event);
                    }
                });
            }
        }
    }

}
