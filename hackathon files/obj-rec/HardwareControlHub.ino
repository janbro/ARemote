#include <Wire.h>
#include "rgb_lcd.h"

#define LED 2 //connect LED to digital pin2
String itemNames[] = {"Speaker", "TV", "Lamp", "Home Phone", "Lights", "Oven", "Coffee Maker", "Gaming Device"};
String incomingMessage;
int state = LOW;
bool tvState = true;

rgb_lcd lcd;

int colorRGB[] = {255,0,0};

void setup() {
  // initialize the digital pin2 as an output.
  Serial.begin(9600);
  pinMode(LED, OUTPUT);
  // set up the LCD's number of columns and rows:
    lcd.begin(16, 2);
    
    lcd.setRGB(colorRGB[0], colorRGB[1], colorRGB[2]);
    
    // Print a message to the LCD.
    lcd.print("hello, world!");

}

void loop() {
  // send data only when you receive data:
  incomingMessage = "";
  while (Serial.available() > 0) {
    delay(100);
    // read the incoming byte:
    char serByte = Serial.read();
    incomingMessage.concat((serByte));
    Serial.println("Ser:"+incomingMessage);
  }
  String uuid = getValue(incomingMessage,':',0);
  //Serial.println(uuid);
  String commnd = getValue(incomingMessage, ':', 1);
  int pin = uuid.toInt();
  
  switch(pin){
    case 1://TV
      if(commnd=="0"){
        lcd.setRGB(0,0,0);
        lcd.clear();
        lcd.noDisplay();
        tvState=false;
      }else if(commnd=="255"){
        lcd.display();
      lcd.setRGB(255,0,0);
      lcd.print("hello, world!");
      tvState=true;
      }else if(commnd=="+C"&&tvState){
        if(colorRGB[0]==255){
          colorRGB[0]=0;
          colorRGB[1]=255;
        }else if(colorRGB[1]==255){
          colorRGB[1]=0;
          colorRGB[2]=255;
        }else if(colorRGB[2]==255){
          colorRGB[2]=0;
          colorRGB[0]=255;
        }
        lcd.setRGB(colorRGB[0],colorRGB[1],colorRGB[2]);
      }else if(commnd=="-C"&&tvState){
        if(colorRGB[0]==255){
          colorRGB[0]=0;
          colorRGB[2]=255;
        }else if(colorRGB[1]==255){
          colorRGB[1]=0;
          colorRGB[0]=255;
        }else if(colorRGB[2]==255){
          colorRGB[2]=0;
          colorRGB[1]=255;
        }
        lcd.setRGB(colorRGB[0],colorRGB[1],colorRGB[2]);
      }
      break;
    case 3:
      digitalWrite(pin,commnd.toInt());
      Serial.println(commnd);
      break;
    case 2://oven
      if(commnd=="425"){
        digitalWrite(pin, HIGH);
        delay(100);
        digitalWrite(pin, LOW);
        delay(50);
        digitalWrite(pin, HIGH);
        delay(100);
        digitalWrite(pin, LOW);
        delay(50);
        digitalWrite(pin, HIGH);
        delay(100);
        digitalWrite(pin, LOW);
      }else if(commnd=="350"){
        digitalWrite(pin, HIGH);
        delay(100);
        digitalWrite(pin, LOW);
        delay(50);
        digitalWrite(pin, HIGH);
        delay(100);
        digitalWrite(pin, LOW);
      }
      break;
  }
  delay(1);               // for 500ms
}

String getValue(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {0, -1  };
  int maxIndex = data.length() - 1;
  for (int i = 0; i <= maxIndex && found <= index; i++) {
    if (data.charAt(i) == separator || i == maxIndex) {
      found++;
      strIndex[0] = strIndex[1] + 1;
      strIndex[1] = (i == maxIndex) ? i + 1 : i;
    }
  }
  return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}
