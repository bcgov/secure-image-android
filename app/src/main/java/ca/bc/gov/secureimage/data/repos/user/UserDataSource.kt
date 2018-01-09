package ca.bc.gov.secureimage.data.repos.user

import ca.bc.gov.secureimage.data.models.local.User
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
interface UserDataSource {

    fun getUser(): Observable<User>

    fun saveUser(user: User): Observable<User>

    fun deleteUser(): Observable<User>

}