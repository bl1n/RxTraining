package team.lf.rxtraining

import io.reactivex.Observable
import io.reactivex.functions.BiFunction

data class User(val name: String, val age: Int)

fun main() {
    val nameObservable = Observable.just("Donald", "Mickey", "Tom", "Jerry")
    val ageObservable = Observable.just(10, 20, 30, 40)
    val disposable = Observable.zip(
        nameObservable,
        ageObservable,
        BiFunction { name: String, age: Int -> User(name, age) }
    )
        .subscribe{
            println(it)
        }
}

