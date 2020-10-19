package edu.cnm.deepdive.animals.viewmodel;


import android.annotation.SuppressLint;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.animals.model.Animal;
import edu.cnm.deepdive.animals.service.AnimalService;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class AnimalViewModel extends AndroidViewModel {

  private final MutableLiveData<List<Animal>> animals;
  private final MutableLiveData<Throwable> throwable;
  private final AnimalService animalService;

  public AnimalViewModel(
      @NonNull Application application) {
    super(application);
    animals = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    animalService = AnimalService.getInstance();
    loadAnimals();
  }

  public LiveData<List<Animal>> getAnimals() {
    return animals;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  @SuppressLint("CheckResult")
  private void loadAnimals() {

    animalService.getApiKey()
        .subscribeOn(Schedulers.io())
        .flatMap((key) -> animalService.getAnimals(key.getKey()))
        .subscribe(
            animals::postValue,
            throwable::postValue
        );
  }
}