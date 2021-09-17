package com.raiyanshahid.letstalk.groups.Model


class GroupList_Model {

    var groupTitle: String? = null
    var groupId: String? = null
    var timestamp: String? = null
    var groupImage: String? = null
    var groupDescription: String? = null
    var createdBy: String? = null
    var admin: String? = null


    constructor() {}
    constructor(groupTitle: String?, groupId: String?, timestamp: String?, groupImage: String?,
                groupDescription: String?,createdBy: String?,admin: String?)
    {
        this.groupTitle = groupTitle
        this.groupId = groupId
        this.timestamp = timestamp
        this.groupImage = groupImage
        this.groupDescription = groupDescription
        this.createdBy = createdBy
        this.admin = admin
    }
}