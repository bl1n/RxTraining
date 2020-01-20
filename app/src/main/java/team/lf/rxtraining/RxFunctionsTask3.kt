package team.lf.rxtraining

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

data class FirstType(val id: Int)
data class SecondType(val id: Int)
data class ThirdType(val id: Int)
data class FourthType(val id: Int)
data class FifthType(val id: Int)


fun main() {
    val first = createObservable<FirstType>(5, true).doOnError { showFullScreenError() }
    val second = createObservable<SecondType>(5, false).doOnError { showFullScreenError() }
    val third = createObservable<ThirdType>(5, false).doOnError { showHalfScreenError() }
    val fourth = createObservable<FourthType>(5, false)
    val fifth = createObservable<FifthType>(5, false)

    val disposable = Observable.merge(
        Observable.mergeDelayError(
            Observable.merge(first, second),
            third
        ),
        Observable.mergeDelayError(fourth, fifth)
    )
        .subscribe({}, { println(it.message) }, {})

}

inline fun <reified T> createObservable(
    count: Int,
    exception: Boolean
): Observable<T> =
    Observable.create<T> { emitter ->
        var c = 0
        while (!emitter.isDisposed) {
            if (exception) {
                emitter.onError(IllegalStateException("${T::class.java.simpleName} exception"))
                break
            }
            val id = c++
            emitter.onNext(T::class.constructors.first().call(id))
            if (c == count) {
                emitter.onComplete()
            }
        }
    }.doOnComplete { println("${T::class.java.simpleName} complete") }.doOnNext { println("$it") }

fun showHalfScreenError() {
    println("Half screen error")
}

fun showFullScreenError() {
    println("Full screen error")
}


