class Audience() {
    var numberAudience : Int = 0
    var teacher : String = ""
    var lessonStart : String = ""
    var lessonEnd : String = ""

    constructor(_numberAudience : Int, _teacher : String, _lessonStart : String, _lessonEnd : String) : this() {
        numberAudience = _numberAudience
        teacher = _teacher
        lessonStart = _lessonStart
        lessonEnd = _lessonEnd
    }
}