package me.francis.audioplayerwithequalizer.viewModels

import androidx.lifecycle.ViewModel
import me.francis.audioplayerwithequalizer.services.AudioService

class PlayerViewModel : ViewModel() {

    private val audioService = AudioService()

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
        if (audioService.isPlaying.value) {
            audioService.pause()
        } else {
            audioService.play()
        }
    }

    fun previousTrack(path: String) {
        audioService.skipToPrevious(path = path)
    }

    fun nextTrack(path: String) {
        audioService.skipToNext(path = path)
    }
}
