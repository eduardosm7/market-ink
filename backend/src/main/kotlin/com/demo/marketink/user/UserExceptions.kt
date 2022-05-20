package com.demo.marketink.user

import java.lang.RuntimeException

class UserNotFoundException(message: String = "User not found") : RuntimeException(message)

class UserNameAlreadyInUseException(message: String = "Username already in use") : RuntimeException(message)
