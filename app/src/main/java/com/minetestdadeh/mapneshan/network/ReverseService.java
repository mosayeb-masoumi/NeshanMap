package com.minetestdadeh.mapneshan.network;

import com.minetestdadeh.mapneshan.model.NeshanAddress;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Url;

public interface ReverseService {

    // TODO: replace "YOUR_API_KEY" with your api key
    @Headers("Api-Key: service.kREahwU7lND32ygT9ZgPFXbwjzzKukdObRZsnUAJ")
    @GET
    Observable<NeshanAddress> getReverse(@Url String url);
}
