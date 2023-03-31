package io.github.driveindex.h2.dao.onedrive

import io.github.driveindex.h2.entity.onedrive.OneDriveClientEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OneDriveClientDao: JpaRepository<OneDriveClientEntity, UUID> {
    @Query("from OneDriveClientEntity where" +
        " id in :ids" +
        " and clientId=:azureClientId" +
        " and clientSecret=:azureClientSecret" +
        " and endPoint=:endPoint" +
        " and tenantId=:tenantId")
    fun findClient(
        ids: List<UUID>, azureClientId: String, azureClientSecret: String,
        endPoint: OneDriveClientEntity.EndPoint, tenantId: String,
    ): OneDriveClientEntity?
}