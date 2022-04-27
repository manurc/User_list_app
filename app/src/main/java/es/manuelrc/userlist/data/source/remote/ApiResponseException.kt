package es.manuelrc.userlist.data.source.remote

class ApiResponseException(val code:Int, msg: String? = null) :
    Exception(msg)
