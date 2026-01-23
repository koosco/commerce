package com.koosco.common.core.util

import com.fasterxml.jackson.core.type.TypeReference
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonUtilsTest {

    data class TestData(
        val name: String,
        val value: Int,
        val timestamp: Instant? = null,
    )

    @Test
    fun `toJson should serialize object to JSON string`() {
        val data = TestData("test", 123)

        val json = JsonUtils.toJson(data)

        assertNotNull(json)
        assertTrue(json.contains("\"name\":\"test\""))
        assertTrue(json.contains("\"value\":123"))
    }

    @Test
    fun `toJson with null should return null`() {
        val json = JsonUtils.toJson(null)
        assertNull(json)
    }

    @Test
    fun `fromJson should deserialize JSON to object`() {
        val json = """{"name":"test","value":123}"""

        val result: TestData? = JsonUtils.fromJson(json)

        assertNotNull(result)
        assertEquals("test", result.name)
        assertEquals(123, result.value)
    }

    @Test
    fun `fromJson with null should return null`() {
        val result: TestData? = JsonUtils.fromJson(null)
        assertNull(result)
    }

    @Test
    fun `fromJson with blank should return null`() {
        val result: TestData? = JsonUtils.fromJson("")
        assertNull(result)
    }

    @Test
    fun `fromJson should handle unknown properties gracefully`() {
        val json = """{"name":"test","value":123,"unknown":"field"}"""

        val result: TestData? = JsonUtils.fromJson(json)

        assertNotNull(result)
        assertEquals("test", result.name)
    }

    @Test
    fun `fromJson with TypeReference should deserialize complex types`() {
        val json = """[{"name":"a","value":1},{"name":"b","value":2}]"""

        val result = JsonUtils.fromJson(json, object : TypeReference<List<TestData>>() {})

        assertNotNull(result)
        assertEquals(2, result.size)
        assertEquals("a", result[0].name)
        assertEquals("b", result[1].name)
    }

    @Test
    fun `toPrettyJson should produce formatted output`() {
        val data = TestData("test", 123)

        val json = JsonUtils.toPrettyJson(data)

        assertNotNull(json)
        assertTrue(json.contains("\n"))
    }

    @Test
    fun `parseJson should return JsonNode for dynamic access`() {
        val json = """{"name":"test","nested":{"key":"value"}}"""

        val node = JsonUtils.parseJson(json)

        assertNotNull(node)
        assertEquals("test", node.get("name").asText())
        assertEquals("value", node.get("nested").get("key").asText())
    }

    @Test
    fun `isValidJson should return true for valid JSON`() {
        assertTrue(JsonUtils.isValidJson("""{"key":"value"}"""))
        assertTrue(JsonUtils.isValidJson("""[1,2,3]"""))
        assertTrue(JsonUtils.isValidJson(""""string""""))
    }

    @Test
    fun `isValidJson should return false for invalid JSON`() {
        assertFalse(JsonUtils.isValidJson("not json"))
        assertFalse(JsonUtils.isValidJson("{invalid}"))
        assertFalse(JsonUtils.isValidJson(null))
        assertFalse(JsonUtils.isValidJson(""))
    }

    @Test
    fun `convertValue should convert map to data class`() {
        val map = mapOf("name" to "test", "value" to 123)

        val result: TestData? = JsonUtils.convertValue(map)

        assertNotNull(result)
        assertEquals("test", result.name)
        assertEquals(123, result.value)
    }

    @Test
    fun `should serialize and deserialize Instant correctly`() {
        val now = Instant.now()
        val data = TestData("test", 123, now)

        val json = JsonUtils.toJson(data)
        val result: TestData? = JsonUtils.fromJson(json)

        assertNotNull(result)
        assertNotNull(result.timestamp)
        assertEquals(now, result.timestamp)
    }
}
