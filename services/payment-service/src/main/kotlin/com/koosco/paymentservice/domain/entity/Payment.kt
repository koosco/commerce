package com.koosco.paymentservice.domain.entity

import com.koosco.paymentservice.domain.enums.PaymentStatus
import com.koosco.paymentservice.domain.vo.Money
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "payment")
class Payment(

    // 외부 식별자 (노출용)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "payment_id", nullable = false, unique = true, updatable = false)
    val paymentId: UUID = UUID.randomUUID(),

    @Column(name = "order_id", nullable = false)
    val orderId: Long,

    val userId: Long,

    @Embedded
    val amount: Money,
) {

    // DB PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus = PaymentStatus.READY
        protected set

    @OneToMany(
        mappedBy = "payment",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    protected val transactions: MutableList<PaymentTransaction> = mutableListOf()

    fun transactions(): List<PaymentTransaction> = transactions.toList()

    fun approve(transaction: PaymentTransaction) {
        require(status == PaymentStatus.READY) {
            "READY 상태에서만 결제 승인 가능"
        }
        require(transaction.amount == amount) {
            "승인 금액 불일치"
        }
        transactions.add(transaction)
        status = PaymentStatus.APPROVED
    }

    fun fail(transaction: PaymentTransaction) {
        require(status == PaymentStatus.READY) {
            "READY 상태에서만 결제 실패 처리 가능"
        }
        transactions.add(transaction)
        status = PaymentStatus.FAILED
    }

    fun cancel(transaction: PaymentTransaction) {
        require(status == PaymentStatus.APPROVED) {
            "APPROVED 상태에서만 결제 취소 가능"
        }

        val canceled = totalCanceledAmount() + transaction.amount
        require(canceled <= amount) {
            "취소 금액 초과"
        }

        transactions.add(transaction)
        status = PaymentStatus.CANCELED
    }

    fun totalCanceledAmount(): Money = transactions
        .filter { it.type == PaymentTransactionType.CANCEL && it.status == PaymentTransactionStatus.SUCCESS }
        .map { it.amount }
        .fold(Money(0)) { acc, money -> acc + money }
}
