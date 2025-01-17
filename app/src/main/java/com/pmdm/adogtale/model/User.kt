package com.pmdm.adogtale.model

import java.io.Serializable

data class User (
    var username: String,
    var email: String,
    var password: String,
    var town: String? = null,
    var phone: String? = null,
    var name: String? = null,
    var surname: String? = null,
) : Serializable