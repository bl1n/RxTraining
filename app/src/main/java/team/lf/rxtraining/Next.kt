package team.lf.rxtraining

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


fun main(){

    var disposable = MainActivity.getListLong(100)
        .subscribeOn(Schedulers.io())
        .flatMap {list: List<Long>->
            Observable.fromIterable(list)
        }
//        .flatMap {id:Long->
//            MainActivity.getProfileById(id)
//        }
//        .toList()
//        .observeOn(AndroidSchedulers.mainThread())
        .subscribe (System.out::println)

}