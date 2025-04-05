package  com.example.mad_project_house_booking

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.State

class AuthViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }
    //to get personal info of users
    private val _username = mutableStateOf("User")
    val username: State<String> get() = _username
    private val _email = mutableStateOf("Email not found")
    val email: State<String> get() = _email

    private val _contact = mutableStateOf("Contact not found")
    val contact: State<String> get() = _contact

    fun fetchUserProfile(uid: String) {
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    _username.value = doc.getString("name") ?: "User"
                    _email.value = doc.getString("email") ?: "Email not found"
                    _contact.value = doc.getString("contact") ?: "Contact not found"
                }
            }
    }


    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
        }else{
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email : String,password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

    fun signup(email : String,password : String,name:String,contact: String){

        if(email.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid ?: return@addOnCompleteListener

                    // Create a user object
                    val userData = hashMapOf(
                        "uid" to uid,
                        "name" to name,
                        "email" to email,
                        "contact" to contact
                    )

                    // Store user data in Firestore
                    db.collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            _authState.value = AuthState.Authenticated
                        }
                        .addOnFailureListener { e ->
                            _authState.value = AuthState.Error("Firestore Error: ${e.message}")
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }

    }

    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }


}


sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}