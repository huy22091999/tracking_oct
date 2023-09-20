package com.oceantech.tracking.data.model

import java.io.Serializable

data class Tracking(var content:String, var date:String, val id: Int, val user: User? ) :Serializable{
}