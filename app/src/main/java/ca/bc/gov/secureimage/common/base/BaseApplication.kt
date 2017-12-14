package ca.bc.gov.secureimage.common.base

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }

}