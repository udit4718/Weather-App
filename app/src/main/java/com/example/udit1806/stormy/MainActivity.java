package com.example.udit1806.stormy;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
{

    private static final String  TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;
    @BindView(R.id.locationLabel) TextView mLocationLabel;
    @BindView(R.id.temperatureview) TextView mTemperatureView;
    @BindView(R.id.timelabel) TextView mTimeLabel;
    @BindView(R.id.humidityvalue) TextView mHumidityValue;
    @BindView(R.id.precipvalue) TextView mPrecipValue;
    @BindView(R.id.summaryview) TextView mSummaryLabel;
    @BindView(R.id.iconimageview) ImageView mIconImageView;
    @BindView(R.id.refreshimageView) ImageView mRefreshImageView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mProgressBar.setVisibility(View.INVISIBLE);
        final double latitude = 28.4;
        final double longitude = 77.2;

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getForecast( latitude, longitude);
            }
        });


        getForecast(latitude,longitude);




    }

    private void getForecast(double latitude,double longitude) {
        String apikey = "38321a5206d7de85184c6ddce750388e";

        String forecastUrl = "https://api.forecast.io/forecast/" + apikey+ "/" + latitude + "," + longitude;


        if(isNetworkAvailable())
        {
            runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toggleRefresh();
            }
        });


            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();

                        }

                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try
                    {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful())
                        {

                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    updateDisplay();

                                }
                            });

                        }
                        else
                         {
                            alertUserAboutError();
                         }
                    }
                    catch (IOException e)
                     {
                        Log.e(TAG, "Exception Caught : ", e);
                     }
                     catch (JSONException e)
                     {
                         Log.e(TAG, "Exception Caught : ", e);
                     }

                }
            });

        }

        else
        {
            Toast.makeText(this, R.string.error_Network_Unavailable,Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh()
    {

        if (mProgressBar.getVisibility() == View.INVISIBLE)
        {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);

        }
        else
        {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);

        }
    }
    private void updateDisplay()
    {
       mLocationLabel.setText(mCurrentWeather.getTimeZone());
      mTemperatureView.setText(mCurrentWeather.getTemperature()+"");
        mTimeLabel.setText("At " + mCurrentWeather.getformattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity()+"");
        mPrecipValue.setText(mCurrentWeather.getPercipChance()+"%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
    }

    private CurrentWeather getCurrentDetails (String jsonData) throws JSONException
    {
        JSONObject foreCast = new JSONObject(jsonData);

        JSONObject currently =foreCast.getJSONObject("currently");

        String timezone = foreCast.getString("timezone");
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setPercipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG,currentWeather.getformattedTime());
        return currentWeather;
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkinfo!=null&&networkinfo.isConnected())
        {
             isAvailable = true;
        }
        return  isAvailable;

    }

    private void alertUserAboutError()
    {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }
}
