import cv2
import pytesseract
import pyrebase
import json
import time

config = {
  "apiKey": "AIzaSyCeQ_CctxIHIAv6VEh6cY_hl5pRq7hBqx8",
  "authDomain": "LuminiPark",
  "databaseURL": "https://luminipark-d68e0-default-rtdb.firebaseio.com",
  "storageBucket": "gs://luminipark-d68e0.appspot.com"
}

firebase = pyrebase.initialize_app(config)

db = firebase.database()

placa = []
pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract'

# Inicializar la captura de video
cap = cv2.VideoCapture("videoPlacas.mp4")


text=""
while True:
    # Leer un cuadro de la cámara
    ret, image = cap.read()

    # Convertir a escala de grises y aplicar un desenfoque
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    gray = cv2.blur(gray, (3,3))

    # Aplicar detección de bordes y dilatación
    canny = cv2.Canny(gray, 150, 200)
    canny = cv2.dilate(canny, None, iterations=1)

    # Encontrar los contornos
    cnts, _ = cv2.findContours(canny, cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)

    for c in cnts:
        area = cv2.contourArea(c)
        x, y, w, h = cv2.boundingRect(c)
        epsilon = 0.09*cv2.arcLength(c, True)
        approx = cv2.approxPolyDP(c, epsilon, True)

        if len(approx) == 4 and area > 9000:
            print('area=', area)

            # Verificar si el ancho de la placa es mayor que su altura
            aspect_ratio = float(w)/h
            if aspect_ratio > 2.4:
                # Recortar la región de la placa y extraer el texto
                placa = gray[y:y+h, x:x+w]
                text = pytesseract.image_to_string(placa, config='--psm 11')
                print('PLACA: ', text)

                # Dibujar un rectángulo y agregar el texto
                cv2.rectangle(image, (x,y), (x+w,y+h), (0, 255, 0), 3)
                cv2.putText(image, text, (x-20, y-10), 1, 2.2, (0, 255, 0), 3)

    # Mostrar la imagen resultante
    cv2.imshow('Image', image)

    plate = text.strip()  # Clean the detected license plate text by removing leading/trailing white spaces

    users = db.child("Users").get()
    users_dict = json.loads(json.dumps(users.val()))

    plate_found = False
    door=True
    for user_id, user_data in users_dict.items():
        vehicles = user_data.get("Vehicle", {})
        for vehicle_id, vehicle_data in vehicles.items():
            registered_plate = vehicle_data.get("plateV", "")
            status_plate=vehicle_data.get("plateStatus",False)
            if registered_plate == plate and status_plate:
                plate_found = True
                door=False
                db.child("Users").child(user_id).update({"payStatus": False})
                db.child("Estacionamiento").child("Angelopolis").update({"Entrada1": True})
                time.sleep(10)
                
                break
        else:
            continue
        break
    else:
        print(f"The plate {plate} is not registered in the app")

    # Esperar una tecla y salir si se presiona 'q'
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Liberar la cámara y cerrar la ventana
cap.release()
cv2.destroyAllWindows()