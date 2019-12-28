
#include <SoftwareSerial.h>
#include <MD_Parola.h>
#include <MD_MAX72xx.h>
#include <SPI.h>
#include "Parola_Fonts_data.h"

#define USE_UI_CONTROL 0

#define HARDWARE_TYPE MD_MAX72XX::ICSTATION_HW
#define MAX_DEVICES 8
#define CLK_PIN 13
#define DATA_PIN 11
#define CS_PIN 10
#define RX_PIN 0
#define TX_PIN 1

SoftwareSerial BTserial(RX_PIN, TX_PIN);
MD_Parola P = MD_Parola(HARDWARE_TYPE, DATA_PIN, CLK_PIN, CS_PIN, MAX_DEVICES);

uint8_t scrollSpeed = 30; // default frame delay value
textEffect_t scrollEffect = PA_SCROLL_LEFT;
textPosition_t scrollAlign = PA_LEFT;
uint16_t scrollPause = 100; // in milliseconds
textEffect_t textGrow = PA_GROW_UP;
textPosition_t growTextPosition = PA_CENTER;

// Global message buffers shared by Serial and Scrolling functions
#define BUF_SIZE 100
char curMessage[BUF_SIZE] = {"Booting..."};
char newMessage[BUF_SIZE] = {""};
bool newMessageAvailable = false;
String readString;
int repeat = 0;

//reading from Bluetooth serial, and copy readed chars to newMessage string
void readSerial(void)
{
  String readString;
  while (BTserial.available())
  {
    delay(100);
    char cp = (char)BTserial.read();
    readString += cp;
  }
  if (readString.length() > 0)
  {
    strcpy(newMessage, readString.c_str());
    newMessageAvailable = true;
    repeat = 0;
  }
  readString = "";
}

//setup the bluetooth serial and parola
void setup()
{
  Serial.begin(9600);
  BTserial.begin(9600);
  P.begin();
  P.setFont(ExtASCII);
  P.setIntensity(10);
}

// Read from serial, when new message available,
// display the text twice with grow_up or scrolling
// based on message length
void loop()
{
  readSerial();
  if (P.displayAnimate())
  {
    if (newMessageAvailable)
    {
      strcpy(curMessage, newMessage);
      if (repeat < 2)
      {
        if (strlen((char*)curMessage) > 10)
        {
          P.displayText(curMessage, scrollAlign, scrollSpeed, scrollPause, scrollEffect, scrollEffect);
        }
        else if (strlen((char*)curMessage) <= 10)
        {
          P.displayText(curMessage, growTextPosition, 30, 1000, textGrow, textGrow);
        }
        repeat++;
      } else {
        newMessageAvailable = false;
        P.displayReset();
        strcpy(curMessage, "");
      }
    }
  }
}
