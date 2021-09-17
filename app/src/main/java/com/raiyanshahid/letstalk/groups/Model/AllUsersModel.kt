package com.raiyanshahid.letstalk.groups.Model

class AllUsersModel {
    private var uid: String? = null
    private var name: String? = null
    private var userImage: String? = null

    constructor() {}

    constructor(uid: String?, name: String?, userImage : String?) {
        this.uid = uid
        this.name = name
        this.userImage = userImage
    }

    fun getUid(): String? {
        return uid
    }

    fun setUid(uid: String?) {
        this.uid = uid
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getUserImage(): String? {
        return userImage
    }

    fun setUserImage(userImage: String?) {
        this.userImage = userImage
    }

}