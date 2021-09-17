package com.raiyanshahid.letstalk.Models

class Profile_Model {



    var image: String? = null

    constructor() {}
    constructor(image: String?)
    {
        this.image = image
    }

    @JvmName("getMessage1")
    fun getImage(): String? {
        return image
    }

    @JvmName("setMessage1")
    fun setUserImage(image: String?) {
        this.image = image
    }

}