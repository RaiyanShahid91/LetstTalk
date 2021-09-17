package com.raiyanshahid.letstalk.publicGroup.TopicInterface

class SelectedTopicModel {
    private var position: String? = null

    constructor()


    constructor( position: String?) {

        this.position = position
    }



    fun getPosition(): String? {
        return position
    }

    fun setPosition(position: String?) {
        this.position = position
    }
}