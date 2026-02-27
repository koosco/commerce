package com.koosco.catalogservice.application.usecase

import com.koosco.catalogservice.application.command.SearchClickCommand
import com.koosco.catalogservice.application.port.SearchClickLogPort
import com.koosco.common.core.annotation.UseCase
import org.slf4j.LoggerFactory

/**
 * 검색 결과 클릭 로그를 기록하는 유스케이스.
 * mAP@k 품질 지표 계산의 기반 데이터를 수집한다.
 */
@UseCase
class RecordSearchClickUseCase(private val searchClickLogPort: SearchClickLogPort) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(command: SearchClickCommand) {
        searchClickLogPort.save(command)

        logger.debug(
            "Recorded search click: query={}, productId={}, position={}/{}",
            command.searchQuery,
            command.clickedProductId,
            command.clickPosition,
            command.totalResults,
        )
    }
}
