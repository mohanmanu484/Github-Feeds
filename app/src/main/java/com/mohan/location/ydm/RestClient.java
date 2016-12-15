package com.mohan.location.ydm;
 
import retrofit2.Retrofit;
 
/** 
 * Created by mohan on 3/10/16. 
 */ 
 
public class RestClient { 
 
    private static Retrofit retrofit;
    static String URL="https://api.github.com";
    //v5CXXRF1nsyEAFs2shT3hcynsHEA2yVpPmoxk4oNLkdGKP2ci3RYupX7vSxu 
 
 
    public static Retrofit getClient(){ 
        if(retrofit==null){
             retrofit = new Retrofit.Builder    ()
                    .baseUrl(URL)
                     .addConverterFactory(new ToStringConverterFactory()) 
                    .build(); 
        } 
 
        return retrofit;
    } 
} 