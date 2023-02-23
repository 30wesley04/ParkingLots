package com.example.parkinglots

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.util.PatternsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern
import kotlin.properties.Delegates

class SignInAct : AppCompatActivity() {

    private var firstName by Delegates.notNull<String>()
    private lateinit var name: TextInputEditText
    private lateinit var nameLayout:TextInputLayout

    private var last by Delegates.notNull<String>()
    private lateinit var lastName: TextInputEditText
    private lateinit var lastLayout: TextInputLayout

    private var email by Delegates.notNull<String>()
    private lateinit var emailSignIn:TextInputEditText
    private lateinit var emailLayout:TextInputLayout

    private var password by Delegates.notNull<String>()
    private lateinit var passwordSignIn: TextInputEditText
    private lateinit var passwordLayout:TextInputLayout

    private var number by Delegates.notNull<String>()
    private lateinit var phone: TextInputEditText
    private lateinit var phoneLayout:TextInputLayout

    private var password2 by Delegates.notNull<String>()
    private lateinit var repeatPass:TextInputEditText
    private lateinit var repeatPassLayout: TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        name=findViewById(R.id.name)
        nameLayout=findViewById(R.id.nameLayout)

        lastLayout=findViewById(R.id.lastNameLayout)
        lastName=findViewById(R.id.lastName)

        passwordSignIn=findViewById(R.id.passwordSignIn)
        passwordLayout=findViewById(R.id.passwordLayout)

        repeatPass=findViewById(R.id.repeatPass)
        repeatPassLayout=findViewById(R.id.repeatPassLayout)

        emailSignIn=findViewById(R.id.emailSignIn)
        emailLayout=findViewById(R.id.emailLayout)

        phone=findViewById(R.id.phone)
        phoneLayout=findViewById(R.id.phoneLayout)

    }

    fun register(view: View){
        registerUser()
    }
    private fun registerUser(){

        val result= arrayOf(validateEmail(),validatePassword(),validateName(),
            validateLast(),validateNumber())
        if(false in result){
            return
        }
        email=emailSignIn.text.toString();
        password=passwordSignIn.text.toString()
        firstName=name.text.toString();
        last=lastName.text.toString()
        number=phone.text.toString()


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    var dbRegister= FirebaseFirestore.getInstance()
                    dbRegister.collection("Users").document(email).set(hashMapOf(
                        "Name" to firstName,
                        "lastName" to last,
                        "Phone" to number,
                        "Email" to email
                    ))
                    Toast.makeText(this,"Usuario registrado", Toast.LENGTH_LONG).show()
                    goLogIn()
                }
                else{
                    Toast.makeText(this,"Algo salio mal", Toast.LENGTH_LONG).show()
                }
            }

    }

    private fun goLogIn(){
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    private fun validateName():Boolean{
        firstName=name.text.toString();
        return if(firstName.isEmpty()){
            nameLayout.error="The field cannot be empty"
            Toast.makeText(this,"The field name cannot be empty", Toast.LENGTH_LONG).show()
            false
        }else{
            nameLayout.error=null
            true
        }
    }
    private fun validateEmail():Boolean{
        email=emailSignIn.text.toString()
        return if(email.isEmpty()){
            emailLayout.error="The field cannot be empty"
            Toast.makeText(this,"The field email cannot be empty", Toast.LENGTH_LONG).show()
            false
        }else if(!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()){
            emailLayout.error="Please type a valid email"
            Toast.makeText(this,"Please type a valid email", Toast.LENGTH_LONG).show()
            false
        }else{
            emailLayout.error=null
            true
        }
    }
    private fun validateLast():Boolean{
        last=lastName.text.toString()
        return if(last.isEmpty()){
            lastLayout.error="The field cannot be empty"
            Toast.makeText(this,"The field last name cannot be empty", Toast.LENGTH_LONG).show()
            false
        }else{
            lastLayout.error=null
            true
        }
    }
    private fun validateNumber():Boolean{
        number=phone.text.toString()
        val numberRegex= Pattern.compile(
            "^"+
                    "(?=.*[0-9])"
        )
        return if(password.isEmpty()){
            phoneLayout.error="The field cannot be empty"
            false
        }else if(!numberRegex.matcher(number).matches() && number.length!=10){
            phoneLayout.error="Invalid number"
            Toast.makeText(this,"Invalid number", Toast.LENGTH_LONG).show()
            false
        }else{
            phoneLayout.error=null
            true
        }
    }

    private fun validatePassword():Boolean{
        password=passwordSignIn.text.toString()
        val passwordRegex= Pattern.compile(
            "^"+
                    "(?=.*[0-9])"+
                    "(?=.*[a-z])"+
                    "(?=.*[A-Z])"+
                    "(?=.*[@#$%^&+=])"+
                    "(?=\\S+$)"+
                    ".{4,}"+
                    "$"
        )
        return if(password.isEmpty()){
            passwordLayout.error="The field cannot be empty"
            Toast.makeText(this,"The field password cannot be empty", Toast.LENGTH_LONG).show()
            false
        }else if(!passwordRegex.matcher(password).matches()){
            passwordLayout.error="Insecure password"
            Toast.makeText(this,"Insecure password", Toast.LENGTH_LONG).show()
            false
        }else{
            passwordLayout.error=null
            true
        }
    }

    /*private fun validateRepeatPassword():Boolean{
        password=passwordSignIn.text.toString()
        password2=repeatPass.text.toString()
        return if(password.equals(password2)){
            repeatPassLayout.error="Passwords do not match"
            false
        }else{
            repeatPassLayout.error=null
            true
        }
    }*/
}