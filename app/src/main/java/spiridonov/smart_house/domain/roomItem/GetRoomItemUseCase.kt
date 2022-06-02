package spiridonov.smart_house.domain.roomItem

class GetRoomItemUseCase(private val roomListRepository: RoomListRepository) {
    fun getRoomItem(roomItemName: String): RoomItem = roomListRepository.getRoomItem(roomItemName)

}