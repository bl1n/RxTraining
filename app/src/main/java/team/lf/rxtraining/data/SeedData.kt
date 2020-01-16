package team.lf.rxtraining.data

import android.util.Log
import io.reactivex.Observable
import team.lf.rxtraining.data.model.Profile


fun createLongList(count: Long): List<Long> {
    val list = mutableListOf<Long>()
    for (i in 0 until count) {
        list.add(i)
    }
    return list.toList()
}

fun createObservableListLong(count: Long): Observable<List<Long>> = Observable.fromIterable(
    listOf(
        createLongList(count)
    )
)

//fake profile storage
fun getObservableProfileById(id: Long): Observable<Profile> {
    //if(id == 10) throw Exc
    return Observable.create<Profile> {
        Profile(id, "name $id")
    }
}

