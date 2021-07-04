package com.example.theproductivityapp.db

import timber.log.Timber

class Utils {

    companion object {
        const val DB_NAME = "Todos"
        const val TODO = "VERTICAL"
        const val TAG = "HORIZONTAL"
        val IMP_UR = "ImportantHigh"
        val IMP_NUR = "ImportantLow"
        val NIMP_UR = "Not ImportantHigh"
        val NIMP_NUR = "Not ImportantLow"
        val QuadrantSharedPrefs = "SharedPreferencesForQuadrants"
    }
}