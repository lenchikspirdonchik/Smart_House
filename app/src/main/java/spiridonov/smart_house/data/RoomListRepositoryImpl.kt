package spiridonov.smart_house.data

import spiridonov.smart_house.domain.roomItem.RoomItem
import spiridonov.smart_house.domain.roomItem.RoomListRepository

object RoomListRepositoryImpl : RoomListRepository {
    private val roomList = mutableListOf<RoomItem>()
    override fun addRoomItem(roomItem: RoomItem) {
        roomList.add(roomItem)
    }

    override fun getRoomList(): List<RoomItem> {
        //TODO("Here Firebase need to be downloaded")
        return roomList
    }

    override fun getRoomItem(roomItemName: String): RoomItem {
        return roomList.find { it.roomName == roomItemName }
            ?: throw RuntimeException("Element with id $roomItemName not found")
    }

    override fun editRoomItem(roomItem: RoomItem) {
        val oldItem = getRoomItem(roomItem.roomName)
        roomList.remove(oldItem)
        addRoomItem(roomItem)
    }
}