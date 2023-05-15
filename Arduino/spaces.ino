#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include "FirebaseESP8266.h"
#include <ArduinoJson.h>

#define FIREBASE_HOST "https://luminipark-d68e0-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "otJojjEotz31T8Sx7R2wfYDqCUoYMtQcaidjiWzM"
#define WIFI_SSID "MEGACABLE-2.4G-DFA6"
#define WIFI_PASSWORD "NYFY4B8JRw"

#define IR_PIN1 D5
#define IR_PIN2 D6
#define IR_PIN3 D7

WiFiClient client; 
String firestatus="";
FirebaseData firebaseData;

int lastOccupiedSpaces = 0;

void setup() {
  Serial.begin(9600);
  WiFi.begin (WIFI_SSID, WIFI_PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
      
  Serial.println("");
  Serial.println("Se conectó al wifi!");
  Serial.println(WiFi.localIP());
    
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);

  delay(2000);
}

void loop() {
  Firebase.getInt(firebaseData, "/Estacionamiento/Angelopolis/Sections/A/Spaces");
  int currentSpaces = firebaseData.intData();
  
  int irValue1 = digitalRead(IR_PIN1);
  int irValue2 = digitalRead(IR_PIN2);
  int irValue3 = digitalRead(IR_PIN3);
  
  int occupiedSpaces = 0;
  if (irValue1 == LOW) occupiedSpaces++;
  if (irValue2 == LOW) occupiedSpaces++;
  if (irValue3 == LOW) occupiedSpaces++;
  
  int spacesChanged = occupiedSpaces - lastOccupiedSpaces;
  if (spacesChanged != 0) {
    if (currentSpaces >= spacesChanged) {
      Firebase.setInt(firebaseData, "/Estacionamiento/Angelopolis/Sections/A/Spaces", currentSpaces - spacesChanged);
      Serial.print("Espacios disponibles: ");
      Serial.println(currentSpaces - spacesChanged);
    } else {
      Serial.println("No hay suficientes espacios disponibles");
    }
  } else {
    Serial.println("No se detectaron cambios");
  }

  lastOccupiedSpaces = occupiedSpaces;
  delay(5000);
}
