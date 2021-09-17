package com.raiyanshahid.letstalk.publicGroup.Model

class PublicGroup_Model {

    var groupid: String? = null
    var title: String? = null
    var description: String ?= null
    var url : String ?= null
    var topic : String ?= null
    var groupImage : String ?= null
    var profileImage : String ?= null
    var createdBy : String ?= null



    constructor(
        groupid: String?,
        title: String?,
        description: String?,
        url: String?,
        topic: String?,
        groupImage: String?,
        profileImage: String?,
        createdBy : String?
    ) {
        this.groupid = groupid
        this.title = title
        this.description = description
        this.url = url
        this.topic = topic
        this.groupImage = groupImage
        this.profileImage = profileImage
        this.createdBy = createdBy
    }

    constructor()
}