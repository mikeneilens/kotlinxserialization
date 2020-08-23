import org.junit.jupiter.api.Test
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.assertEquals

class MainTest {
    @Test
    fun `simple example of encoding a class to a string`() {

        @Serializable
        class Sample (val name:String)

        val sample = Sample("mike")
        val encoded = Json.encodeToString(sample)
        assertEquals("""{"name":"mike"}""", encoded)
    }

    @Test
    fun `simple example of decoding a string containing json into an object`() {

        @Serializable
        class Sample (val name:String)

        val json =  """
                    {"name":"mike"}
                    """
        val decoded = Json.decodeFromString<Sample>(json)
        assertEquals("mike", decoded.name)
    }
    @Test
    fun `simple example of encoding a class with some properties computed`() {

        @Serializable
        data class Sample (val firstName:String, val lastName:String  ) {
            val fullName:String get() = "$firstName $lastName"
        }

        val sample = Sample("mike", "neilens")
        val encoded = Json.encodeToString(sample)
        assertEquals("mike neilens", sample.fullName) //fullname has no backing field so is not encoded into json
        assertEquals("""{"firstName":"mike","lastName":"neilens"}""", encoded)
    }
    @Test
    fun `simple example of deccoding json into a class with default property values`() {
        @Serializable
        data class Sample (val firstName:String, val lastName:String="blogs" )
        val json =  """{
                      "firstName":"mike",
                      "lastName":"neilens"
                    }"""
        val decoded = Json.decodeFromString<Sample>(json)
        assertEquals("mike", decoded.firstName)
        assertEquals("neilens", decoded.lastName)

        val json2 = """{
                      "firstName":"mike"
                    }"""
        val decoded2 = Json.decodeFromString<Sample>(json2)
        assertEquals("mike", decoded2.firstName)
        assertEquals("blogs", decoded2.lastName)
    }

    @Test
    fun `simple example of encoding with "pretty print" set`() {
        @Serializable
        data class Sample (val firstName:String, val lastName:String="blogs" )
        val jsonFormatter = Json { prettyPrint = true}
        val sample = Sample("mike", "neilens")
        val encoded = jsonFormatter.encodeToString(sample)

        println(encoded)
        assertEquals("""{
    "firstName": "mike",
    "lastName": "neilens"
}""", encoded)
    }
    @Test
    fun `simple example of lenient decoding`() {
        val jsonFormatter = Json { isLenient = true}
        @Serializable
        data class Sample (val firstName:String, val lastName:String="blogs" )
        val json =  """{
                      "firstName":mike,
                      "lastName":neilens
                    }""" //this should not decode because strings should be quoted

        val decoded = jsonFormatter.decodeFromString<Sample>(json)
        assertEquals("mike", decoded.firstName)
        assertEquals("neilens", decoded.lastName)
    }

    @Test
    fun `simple example of ignoring unwanted keys when decoding`() {
        val jsonFormatter = Json { ignoreUnknownKeys = true}
        @Serializable
        data class Sample (val firstName:String, val lastName:String="blogs" )
        val json =  """{
                      "firstName":"mike",
                      "lastName":"neilens"
                      "placeOfBirth":"St Helens"
                    }""" //this should not decode because strings should be quoted

        val decoded = jsonFormatter.decodeFromString<Sample>(json)
        assertEquals("mike", decoded.firstName)
        assertEquals("neilens", decoded.lastName)
    }
}