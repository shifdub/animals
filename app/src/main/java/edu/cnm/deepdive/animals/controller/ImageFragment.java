
package edu.cnm.deepdive.animals.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cnm.deepdive.animals.BuildConfig;
import edu.cnm.deepdive.animals.R;
import edu.cnm.deepdive.animals.model.Animal;
import edu.cnm.deepdive.animals.model.ApiKey;
import edu.cnm.deepdive.animals.service.AnimalService;
import edu.cnm.deepdive.animals.viewmodel.AnimalViewModel;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageFragment extends Fragment {

  private WebView contentView;
  private AnimalViewModel animalViewModel;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_image, container, false);
    setupWebView(root);
    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view,
      @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    animalViewModel = new ViewModelProvider(getActivity())
        .get(AnimalViewModel.class);
    animalViewModel.getAnimals().observe(getViewLifecycleOwner(), new Observer<List<Animal>>() {
      @Override
      public void onChanged(List<Animal> animals) {
        contentView.loadUrl(animals.get(22).getImageUrl());
      }
    });

  }

  private void setupWebView(View root) {
    contentView = root.findViewById(R.id.content_view);
    contentView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return false;
      }
    });
    WebSettings settings = contentView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setSupportZoom(true);
    settings.setBuiltInZoomControls(true);
    settings.setDisplayZoomControls(false);
    settings.setUseWideViewPort(true);
    settings.setLoadWithOverviewMode(true);
    new RetrieverTask().execute();
  }

  private class RetrieverTask extends AsyncTask<Void, Void, List<Animal>> {

    private AnimalService animalService;

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      Gson gson = new GsonBuilder()
          .create();
      Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(BuildConfig.BASE_URL)
          .addConverterFactory(GsonConverterFactory.create(gson))
          .build();
      animalService = retrofit.create(AnimalService.class);
    }

    @Override
    protected List<Animal> doInBackground(Void... voids) {
      try {
        Response<ApiKey> keyResponse = animalService.getApiKey().execute();
        ApiKey key = keyResponse.body();
        assert key != null;
        final String clientKey = key.getKey();

        Response<List<Animal>> listResponse = animalService.getAnimals(clientKey).execute();
        List<Animal> animalList = listResponse.body();
        assert animalList != null;
        return animalList;
      } catch (
          IOException e) {
        Log.e("AnimalService", e.getMessage(), e);
        cancel(true);
      }
      return null;
    }

    @Override
    protected void onPostExecute(List<Animal> animalList) {


      Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
        @Override
        public void run() {


        }
      });
    }
  }
}
