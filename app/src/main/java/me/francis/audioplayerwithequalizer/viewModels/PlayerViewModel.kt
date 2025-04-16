package me.francis.audioplayerwithequalizer.viewModels

import androidx.lifecycle.ViewModel

class PlayerViewModel() : ViewModel() {

    // todo: reaproveitar para estados da aplicação (play/pause, etc)
//    private val _uiUserListState = MutableStateFlow(UserListUiState())
//    val uiListViewState = _uiUserListState.asStateFlow()

//    private val _uiUserDetailViewState = MutableStateFlow(UserDetailUiState())
//    val uiUserDetailViewState = _uiUserDetailViewState.asStateFlow()

//    private val _uiEqualizerViewState = MutableStateFlow(EqualizerUiState())
//    val uiEqualizerViewState = _uiEqualizerViewState.asStateFlow()

//    init {
//        viewModelScope.launch {
//            userDao.getUsers().collectLatest {
//                println("********** state $it")
//                _uiUserListState.update { currenState ->
//                    currenState.copy(userList = it)
//                }
//            }
//        }
//    }

    fun playPause() {
        println("*** play pause")
    }

    fun previousTrack() {
        println("*** previous track")
    }

    fun nextTrack() {
        println("*** next track")
    }
}
