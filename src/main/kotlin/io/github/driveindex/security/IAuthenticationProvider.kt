package io.github.driveindex.security

import io.github.driveindex.core.ConfigManager
import io.github.driveindex.exception.WrongPasswordException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component

/**
 * @author sgpublic
 * @Date 2023/2/7 15:55
 */
@Component
class IAuthenticationProvider: AuthenticationProvider {
    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication? {
        authentication as PasswordOnlyToken
        if (authentication.credentials == ConfigManager.Password) {
            return PasswordOnlyToken.authenticated(authentication.credentials, SecurityConfig.AUTH_ADMIN)
        }
        throw WrongPasswordException()
    }

    override fun supports(authentication: Class<*>): Boolean {
        return authentication == PasswordOnlyToken::class.java
    }
}