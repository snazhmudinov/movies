package com.snazhmudinov.movies.modules;

import android.content.Context;

import com.snazhmudinov.movies.database.DatabaseManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by snazhmudinov on 7/16/17.
 */
@Module
public class DatabaseModule {

    private Context context;

    public DatabaseModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    DatabaseManager provideDatabaseManager() {
        return new DatabaseManager(context);
    }
}
