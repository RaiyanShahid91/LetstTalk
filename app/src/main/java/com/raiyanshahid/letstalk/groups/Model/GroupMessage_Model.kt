package com.raiyanshahid.letstalk.groups.Model


class GroupMessage_Model {

    private var ID: Int = 0

    private  var message: String? = null

    private  var sender: String? = null

    private   var username: String?= null

    private   var timestamp: String? = null

    constructor() {}

    constructor(message: String?, sender: String?, username: String?, timestamp: String) {
        this.message = message
        this.sender = sender
        this.username = username
        this.timestamp = timestamp
    }

    fun getID(): Int {
        return ID
    }

    fun setID(ID: Int) {
        this.ID = ID
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getSender(): String? {
        return sender
    }

    fun setSender(sender: String?) {
        this.sender = sender
    }

    fun getUsername(): String? {
        return username
    }

    fun setUsername(username: String?) {
        this.username = username
    }


    fun getTimestamp(): String? {
        return timestamp
    }

    fun setTimestamp(timestamp: String) {
        this.timestamp = timestamp
    }

}