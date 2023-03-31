package io.github.driveindex.exception

import io.github.driveindex.Application
import io.github.driveindex.core.util.toGson
import io.github.driveindex.dto.resp.RespResult
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author sgpublic
 * @Date 2023/2/8 9:15
 */
class FailedResult private constructor(
    val code: Int,
    override val message: String,
    private val params: Map<String, Any>? = null
): RuntimeException(message) {
    companion object {
        val UnsupportedRequest get() = FailedResult(-4001, "不支持的请求方式")
        val MissingBody get() = FailedResult(-4002, "参数缺失")
        val AnonymousDenied get() = FailedResult(-4050, "请登陆后再试")
        val NotFound get() = FailedResult(-4040, "您请求的资源不存在")

        val ServiceUnavailable get() = FailedResult(-5001, "服务不可用")
        val InternalServerError get() = FailedResult(-5002, "服务器内部错误")
        val ServerProcessingError get() = FailedResult(-5003, "请求处理出错")
        val NotImplementationError get() = FailedResult(-5004, "别买炒饭了，头发快掉光了(´╥ω╥`)")

        val BadGateway get() = FailedResult(-5020, "上游服务器响应错误，请查阅日志")
    }

    fun resp(): FailedRespResult {
        return FailedRespResult(code, message, params)
    }

    object UserSettings {
        val PasswordNotMatched get() = FailedResult(-100101, "密码不匹配")
        val PasswordLength get() = FailedResult(-100101, "密码至少 6 位")
        val DeltaTrackDuration get() = FailedResult(-100202, "文件同步间隔时间只能为正整数")
    }

    object Auth {
        val AccessDenied get() = FailedResult(-110101, "非常抱歉，您暂时不能访问")
        val WrongPassword get() = FailedResult(-110102, "用户不存在或密码错误")
        val UserDisabled get() = FailedResult(-110103, "您的用户已被禁用，请联系管理员")
        val UserDeleted get() = FailedResult(-110104, "您的用户已被删除，请联系管理员")
        val ExpiredToken get() = FailedResult(-110105, "无效的 token")

        val IllegalRequest get() = FailedResult(-110201, "无效的登录验证请求")
        val AuthTimeout get() = FailedResult(-110202, "登录超时，请重试")
        val DuplicateAccount get() = FailedResult(-110203, "此账户已登录到 ${Application.APPLICATION_BASE_NAME}")
    }

    object Client {
        val NotFound get() = FailedResult(-120101, "Client 不存在")
        val TypeNotMatch get() = FailedResult(-120102, "Client 类型不匹配")

        val DuplicateClientName get() = FailedResult(-120201, "Client 名称已存在")
        fun DuplicateClientInfo(name: String, id: UUID) =
            FailedResult(-100301, "此 Client 信息与“$name”相同",
                mapOf("name" to name, "id" to id))

        fun DuplicateAccountName(name: String, id: UUID) =
            FailedResult(-100301, "账号名称已存在：$name",
                mapOf("name" to name, "id" to id))
    }

    object Dir {
        val TargetNotFound get() = FailedResult(-130101, "找不到指定目录或指定了一个非本地目录")
        val ModifyRoot get() = FailedResult(-130102, "无法操作 root 目录")
        val ModifyRemote get() = FailedResult(-130103, "暂不支持操作远端目录")
    }
}

class FailedRespResult(code: Int, message: String, params: Map<String, Any>?)
    : RespResult<Map<String, Any>>(code, message, params)

fun HttpServletResponse.write(result: Serializable) {
    characterEncoding = StandardCharsets.UTF_8.name()
    addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    writer.use {
        it.write(result.toGson())
        it.flush()
    }
}