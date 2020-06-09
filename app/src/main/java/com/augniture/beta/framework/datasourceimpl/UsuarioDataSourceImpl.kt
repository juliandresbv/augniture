package com.augniture.beta.framework.datasourceimpl

import android.content.Context
import android.util.Log
import com.augniture.beta.data.datasource.UsuarioDataSource
import com.augniture.beta.domain.CredencialesUsuario
import com.augniture.beta.domain.RegistroUsuario
import com.augniture.beta.domain.Usuario
import com.augniture.beta.framework.firebase.auth.AugnitureFirebaseAuthManager
import com.augniture.beta.framework.firebase.auth.FirebaseAuthCons
import com.augniture.beta.framework.firebase.dbconfig.AugnitureFirebaseFirestoneDatabaseManager
import com.augniture.beta.framework.network.NetworkManager
import com.augniture.beta.framework.room.dbconfig.AugnitureRoomDatabaseManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.*
import java.lang.Exception
import kotlin.coroutines.coroutineContext

class UsuarioDataSourceImpl(context: Context) : UsuarioDataSource {


    // Check if device has Internet connection
    private var isOnline = NetworkManager.isOnline(context)

    /*
    Firebase Auth
    */
    private val firebaseAuthInstance = AugnitureFirebaseAuthManager.getFirebaseAuthInstance()


    /*
    Room
    Endpoint to local persistence with Room.
    */
    private val usuarioDao = AugnitureRoomDatabaseManager.getInstance(context).usuarioDao()

    /*
    Firebase
    Endpoint to cloud persistence with Firestone.
     */
    private val usuarioFirestoneCollection = AugnitureFirebaseFirestoneDatabaseManager
        .getFirestoneInstance()
        .collection(AugnitureFirebaseFirestoneDatabaseManager.USUARIOS_COLLECTION)

    override suspend fun signIn(credencialesUsuario: CredencialesUsuario, proveedor: String): Usuario {
        isOnline = NetworkManager.isOnlineSimple()

        if (isOnline) {
            if (proveedor == FirebaseAuthCons.EMAIL_PROVIDER) {

                return try {
                    val authResult: AuthResult = firebaseAuthInstance
                        .signInWithEmailAndPassword(
                            credencialesUsuario.email!!,
                            credencialesUsuario.contrasena!!
                        )
                        .await()

                    val firebaseUser = authResult.user
                    var usuario = Usuario.USUARIO_NULO

                    // Change attributes if the user is retrieved from FirebaseAuth
                    if (firebaseUser != null) {
                        usuario.id = firebaseUser.uid
                        usuario.nombre = firebaseUser.displayName
                        usuario.email = firebaseUser.email
                        usuario.foto = firebaseUser.photoUrl.toString()
                    }

                    usuario
                } catch (e: Exception) {
                    return Usuario.USUARIO_EXCEPCION
                }

            }
            else {

                return try {
                    val tokenId = credencialesUsuario.email
                    var credencial = GoogleAuthProvider.getCredential(tokenId, null)

                    val authResult = firebaseAuthInstance.signInWithCredential(credencial).await()

                    val firebaseUser = authResult.user
                    var usuario = Usuario.USUARIO_NULO

                    // Change attributes if the user is retrieved from FirebaseAuth
                    if (firebaseUser != null) {
                        usuario.id = firebaseUser.uid
                        usuario.nombre = firebaseUser.displayName
                        usuario.email = firebaseUser.email
                        usuario.foto = firebaseUser.photoUrl.toString()
                    }

                    usuario
                }
                catch (e: Exception) {
                    return Usuario.USUARIO_EXCEPCION
                }

            }
        }
        else {
            return Usuario.USUARIO_NO_CONEXION
        }

    }

    override suspend fun register(registroUsuario: RegistroUsuario): Usuario {
        isOnline = NetworkManager.isOnlineSimple()

        if (isOnline) {
            return try {
                Log.i("RESGITER", "INICIO")
                val authResult: AuthResult = firebaseAuthInstance
                    .createUserWithEmailAndPassword(
                        registroUsuario.email!!,
                        registroUsuario.contrasena!!
                    )
                    .await()

                val usuarioNuevoFirebase = authResult.user
                Log.i("USUARIO ", usuarioNuevoFirebase?.uid)

                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(registroUsuario.nombre!!)
                    .build()

                usuarioNuevoFirebase?.updateProfile(profileUpdate)?.await()

                var usuarioNuevo = Usuario.USUARIO_NULO

                if (usuarioNuevoFirebase != null) {
                    usuarioNuevo.id = usuarioNuevoFirebase.uid
                    usuarioNuevo.nombre = usuarioNuevoFirebase.displayName
                    usuarioNuevo.email = usuarioNuevoFirebase.email
                    usuarioNuevo.foto = usuarioNuevoFirebase.photoUrl.toString()
                }

                Log.i("USUARIO N ", usuarioNuevo?.id)

                usuarioNuevo
            }
            catch (e: Exception) {
                return Usuario.USUARIO_EXCEPCION
            }

        }
        else {
            return Usuario.USUARIO_NO_CONEXION
        }

    }

    override suspend fun signOut() {
        try {
            firebaseAuthInstance.signOut()
        }
        catch (e: Exception) {

        }
    }

    override suspend fun getCurrent(): Usuario {

        return try {
            val firebaseCurrentUser = firebaseAuthInstance.currentUser
            val usuarioActual = Usuario.USUARIO_NULO

            if (firebaseCurrentUser != null) {
                usuarioActual.id = firebaseCurrentUser.uid
                usuarioActual.nombre = firebaseCurrentUser.displayName
                usuarioActual.email = firebaseCurrentUser.email
                usuarioActual.foto = firebaseCurrentUser.photoUrl.toString()
            }

            usuarioActual
        }
        catch (e: Exception) {
            return Usuario.USUARIO_EXCEPCION
        }

    }

    override suspend fun add(usuario: Usuario) {
        /*
        return usuarioDao.addUsuario(
            ProductoEntity(

            )
        )
        */
    }

}