package ca.bc.gov.secureimage.data.repos.user

import ca.bc.gov.secureimage.data.models.User
import io.reactivex.Observable
import io.realm.Realm

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
object UserLocalDataSource : UserDataSource {

    override fun getUser(): Observable<User> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val user = realm.where(User::class.java).findFirst()
                if(user != null) emitter.onNext(realm.copyFromRealm(user))
                else emitter.onNext(User())
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun saveUser(user: User): Observable<User> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                realm.copyToRealmOrUpdate(user)
                emitter.onNext(user)
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun deleteUser(): Observable<User> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val user = realm.where(User::class.java).findFirst()
                user?.deleteFromRealm()

                if (user != null) emitter.onNext(user)
                else emitter.onError(Throwable("User not found"))
            }
            realm.close()

            emitter.onComplete()
        }
    }

}