package com.codemave.mobilecomputing.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.versionedparcelable.VersionedParcelize


@Entity(
    tableName = "payments",
    indices = [
        Index("id", unique = true),
        Index("payment_category_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["payment_category_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Payment(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val paymentId: Long = 0,
    @ColumnInfo(name = "payment_title") val paymentTitle: String,
    @ColumnInfo(name = "payment_date") val paymentDate: Long,
    @ColumnInfo(name = "payment_category_id") val paymentCategoryId: Long,
    @ColumnInfo(name = "payment_amount") val paymentAmount: Double,
    @ColumnInfo(name = "payment_active") val paymentActive: Boolean,
    @ColumnInfo(name = "payment_notifications") val paymentHowManyNotifications: Int,
    @ColumnInfo(name = "payment_locationX") val paymentLocationX: Double,
    @ColumnInfo(name = "payment_locationY") val paymentLocationY: Double,
    @ColumnInfo(name = "payment_flocationX") val locationX: Double,
    @ColumnInfo(name = "payment_flocationY") val locationY: Double
)