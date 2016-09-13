package com.example.bilaizi.asymmetricfingerprintdialog;

import android.app.Application;
import android.util.Log;

import dagger.ObjectGraph;

/**
 * The Application class of the sample which holds the ObjectGraph in Dagger and enables
 * dependency injection.
 */
public class InjectedApplication extends Application {

    private static final String TAG = InjectedApplication.class.getSimpleName();

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        initObjectGraph(new FingerprintModule(this));
    }

    /**
     * Initialize the Dagger module. Passing null or mock modules can be used for testing.
     *
     * @param module for Dagger
     */
    public void initObjectGraph(Object module) {
        mObjectGraph = module != null ? ObjectGraph.create(module) : null;
    }

    public void inject(Object object) {
        if (mObjectGraph == null) {
            // This usually happens during tests.
            Log.i(TAG, "Object graph is not initialized.");
            return;
        }
        mObjectGraph.inject(object);
    }

}
