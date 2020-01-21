package team.lf.rxtraining

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

data class FirstType(val id: Int)
data class SecondType(val id: Int)
data class ThirdType(val id: Int)
data class FourthType(val id: Int)
data class FifthType(val id: Int)

val executor: ExecutorService = Executors.newFixedThreadPool(3)
val pooledScheduler = Schedulers.from(executor)


fun main() {
    val first = createObservable<FirstType>(5, false).doOnError { showFullScreenError() }
        .subscribeOn(pooledScheduler)
    val second = createObservable<SecondType>(5, false).doOnError { showFullScreenError(); }
        .subscribeOn(pooledScheduler)
    val third = createObservable<ThirdType>(5, false).doOnError { showHalfScreenError() }
        .doOnSubscribe { halfScreenLoading() }.doOnComplete { hideHalfScreenLoading() }
        .subscribeOn(pooledScheduler)
    val fourth = createObservable<FourthType>(5, false)
    val fifth = createObservable<FifthType>(5, false)

    val disposable = Observable.concat(
        Observable.mergeDelayError(
            Observable.merge(
                first,
                second
            ).doOnSubscribe { fullScreenLoading() }.doOnComplete { hideFullScreenLoading() },
            third
        ).subscribeOn(Schedulers.trampoline()),
        Observable.merge(fourth, fifth)
            .subscribeOn(Schedulers.trampoline())
    )
        .subscribe(
            {}, //all observables provide theirs onNext methods
            { executor.shutdownNow() }, //onError
            { executor.shutdownNow() }) //onComplete
}

fun hideFullScreenLoading() {
    println("hide full screen loading")
}

fun hideHalfScreenLoading() {
    println("hide half screen loading")
}

fun halfScreenLoading() {
    println("half screen loading")
}

fun fullScreenLoading() {
    println("full screen loading")
}


inline fun <reified T> createObservable(
    count: Int,
    exception: Boolean
): Observable<T> =
    Observable.create<T> { emitter ->
        var c = 0
        while (!emitter.isDisposed) {
            if (exception) {
                emitter.onError(IllegalStateException("${T::class.java.simpleName} excepted"))
                break
            }
            val id = c++
            emitter.onNext(T::class.constructors.first().call(id))
            if (c == count) {
                emitter.onComplete()
            }
        }
    }.doOnComplete { println("${T::class.java.simpleName} complete") }.doOnNext { println("$it, thread = ${Thread.currentThread().name} ") }

fun showHalfScreenError() {
    println("Half screen error!")
}

fun showFullScreenError() {
    println("Full screen error!")
}


