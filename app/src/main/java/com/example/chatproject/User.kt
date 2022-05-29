package com.example.project

class User {
    var name:String? = null
    var email:String? = null
    var uid:String? = null

        get() = field
        set(value){
            field = value
        }
    constructor() {}
    constructor(name: String?, email:String?, uid:String?){
        this.name = name
        this.email = email
        this.uid = uid
    }
}