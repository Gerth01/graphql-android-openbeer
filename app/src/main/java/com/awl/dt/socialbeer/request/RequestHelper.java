package com.awl.dt.socialbeer.request;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Query;

import okhttp3.OkHttpClient;

public class RequestHelper {

    private String BASE_URL = "http://graphql-neo4j-openbeerdb.beta.ita.rocks/graphql";

    private ApolloClient apolloClient;
    private static RequestHelper instance;

    private RequestHelper() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        apolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .build();
    }

    public static RequestHelper getInstance(){
        if (instance == null)
            instance = new RequestHelper();
        return instance;
    }

    public void performQuery (Object query, ApolloCall.Callback callback){
        apolloClient.query((Query<Operation.Data, Object, Operation.Variables>) query).enqueue(callback);
    }

    public void performMutation ( Object mutation , ApolloCall.Callback callback){
        apolloClient.mutate((Mutation<Operation.Data, Object, Operation.Variables>) mutation).enqueue(callback);
    }
}
