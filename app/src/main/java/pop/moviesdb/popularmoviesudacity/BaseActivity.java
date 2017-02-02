package pop.moviesdb.popularmoviesudacity;

import android.support.v7.app.AppCompatActivity;

import pop.moviesdb.popularmoviesudacity.events.BusProvider;

/**
 * Created by css on 2/2/17.
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
