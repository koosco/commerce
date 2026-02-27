package com.koosco.catalogservice.application.port

import com.koosco.catalogservice.application.command.SearchClickCommand

/**
 * 검색 클릭 로그 저장 및 조회 포트.
 * mAP@k 계산을 위한 클릭 데이터를 관리한다.
 */
interface SearchClickLogPort {

    /**
     * 검색 클릭 로그를 저장한다.
     */
    fun save(command: SearchClickCommand)

    /**
     * 특정 검색 쿼리에 대해 클릭된 위치 목록을 조회한다.
     * 반환값은 클릭된 position의 리스트(정렬됨).
     */
    fun getClickPositions(searchQuery: String): List<Int>

    /**
     * 모든 검색 쿼리 키를 조회한다.
     */
    fun getAllSearchQueries(): Set<String>
}
