#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include "FirebaseESP8266.h"
#include <ArduinoJson.h>

#include <Servo.h>


// Host servidor
#define FIREBASE_HOST "https://luminipark-d68e0-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "otJojjEotz31T8Sx7R2wfYDqCUoYMtQcaidjiWzM"
#define WIFI_SSID "MEGACABLE-2.4G-DFA6"
#define WIFI_PASSWORD "NYFY4B8JRw"

WiFiClient client; 
Servo servo;
String firestatus="";
FirebaseData firebaseData;

void setup() {
  
  Serial.begin(9600);
  WiFi.begin (WIFI_SSID, WIFI_PASSWORD);
    
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
      
  Serial.println ("");
  Serial.println ("Se conectó al wifi!");
  Serial.println(WiFi.localIP());
    
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);


  servo.attach(15); //D1
  servo.write(0);
  delay(2000);

}

void loop() {

  Firebase.getBool(firebaseData,"/Estacionamiento/Angelopolis/Entrada1");
  bool value=firebaseData.boolData();
  if(value==true){
    servo.write(180);
    delay(9000);
    servo.write(0);
    Firebase.setBool(firebaseData,"/Estacionamiento/Angelopolis/Entrada1",false);   
  }
  Serial.println (value);
    
}
