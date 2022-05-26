package serialization

import model.Point

object Serialization {
    fun getPointFromJson(json: String) : Point {
        val name = json.substring(9 until json.indexOf("\",\""))
        val x = json.substring(json.indexOf("\"x\"")+4
                until json.indexOf(",\"y\"")).toDouble()
        val y = json.substring(json.indexOf("\"y\"")+4
                until json.indexOf(",\"direction\"")).toDouble()
        val direction = json.substring(json.indexOf("\"direction\"")+12
                until json.indexOf('}')).toInt()
        return Point(name, x, y, direction)
    }
    fun getJsonFromPoint(point: Point) : String {
        return """{"name":"${point.name}","x":${point.x},"y":${point.y},"direction":${point.direction}}"""
    }
}