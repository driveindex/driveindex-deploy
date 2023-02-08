package io.github.driveindex.dto.req.admin

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

/**
 * @author sgpublic
 * @Date 2023/2/7 15:33
 */
data class LoginReqDto(
    @field:Schema(description = "管理员密码", required = true)
    val password: String
): Serializable