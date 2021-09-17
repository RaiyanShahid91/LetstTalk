package com.raiyanshahid.letstalk.publicGroup.Model

class Topic_selected {
    private var sdg: Int? = null
    private var select: String? = null
    private var position: String? = null

    constructor()


    constructor(sdg: Int?, select: String?,position: String? ) {
        this.sdg = sdg
        this.select = select
        this.position = position
    }

    fun getSdg(): Int? {
        return sdg
    }

    fun setSdg(sdg: Int?) {
        this.sdg = sdg
    }

    fun getSelect(): String? {
        return select
    }

    fun setSelect(select: String?) {
        this.select = select
    }

    fun getPosition(): String? {
        return position
    }

    fun setPosition(position: String?) {
        this.position = position
    }
}