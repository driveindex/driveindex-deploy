package io.github.driveindex.database.dao

import io.github.driveindex.core.util.CanonicalPath
import io.github.driveindex.database.entity.FileEntity
import io.github.driveindex.exception.FailedResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

/**
 * @author sgpublic
 * @Date 2023/3/29 下午12:54
 */
@Repository
interface FileDao: JpaRepository<FileEntity, UUID> {
    @Modifying
    @Query("delete FileEntity where id=:id")
    fun deleteByUUID(id: UUID)

    @Modifying
    @Query("update FileEntity set name=:name where id=:id")
    fun rename(id: UUID, name: String)

    @Query("from FileEntity where pathHash=:pathHash and createBy=:createBy")
    fun findVirtualByPath(pathHash: String, createBy: UUID): FileEntity?

    @Query("from FileEntity where pathHash=:pathHash and accountId=:account")
    fun findLinkedByPath(pathHash: String, account: UUID): FileEntity?

    @Query("from FileEntity where id=:parent")
    fun findByUUID(parent: UUID): FileEntity?

    @Query("from FileEntity where parentId=:parent")
    fun findByParent(parent: UUID): List<FileEntity>

    @Query("from FileEntity where accountId=:account")
    fun listByAccount(account: UUID): List<FileEntity>

    @Query("from FileEntity where createBy=:user")
    fun listByUser(user: UUID): List<FileEntity>
}

/**
 * 根据指定路径，寻找用户创建的本地目录，若指定目录为
 */
fun FileDao.getLocalUserFile(path: CanonicalPath, createBy: UUID): FileEntity {
    val target = findTopUserFile(path, createBy)
    if (path != target.path) {
        throw FailedResult.Dir.ModifyRemote
    }
    return target
}

/**
 * 根据指定路径，向上寻找由用户创建的软连接
 */
fun FileDao.findTopUserFile(path: CanonicalPath, createBy: UUID): FileEntity {
    var entity: FileEntity? = findVirtualByPath(path.pathSha256, createBy)
    while (entity != null && entity.isRemote) {
        entity = findByUUID(entity.parentId ?: break)
    }
    return entity ?: throw FailedResult.Dir.TargetNotFound
}
