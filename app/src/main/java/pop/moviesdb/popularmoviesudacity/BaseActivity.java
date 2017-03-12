package pop.moviesdb.popularmoviesudacity;

import android.support.v7.app.AppCompatActivity;

import pop.moviesdb.popularmoviesudacity.events.BusProvider;

/**
 * Base class for Activities, contains basic common functionality.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

}
