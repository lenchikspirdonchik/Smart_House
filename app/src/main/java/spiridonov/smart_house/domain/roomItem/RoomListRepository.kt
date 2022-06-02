package spiridonov.smart_house.domain.roomItem

interface RoomListRepository {
    fun addRoomItem(roomItem: RoomItem)
    fun getRoomList(): List<RoomItem>
    fun getRoomItem(roomItemName: String): RoomItem
    fun editRoomItem(roomItem: RoomItem)
}