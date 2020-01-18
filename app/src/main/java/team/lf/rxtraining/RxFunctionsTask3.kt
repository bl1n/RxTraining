package team.lf.rxtraining

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

val compositeDisposable = CompositeDisposable()

fun main() {
    compositeDisposable.add(
        Observable
            .mergeDelayError(
                getFirstObservable(false)
                    .mergeWith(getSecondObservable(false))
                    .doOnError {
                        compositeDisposable.dispose()
                        showFullScreenError()
                    }
                , getThirdObservable(false)
            )
            .doOnError {
                compositeDisposable.dispose()
                showHalfScreenError()
            }
            .concatWith(
                getFourthObservable()
                    .concatWith(getFifthObservable())
            )
            .subscribe {
                println(it)
            }
    )
}


fun getFirstObservable(exception: Boolean): Observable<Int> =
    if (exception) throw IllegalStateException("first exception")
    else Observable.just(1, 1, 1, 1)


fun getSecondObservable(exception: Boolean): Observable<Int> =
    if (exception) throw IllegalStateException("second exception")
    else Observable.just(2, 2, 2, 2)


fun getThirdObservable(exception: Boolean): Observable<Int> =
    if (exception) throw IllegalStateException("third exception")
    else Observable.just(3, 3, 3, 3)

fun getFourthObservable() = Observable.just(4, 4, 4, 4)

fun getFifthObservable() = Observable.just(5, 5, 5, 5)


fun showHalfScreenError() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}

fun showFullScreenError() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}


