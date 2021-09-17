package com.raiyanshahid.letstalk.groups.Model

class ParticipantsUser_Model
{
     var uid: String? = null
     var name: String? = null
     var userImage: String? = null

    constructor()
    constructor(uid: String?, name: String?, userImage: String?) {
        this.uid = uid
        this.name = name
        this.userImage = userImage
    }


}