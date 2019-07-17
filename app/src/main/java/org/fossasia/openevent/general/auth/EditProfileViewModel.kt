package org.fossasia.openevent.general.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.fossasia.openevent.general.utils.extensions.withDefaultSchedulers
import org.fossasia.openevent.general.R
import org.fossasia.openevent.general.common.SingleLiveEvent
import org.fossasia.openevent.general.data.Resource
import timber.log.Timber
import java.io.File

class EditProfileViewModel(
    private val authService: AuthService,
    private val authHolder: AuthHolder,
    private val resource: Resource
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val mutableProgress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = mutableProgress
    private val mutableUser = MutableLiveData<User>()
    val user: LiveData<User> = mutableUser
    private val mutableMessage = SingleLiveEvent<String>()
    val message: LiveData<String> = mutableMessage
    private var updatedImageTemp = MutableLiveData<File>()
    var avatarUpdated = false
    var encodedImage: String? = null

    fun getId() = authHolder.getId()

    fun isLoggedIn() = authService.isLoggedIn()

    fun updateProfile(user: User) {
        if (encodedImage.isNullOrEmpty()) {
            updateUser(user)
            return
        }
        compositeDisposable += authService.uploadImage(UploadImage(encodedImage))
            .withDefaultSchedulers()
            .doOnSubscribe {
                mutableProgress.value = true
            }
            .doFinally {
                mutableProgress.value = false
            }
            .subscribe({
                updateUser(user.copy(avatarUrl = it.url))
                mutableMessage.value = resource.getString(R.string.image_upload_success_message)
                Timber.d("Image uploaded ${it.url}")
            }) {
                mutableMessage.value = resource.getString(R.string.image_upload_error_message)
                Timber.e(it, "Error uploading user!")
            }
    }

    private fun updateUser(user: User) {
        compositeDisposable += authService.updateUser(user)
            .withDefaultSchedulers()
            .doOnSubscribe {
                mutableProgress.value = true
            }
            .doFinally {
                mutableProgress.value = false
            }
            .subscribe({
                mutableMessage.value = resource.getString(R.string.user_update_success_message)
                Timber.d("User updated")
            }) {
                mutableMessage.value = resource.getString(R.string.user_update_error_message)
                Timber.e(it, "Error updating user!")
            }
    }

    fun setUpdatedTempFile(file: File) {
        updatedImageTemp.value = file
    }

    fun getUpdatedTempFile(): MutableLiveData<File> {
        return updatedImageTemp
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
