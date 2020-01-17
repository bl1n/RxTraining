package team.lf.rxtraining

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers

/**
 * Придумать кейсы и реализовать их на тестовых данных. Например:
1) Выполнение двух запросов последовательно, где данные первого запроса участвуют во втором.
Как пример запрос списка id пользователей, для каждого id пользователя получение профиля и возвращение списка профилей.
Т.е. первый запрос возвращается Observable<List<Long>>, второй по id Observable<Profile>, результирующий возвращает Observable<List<Profile>>.
2) Выполнение двух запросов параллельно и одновременный вывод данных с двух этих запросов на экран.
3) Тест сложной логики обработки ошибок. Выполнение 3 (1, 2 и 3) запросов параллельно и 2 (4 и 5)
последовательно. Если 1 или 2 вернул ошибку то отображаем ошибку на весь экран, если 1 и 2 не вернули ошибки
и 3 вернул ошибку отображаем сообщение об ошибке на часть экрана и данные 1 и 2 запроса (при этом 4 и 5 не выполняются),
если 1, 2, 3 не вернули ошибок то 4 и 5 выполняются. При этом пока идут 1 и 2 запрос мы показываем полноэкранную загрузку,
когда они загрузятся мы показываем данные на части экрана и пока в этот момент грузится 3 запрос мы показываем загрузку на
части экрана, где выводятся данные с 3 запроса.
 */


class MainActivity : AppCompatActivity() {
    companion object FakeRepository {

        data class Profile(
            val id: Long,
            val name: String
        )

        private fun createLongList(count: Long): List<Long> {
            val list = mutableListOf<Long>()
            for (i in 0 until count) {
                list.add(i)
            }
            return list.toList()
        }

        fun getListLong(count: Long, exception: Boolean = false): Observable<List<Long>> {
            if (exception) throw IllegalStateException("longs exception")
            return listOf(createLongList(count)).toObservable()
        }

        fun getProfileById(id: Long, exception: Boolean = false): Profile {
            if (exception) throw IllegalStateException("profile exception")
            return Profile(id, "name $id")
        }
    }

    private lateinit var disposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        disposable = FakeRepository.getListLong(100)
            .subscribeOn(Schedulers.io())
            .flatMap {list:List<Long> ->
                Log.d("TAG", "long list size ${list.size}")
                Observable.fromIterable(list)
            }
            .flatMap {id:Long->
                Log.d("TAG", "id is $id")
                Observable.just(FakeRepository.getProfileById(id))
            }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError{
                showError(it)
            }
            .subscribe {
                    list: List<Profile> -> Log.d("TAG", list.size.toString())
            }
    }

    private fun showError(it: Throwable?) {
        Log.d("TAG", it?.message.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposable.isDisposed) disposable.dispose()
    }
}
