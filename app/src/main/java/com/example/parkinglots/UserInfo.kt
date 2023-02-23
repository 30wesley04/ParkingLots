package com.example.parkinglots

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.parkinglots.uiload.LoadingDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserInfo : AppCompatActivity() {

    private lateinit var logOutButton: Button
    private lateinit var nameEdit: TextInputEditText
    private lateinit var lastEdit: TextInputEditText
    private lateinit var emailEdit: TextInputEditText
    private lateinit var phoneEdit: TextInputEditText

    private lateinit var lastEditLayout: TextInputLayout
    private lateinit var nameEditLayout: TextInputLayout
    private lateinit var emailEditLayout: TextInputLayout
    private lateinit var phoneEditLayout: TextInputLayout

    private lateinit var name:String
    private lateinit var lastName:String
    private lateinit var email:String
    private lateinit var phone:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        lastEditLayout=findViewById(R.id.lastEditLayout)
        nameEditLayout=findViewById(R.id.nameEditLayout)
        emailEditLayout=findViewById(R.id.emailEditLayout)
        phoneEditLayout=findViewById(R.id.phoneEditLayout)


        val db= FirebaseFirestore.getInstance()
        db.collection("Users").document(MainActivity.userEmail).get().addOnSuccessListener { documento->
            if(documento.exists()){
                nameEdit=findViewById(R.id.nameEdit)
                nameEdit.setText(documento.data?.get("Name").toString())

                lastEdit=findViewById(R.id.lastEdit)
                lastEdit.setText(documento.data?.get("lastName").toString())

                emailEdit=findViewById(R.id.emailEdit)
                emailEdit.setText(documento.data?.get("Email").toString())

                phoneEdit=findViewById(R.id.phoneEdit)
                phoneEdit.setText(documento.data?.get("Phone").toString())
            }else{
                Toast.makeText(this,"Algo salio mal", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun callLogOut(view: View){
        logOut()
    }

    private fun logOut(){

        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run() {
                loading.isDismiss()
            }

        },5000)
        MainActivity.userEmail

        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this,MainActivity::class.java))
    }

    fun access(view: View){
        accessMethod()
    }

    private fun accessMethod(){
        val intent= Intent(this,HomeActivity::class.java)
        startActivity(intent)
    }


    fun saveData(view: View){
        updateData()
    }

    private fun updateData(){
        name=nameEdit.text.toString()
        lastName=lastEdit.text.toString()
        email=emailEdit.text.toString()
        phone=phoneEdit.text.toString()

        if(name.isEmpty()){
            nameEditLayout.error="The field cannot be empty"

        }else{
            name=nameEdit.text.toString()
        }

        if(lastName.isEmpty()){
            lastEditLayout.error="The field cannot be empty"

        }else{
            lastName=lastEdit.text.toString()
        }

        if(email.isEmpty()){
            emailEditLayout.error="The field cannot be empty"

        }else{
            email=emailEdit.text.toString()
        }

        if(phone.isEmpty()){
            phoneEditLayout.error="The field cannot be empty"

        }else{
            phone=phoneEdit.text.toString()
        }

        var collection="Users"
        var dbUpdate= FirebaseFirestore.getInstance()
        dbUpdate.collection(collection).document(MainActivity.userEmail)
            .update("Name",name)

        dbUpdate.collection(collection).document(MainActivity.userEmail)
            .update("lastName",lastName)

        dbUpdate.collection(collection).document(MainActivity.userEmail)
            .update("Email",email)

        dbUpdate.collection(collection).document(MainActivity.userEmail)
            .update("Phone",phone)



        Toast.makeText(this,"Datos guardados correctamente", Toast.LENGTH_LONG).show()

    }
}