package ca.bc.gov.secureimage.data.repos.user

import ca.bc.gov.secureimage.data.models.local.User
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
class UserRepo
private constructor(private val localDataSource: UserDataSource) : UserDataSource {

    companion object {

        @Volatile private var INSTANCE: UserRepo? = null

        fun getInstance(localDataSource: UserDataSource): UserRepo =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: UserRepo(localDataSource).also { INSTANCE = it }
                }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getUser(): Observable<User> = localDataSource.getUser()

    override fun saveUser(user: User): Observable<User> = localDataSource.saveUser(user)

    override fun deleteUser(): Observable<User> = localDataSource.deleteUser()

}