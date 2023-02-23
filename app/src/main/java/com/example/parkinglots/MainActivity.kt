package com.example.parkinglots

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.core.widget.doOnTextChanged
import com.example.parkinglots.ValidateEmail
import com.example.parkinglots.uiload.LoadingDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var userEmail:String
        lateinit var providerSession:String
    }

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private lateinit var emailInit: TextInputEditText
    private lateinit var passwordInit: TextInputEditText
    private lateinit var logIn: Button
    private lateinit var forgottenPassword:TextView
    private lateinit var signUp:Button

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_ParkingLots)
        Thread.sleep(2000)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailInit=findViewById(R.id.emailInit)
        passwordInit=findViewById(R.id.passwordInit)
        logIn=findViewById(R.id.logIn)
        signUp=findViewById(R.id.signUp)
        forgottenPassword=findViewById(R.id.forgottenPassword)
        mAuth=FirebaseAuth.getInstance()

        manageButtonLogin()
        emailInit.doOnTextChanged { text, start, before, count -> manageButtonLogin() }
        passwordInit.doOnTextChanged { text, start, before, count -> manageButtonLogin() }

    }

    public override fun onStart() {
        super.onStart()
        val currentUser=FirebaseAuth.getInstance().currentUser
        if(currentUser!=null) goHome(currentUser.email.toString(),currentUser.providerId)
    }

    override fun onBackPressed() {
        val startMain=Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags=Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }



    private fun manageButtonLogin(){
        var buttonInit=findViewById<TextView>(R.id.logIn)
        email=emailInit.text.toString()
        password=passwordInit.text.toString()

        if(TextUtils.isEmpty(password)|| ValidateEmail.isEmail(email)==false){
            buttonInit.setBackgroundColor(ContextCompat.getColor(this,R.color.gray))
            buttonInit.isEnabled=false
        }else{

            buttonInit.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.green_button))
            buttonInit.isEnabled=true
        }
    }

    fun login(view: View){
        loginUser()
    }

    private fun loginUser(){
        email=emailInit.text.toString()
        password=passwordInit.text.toString()

        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){task->
                if(task.isSuccessful) {
                    goHome(email,"Email")

                    val loading = LoadingDialog(this)
                    loading.startLoading()
                    val handler = Handler()
                    handler.postDelayed(object :Runnable{
                        override fun run() {
                            loading.isDismiss()
                        }

                    },3000)
                } else{
                    Toast.makeText(this,"Incorrect email or password", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun goHome(email:String,provider:String){
        userEmail=email
        providerSession=provider

        val intent=Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }

    fun forgotPassword(view: View){
        resetPassword()
    }

    private fun resetPassword(){
        var e=emailInit.text.toString()
        if(!TextUtils.isEmpty(e)){
            mAuth.sendPasswordResetEmail(e)
                .addOnCompleteListener{task->
                    if(task.isSuccessful) Toast.makeText(this,"Correo enviado a $e", Toast.LENGTH_LONG).show()
                    else Toast.makeText(this,"No se encontro al usuario con este correo", Toast.LENGTH_LONG).show()
                }
        }else Toast.makeText(this,"Por favor indica un email", Toast.LENGTH_LONG).show()
    }

    fun signUp(view: View){
        goSignUp()
    }

    private fun goSignUp(){
        val intent= Intent(this,SignInAct::class.java)
        startActivity(intent)
    }
}