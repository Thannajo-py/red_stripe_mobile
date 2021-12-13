package com.example.filrouge

import org.mindrot.jbcrypt.BCrypt

/**
 *hash a plaintext password using the typical log rounds (10)
 * @return encrypted password
 */
fun generateHashedPass(pass: String) = BCrypt.hashpw(pass, BCrypt.gensalt())


/**
 *compare password to hash version
 * @return true if password matches hash
 */
fun isValid(clearTextPassword: String, hashedPass: String) =
    BCrypt.checkpw(clearTextPassword, hashedPass)

