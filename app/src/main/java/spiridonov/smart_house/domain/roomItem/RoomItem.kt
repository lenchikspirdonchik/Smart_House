package spiridonov.smart_house.domain.roomItem

class RoomItem(
    val roomName: String,
    val status: String = "",
    val Sensor0: Int = UNDEFINED_DATA,
    val Sensor1: Int = UNDEFINED_DATA,
    val Sensor2: Int = UNDEFINED_DATA,
    val whenTurnOnFan: Int = UNDEFINED_DATA,
    val isFanWork: Boolean = false,
    val isFanWorkRoot: Boolean = false,
    val isLightOn: Boolean = false,
) {
    companion object {
        const val UNDEFINED_DATA = -1
    }
}