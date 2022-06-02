package spiridonov.smart_house.domain.roomItem

class AddRoomItemUseCase(private val roomListRepository: RoomListRepository) {
    fun addRoomItem(roomItem: RoomItem) = roomListRepository.addRoomItem(roomItem)
}