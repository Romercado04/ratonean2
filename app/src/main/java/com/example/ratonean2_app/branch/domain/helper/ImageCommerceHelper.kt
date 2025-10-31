package com.example.ratonean2_app.branch.domain.helper

import com.example.ratonean2_app.R

fun assignImageToCommerce(idCommerce: String): Int{
    return when(idCommerce){
        "1000" -> R.drawable.logo_commid1000_fgorina
        "1003" -> R.drawable.logo_commid1003_potigian
        "10" -> R.drawable.logo_commid10_carrefour
        "11" -> R.drawable.logo_commid11_changomas
        "12" -> R.drawable.logo_commid12_coto
        "13" -> R.drawable.logo_commid13_coopobrera
        "15" -> R.drawable.logo_commid15_dia
        "16" -> R.drawable.logo_commid16_libertad
        "2000" -> R.drawable.logo_commid2000_musimundo
        "2002" -> R.drawable.logo_commid2002_fravega
        "2004" -> R.drawable.logo_commid2004_coppel
        "2005" -> R.drawable.logo_commid2005_naldo
        "2007" -> R.drawable.logo_commid2007_sony
        "2013" -> R.drawable.logo_commid2013_mercadolibre
        "20" -> R.drawable.logo_commid20_logolar
        "21" -> R.drawable.logo_commid21_supertoledo
        "23" -> R.drawable.logo_commid23_axion
        "24" -> R.drawable.logo_commid24_farmacity
        "2" -> R.drawable.logo_commid2_laanonima
        "3001" -> R.drawable.logo_commid3001_easy
        "36" -> R.drawable.logo_commid36_clc
        "5" -> R.drawable.logo_commid5_californiasa
        "6" -> R.drawable.logo_commid6_comodin
        "8" -> R.drawable.logo_commid8_marianomax
        "9" -> R.drawable.logo_commid9_vea_cencosud
        else -> R.drawable.markers_ratonean2
    }
}