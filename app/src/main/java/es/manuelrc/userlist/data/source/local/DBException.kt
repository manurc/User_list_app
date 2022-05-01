package es.manuelrc.userlist.data.source.local

import es.manuelrc.userlist.model.exceptions.TypeError

class DBException(val type: TypeError) : Exception()
