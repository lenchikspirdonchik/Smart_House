package spiridonov.smart_house.domain.roomItem

class EditRoomItemUseCase(private val roomListRepository: RoomListRepository) {
    fun editRoomItem(roomItem: RoomItem) = roomListRepository.editRoomItem(roomItem)
}