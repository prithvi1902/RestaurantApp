package com.example.demo.restaurantapp.Model

data class NearByPlaces(
        val html_attributions: List<Any?>?,
        val next_page_token: String,
        val results: List<Result?>?,
        val status: String
)