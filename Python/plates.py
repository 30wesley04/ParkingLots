import cv2
import numpy as np
import pytesseract
from PIL import Image

cap=cv2.VideoCapture("videoPlacas.mp4")

Ctexto=''

while True:
    ret,frame=cap.read()

    if ret==False:
        break

    cv2.rectangle(frame,(870,750),(1070,850),(0,0,0),cv2.FILLED)
    cv2.putText(frame,Ctexto[0:7],(900,810),cv2.FONT_HERSHEY_SIMPLEX,1,(0,255,0),2)

    al,an,c=frame.shape

    x1=int(an/3)
    x2=int(x1*2)

    y1=int(al/3)
    y2=int(y1*2)

    cv2.rectangle(frame,(x1+160,y1+500),(1120,940),(0,0,0),cv2.FILLED)
    cv2.putText(frame,'Procesando placa',(x1+180,y1+550),cv2.FONT_HERSHEY_SIMPLEX,1,(0,255,0),2)

    cv2.rectangle(frame,(x1,y1),(x2,y2),(0,255,0),2)

    recorte=frame[y1:y2,x1:x2]

    hsv = cv2.cvtColor(recorte, cv2.COLOR_BGR2HSV)
    lower_white = np.array([0, 0, 200])
    upper_white = np.array([179, 40, 255])
    mask = cv2.inRange(hsv, lower_white, upper_white)

    contornos,_=cv2.findContours(mask,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)

    contornos=sorted(contornos,key=lambda x:cv2.contourArea(x),reverse=True)

    for contorno in contornos:
        area=cv2.contourArea(contorno)
        if area>500 and area<5000:
            x,y,ancho,alto=cv2.boundingRect(contorno)

            xpi=x+x1
            ypi=y+y1

            xpf=x+ancho+x1
            ypf=y+alto+y1

            cv2.rectangle(frame,(xpi,ypi),(xpf,ypf),(255,255,0),2)

            placa=frame[ypi:ypf,xpi:xpf]

            alp,anp,cp=placa.shape

            Mva=np.zeros((alp,anp))

            nBp=np.matrix(placa[:,:,0])
            nGp=np.matrix(placa[:,:,1])
            nRp=np.matrix(placa[:,:,2])

            for col in range(0,alp):
                for fil in range(0,anp):
                    Max=max(nRp[col,fil],nGp[col,fil],nBp[col,fil])
                    Mva[col,fil]=255-Max


            _,bin=cv2.threshold(Mva,150,255,cv2.THRESH_BINARY)
            bin=bin.reshape(alp,anp)
            bin=Image.fromarray(bin)
            bin=bin.convert("L")

            if alp>=36 and anp>=82:
                pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR'

                texto = pytesseract.image_to_string(bin, lang='eng', config='--psm 11')

                texto = texto.replace('\n', '').replace('\r', '')
                texto = texto.strip()

                if texto.isalnum() and len(texto) == 7:
                    Ctexto = texto
                else:
                    Ctexto = 'Procesando...'

                cv2.imshow('frame',frame)

                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break

cap.release()
cv2.destroyAllWindows()
