package com.example.demo.restaurantapp.Model

data class Result(
        val geometry: Geometry,
        val icon: String?,
        val id: String?,
        val name: String?,
        val photos: List<Photo?>?,
        val place_id: String?,
        val reference: String?,
        val scope: String?,
        val types: List<String?>?,
        val vicinity: String?,
        val plus_code: PlusCode?,
        val rating: Double?,
        val opening_hours: OpeningHours?,
        var distance: String?
)