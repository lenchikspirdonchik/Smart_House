package spiridonov.smart_house.domain.roomItem

class GetRoomListUseCase(private val roomListRepository: RoomListRepository) {
    fun getRoomList(): List<RoomItem> = roomListRepository.getRoomList()
}