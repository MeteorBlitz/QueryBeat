package com.example.querybeat.data.remote

import com.apollographql.apollo.ApolloClient

object GraphQLClient {
    val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl("https://countries.trevorblades.com/graphql")
        .build()
}